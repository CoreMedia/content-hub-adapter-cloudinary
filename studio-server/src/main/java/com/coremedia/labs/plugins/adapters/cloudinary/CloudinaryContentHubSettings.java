package com.coremedia.labs.plugins.adapters.cloudinary;

/**
 * Interface that marks the settings for the configuration of the CloudinaryAdapter.
 */
public interface CloudinaryContentHubSettings {

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

  /**
   * Returns whether a Cloudinary's items asset_id should be used/stored as externalRefId.
   * Defaults to false, and the public_id is stored for backwards compatibility.
   * Once configured, the configuration should not be changed.
   *
   * @return
   */
  String getAssetIdModeEnabled();

  /**
   * Returns the name of the property a Cloudinary's items public_id should be stored as.
   * Defaults to null. Useful in combination with assetIdModeEnabled to still store the public_id in CM.
   *
   * @return
   */
  String getImportPublicIdAs();

  /**
   * Returns the Cloudinary query to use for search.
   * Use "%1$s" to refer to the query term entered by the user.
   * Defaults to "filename=%1$s"
   */
  String getSearchQuery();

  /**
   * Returns whether the blob of a Video should be imported.
   * Defaults to true for backwards compatibility.
   * @return
   */
  String getImportVideoBlob();

  /**
   * Returns whether the URL of a Video should be imported.
   * Defaults to false for backwards compatibility.
   * @return
   */
  String getImportVideoURL();

  /**
   * Returns whether images should be scaled down to a maximum width upon import.
   */
  Integer getImportImageMaxWidth();

  /**
   * Returns whether image quality should be reduced upon import.
   */
  Integer getImportImageQuality();

}
