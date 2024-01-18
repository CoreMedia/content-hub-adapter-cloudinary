package com.coremedia.labs.plugins.adapters.cloudinary;

/**
 * Interface that marks the settings for the configuration of the CloudinaryAdapter.
 */
interface CloudinaryContentHubSettings {

  /**
   * @return the name
   */
  String getDisplayName();

  //--------------------# Cloudinary settings #--------------------//

  /**
   * @return name of Cloudinary cloud
   */
  String getCloudName();

  /**
   * @return apiKey
   */
  String getApiKey();

  /**
   * @return apiSecret
   */
  String getApiSecret();

  /**
   * Returns the API endpoint to use, e.g. https://api-eu.cloudinary.com, defaults to
   *
   * @return uploadPrefix.
   */
  String getUploadPrefix();

  Boolean isAdapterAssetIdModeEnabled();
}
