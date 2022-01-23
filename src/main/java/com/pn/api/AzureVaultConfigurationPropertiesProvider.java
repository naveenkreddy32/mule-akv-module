package com.pn.api;

import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class AzureVaultConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {

	Logger LOGGER = LogManager.getLogger(getClass());

	private final static String AZUREVAULT_DNS_PREFIX = ".vault.azure.net";

	private final static String HTTPS_PROTOCOL = "https://";

	private final static String AZUREVAULT_SECRET_PROPERTIES_PREFIX = "akv::";

	private final static Pattern AZURE_VAULT_SECRETS_PATTERN = Pattern
			.compile("\\$\\{" + AZUREVAULT_SECRET_PROPERTIES_PREFIX + "[^}]*}");

	private KeyVaultClient azureKeyVaultClient;

	private String vaultName;
	
	Cache AKVCache = AzureVaultEhCache.getInMemoryCache("AKVCache", String.class, String.class);

//	Cache<String, String> AKVCache = AzureVaultEhCache.buildInMemoryCache("propertyCache", String.class,
//			String.class);

	public AzureVaultConfigurationPropertiesProvider(KeyVaultClient azureKeyVaultClient, String vaultName) {
		this.azureKeyVaultClient = azureKeyVaultClient;
		this.vaultName = vaultName;
	}

	@Override
	public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
		LOGGER.trace("==============================" + configurationAttributeKey);
		String propertyKey = configurationAttributeKey;
		if (propertyKey.startsWith(AZUREVAULT_SECRET_PROPERTIES_PREFIX)) {
			final String propertyActualKey = propertyKey.substring(AZUREVAULT_SECRET_PROPERTIES_PREFIX.length());
			LOGGER.trace("==============================" + propertyActualKey);

			try {
				final String value;

				value = getSecretFromVault(propertyActualKey);

				if (value != null) {
					return Optional.of(new ConfigurationProperty() {

						@Override
						public Object getSource() {
							return "Azure Key Vault";
						}

						@Override
						public Object getRawValue() {
							return value;
						}

						@Override
						public String getKey() {
							return propertyActualKey;
						}
					});
				}
			} catch (Exception e) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

	@Override
	public String getDescription() {
		return "Azure Vault Property Provider";
	}

	private String getSecretFromVault(String key) {

		String secretValue = null;
		try {

			if (AKVCache.containsKey(key)) {

				secretValue = AKVCache.get(key).toString();
				LOGGER.debug("Found Key " + key + " in cache. Retrieved it");
				
			} else {
				SecretBundle secret = azureKeyVaultClient.getSecret(HTTPS_PROTOCOL + vaultName + AZUREVAULT_DNS_PREFIX,
						key);

				if (secret != null) {
					secretValue = secret.value();
					
					AKVCache.put(key, secretValue);
					LOGGER.debug("Key " + key + " not found in Cache. Retrieved from AKV and stored in cache");

				} else {
					LOGGER.error("secret key not found : " + key);
					throw new IllegalArgumentException("secret value not found for the key");

				}

			}

		} catch (Exception e) {

			LOGGER.error("Error Occured " + e.getMessage());

		}
		return secretValue;

	}
}