package com.pn.api;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;

public class AzureVaultConfigurationClientSecretKeyCredential extends KeyVaultCredentials {

	Logger LOGGER = LogManager.getLogger(getClass());

	private String applicationId;
	private String applicationSecret;

	Cache<String, String> accessTokenCache = AzureVaultEhCache.buildInMemoryCache("accessTokenCache", String.class, String.class);

	public AzureVaultConfigurationClientSecretKeyCredential(String applicationId, String applicationSecret) {
		this.setApplicationId(applicationId);
		this.setApplicationSecret(applicationSecret);
	}

	public String getApplicationId() {
		return applicationId;
	}

	private void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationSecret() {
		return applicationSecret;
	}

	private void setApplicationSecret(String applicationSecret) {
		this.applicationSecret = applicationSecret;
	}

	@Override
	public String doAuthenticate(String authorization, String resource, String scope) {
		AuthenticationResult res = null;
		String accessToken = null;

		try {

			if (!accessTokenCache.containsKey("accessToken")) {

				accessToken = GetAccessToken(authorization, resource, applicationId, applicationSecret).getAccessToken();
				accessTokenCache.put("accessToken", accessToken);
				
				LOGGER.debug("AKV access token stored in cache");

			} else {
				
				accessToken = accessTokenCache.get("accessToken").toString();
				
				LOGGER.debug("AKV access token retrived from cache");
			}

		} catch (InterruptedException e) {
			LOGGER.error("error occured while getting the accesstoken");
			e.printStackTrace();
		} catch (ExecutionException e) {
			LOGGER.error("execution exception while getting the accesstoken");
			e.printStackTrace();
		}
		return accessToken;
	}

	private AuthenticationResult GetAccessToken(String authorization, String resource, String clientID,
			String clientKey) throws InterruptedException, ExecutionException {
		AuthenticationContext ctx = null;
		ExecutorService service = Executors.newFixedThreadPool(1);
		try {
			ctx = new AuthenticationContext(authorization, false, service);
		} catch (MalformedURLException e) {
			LOGGER.error("Please check vault url and it is malformed");
			e.printStackTrace();
		}
		Future<AuthenticationResult> resp = ctx.acquireToken(resource, new ClientCredential(clientID, clientKey), null);
		AuthenticationResult res = resp.get();
		return res;
	}

}