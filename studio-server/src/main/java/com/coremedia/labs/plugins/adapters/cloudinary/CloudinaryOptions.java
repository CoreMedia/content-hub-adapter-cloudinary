package com.coremedia.labs.plugins.adapters.cloudinary;

public class CloudinaryOptions {
  private static final boolean DEFAULT_ASSET_ID_MODE_ENABLED = false;
  private static final String DEFAULT_SEARCH_QUERY = "filename=%1$s";
  private static final boolean DEFAULT_IMPORT_VIDEO_URL = false;
  private static final boolean DEFAULT_IMPORT_VIDEO_BLOB = true;
  private final boolean assetIdModeEnabled;
  private final String importPublicIdAs;
  private final String searchQuery;
  private final boolean importVideoURL;
  private final boolean importVideoBlob;
  private final Integer importImageMaxWidth;
  private final Integer importImageQuality;

  public CloudinaryOptions(CloudinaryContentHubSettings settings) {
    assetIdModeEnabled = getBooleanOption(settings.getAssetIdModeEnabled(), DEFAULT_ASSET_ID_MODE_ENABLED);
    importPublicIdAs = settings.getImportPublicIdAs();
    searchQuery = getStringOption(settings.getSearchQuery(), DEFAULT_SEARCH_QUERY);
    importVideoURL = getBooleanOption(settings.getImportVideoURL(), DEFAULT_IMPORT_VIDEO_URL);
    importVideoBlob = getBooleanOption(settings.getImportVideoBlob(), DEFAULT_IMPORT_VIDEO_BLOB);
    importImageMaxWidth = settings.getImportImageMaxWidth();
    importImageQuality = settings.getImportImageQuality();
  }

  public boolean isAssetIdModeEnabled() {
    return assetIdModeEnabled;
  }

  public String getImportPublicIdAs() {
    return importPublicIdAs;
  }

  public String getSearchQuery() {
    return searchQuery;
  }

  public boolean isImportVideoURL() {
    return importVideoURL;
  }

  public boolean isImportVideoBlob() {
    return importVideoBlob;
  }

  public Integer getImportImageMaxWidth() {
    return importImageMaxWidth;
  }

  public Integer getImportImageQuality() {
    return importImageQuality;
  }

  private boolean getBooleanOption(String settingValue, boolean defaultValue) {
    if(settingValue == null)
      return defaultValue;
    return Boolean.parseBoolean(settingValue);
  }

  private String getStringOption(String settingValue, String defaultValue) {
    if(settingValue == null)
      return defaultValue;
    return settingValue;
  }


  @Override
  public String toString() {
    return "CloudinaryOptions{" +
            "assetIdModeEnabled=" + assetIdModeEnabled +
            ", importPublicIdAs='" + importPublicIdAs + '\'' +
            ", searchQuery='" + searchQuery + '\'' +
            ", importVideoURL=" + importVideoURL +
            ", importVideoBlob=" + importVideoBlob +
            ", importImageMaxWidth=" + importImageMaxWidth +
            ", importImageQuality=" + importImageQuality +
            '}';
  }
}
