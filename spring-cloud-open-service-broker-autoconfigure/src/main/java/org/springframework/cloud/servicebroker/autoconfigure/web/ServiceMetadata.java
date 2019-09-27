/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.servicebroker.autoconfigure.web.util.MetadataUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties that describes a service
 * offered by this broker.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">
 *     Service Metadata API Specification</a>
 *
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition
 */
public class ServiceMetadata {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceMetadata.class);

	private static final String DISPLAY_NAME_KEY = "displayName";

	private static final String IMAGE_URL_KEY = "imageUrl";

	private static final String LONG_DESCRIPTION_KEY = "longDescription";

	private static final String PROVIDER_DISPLAY_NAME_KEY = "providerDisplayName";

	private static final String DOCUMENTATION_URL_KEY = "documentationUrl";

	private static final String SUPPORT_URL_KEY = "supportUrl";

	private static final String IMAGE_DATA_FORMAT = "data:image/png;base64,%s";

	/**
	 * The name of the service to be displayed in graphical clients
	 */
	private String displayName;

	/**
	 * The URL to an image or a data URL containing an image
	 */
	private String imageUrl;

	/**
	 * The location of an image resource on the class path. This resource will be converted to a byte array, Base64
	 * encoded, and configured as the value of 'metadata.imageUrl' for the service offering.
	 */
	private String imageUrlResource;

	/**
	 * Long description
	 */
	private String longDescription;

	/**
	 * The name of the upstream entity providing the actual service
	 */
	private String providerDisplayName;

	/**
	 * Link to documentation page for the service
	 */
	private String documentationUrl;

	/**
	 * Link to support page for the service
	 */
	private String supportUrl;

	/**
	 * Additional properties used to describe the service
	 */
	private final Map<String, Object> properties = new HashMap<>();

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getImageUrlResource() {
		return imageUrlResource;
	}

	public void setImageUrlResource(String imageUrlResource) {
		this.imageUrlResource = imageUrlResource;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getProviderDisplayName() {
		return providerDisplayName;
	}

	public void setProviderDisplayName(String providerDisplayName) {
		this.providerDisplayName = providerDisplayName;
	}

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public String getSupportUrl() {
		return supportUrl;
	}

	public void setSupportUrl(String supportUrl) {
		this.supportUrl = supportUrl;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		if (!CollectionUtils.isEmpty(properties)) {
			this.properties.putAll(properties);
		}
	}

	/**
	 * Converts this object into its corresponding model
	 *
	 * @return a Map model
	 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition
	 */
	public Map<String, Object> toModel() {
		final HashMap<String, Object> model = new HashMap<>();
		if (StringUtils.hasText(this.displayName)) {
			model.put(DISPLAY_NAME_KEY, this.displayName);
		}
		if (StringUtils.hasText(this.imageUrl)) {
			model.put(IMAGE_URL_KEY, this.imageUrl);
		}
		else if (StringUtils.hasText(this.imageUrlResource)) {
			model.put(IMAGE_URL_KEY, base64EncodeImageData(this.imageUrlResource));
		}
		if (StringUtils.hasText(this.longDescription)) {
			model.put(LONG_DESCRIPTION_KEY, this.longDescription);
		}
		if (StringUtils.hasText(this.providerDisplayName)) {
			model.put(PROVIDER_DISPLAY_NAME_KEY, this.providerDisplayName);
		}
		if (StringUtils.hasText(this.documentationUrl)) {
			model.put(DOCUMENTATION_URL_KEY, this.documentationUrl);
		}
		if (StringUtils.hasText(this.supportUrl)) {
			model.put(SUPPORT_URL_KEY, this.supportUrl);
		}
		if (!CollectionUtils.isEmpty(this.properties)) {
			model.putAll(MetadataUtils.convertMap(this.properties));
		}
		return model;
	}

	private String base64EncodeImageData(String filename) {
		String formattedImageData = null;
		ClassPathResource resource = new ClassPathResource(filename);
		try(InputStream stream = resource.getInputStream()) {
			byte[] imageBytes = StreamUtils.copyToByteArray(stream);
			String imageData = Base64Utils.encodeToString(imageBytes);
			formattedImageData = String.format(IMAGE_DATA_FORMAT, imageData);
		}
		catch (IOException e) {
			LOG.warn("Error converting image file to byte array", e);
		}
		return formattedImageData;
	}

}
