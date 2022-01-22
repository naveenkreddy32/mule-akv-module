/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.pn.api;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.SELECT;
import org.mule.runtime.api.meta.ExpressionSupport;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.display.DisplayModel;

import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

import org.mule.metadata.api.ClassTypeLoader;
import org.mule.runtime.extension.api.declaration.type.ExtensionsTypeLoaderFactory;


/**
 * Declares extension for Secure Properties Configuration module
 *
 * @since 1.0
 */
public class AzureVaultConfigurationPropertiesExtensionLoadingDelegate implements ExtensionLoadingDelegate {

	public static final String EXTENSION_NAME = "AKV";
	public static final String CONFIG_ELEMENT = "config";
	public static final String AZUREVALUT_CLIENT_PARAMETER_GROUP = "Vault";

	@Override
	public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
		ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
				.describedAs(String.format("Crafted %s Extension", EXTENSION_NAME)).withCategory(SELECT)
				.onVersion("1.0.0").fromVendor("PN").withConfig(CONFIG_ELEMENT);

		addAzureVaultParameters(configurationDeclarer);
	}

	private void addAzureVaultParameters(ConfigurationDeclarer configurationDeclarer) {

		ParameterGroupDeclarer addAzureVaultParametersGroup = configurationDeclarer
				.onParameterGroup(AZUREVALUT_CLIENT_PARAMETER_GROUP).withDslInlineRepresentation(true);

		 ClassTypeLoader typeLoader =
		 ExtensionsTypeLoaderFactory.getDefault().createTypeLoader();

		addAzureVaultParametersGroup.withRequiredParameter("clientId")
				.withDisplayModel(DisplayModel.builder().displayName("Azure Application Client Id").build())
				.ofType(BaseTypeBuilder.create(JAVA).stringType().build())
				.withExpressionSupport(ExpressionSupport.SUPPORTED)
				.describedAs("Azure Application Client Id");

		addAzureVaultParametersGroup.withRequiredParameter("secretKey")
				.withDisplayModel(
						DisplayModel.builder().displayName("Azure Application Client Secret").build())
				.ofType(BaseTypeBuilder.create(JAVA).stringType().build())
				.withExpressionSupport(ExpressionSupport.SUPPORTED)
				.describedAs("Azure Application Secret Key");
		
		addAzureVaultParametersGroup.withRequiredParameter("vaultName")
				.withDisplayModel(DisplayModel.builder().displayName("Azure Vault Name").build())
				.ofType(BaseTypeBuilder.create(JAVA).stringType().build())
				.withExpressionSupport(ExpressionSupport.SUPPORTED).describedAs("Azure Vault Name");

	}

}
