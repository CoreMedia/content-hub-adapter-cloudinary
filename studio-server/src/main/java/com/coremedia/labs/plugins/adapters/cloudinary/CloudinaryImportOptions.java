package com.coremedia.labs.plugins.adapters.cloudinary;

public class CloudinaryImportOptions {
  private static final boolean DEFAULT_ASSET_ID_MODE_ENABLED = false;
  private static final boolean DEFAULT_IMPORT_VIDEO_URL = false;
  private static final boolean DEFAULT_IMPORT_VIDEO_BLOB = true;
  private final boolean assetIdModeEnabled;
  private final String importPublicIdAs;
  private final boolean importVideoURL;
  private final boolean importVideoBlob;
  private final Integer importImageMaxWidth;
  private final Integer importImageQuality;

  public CloudinaryImportOptions(CloudinaryContentHubSettings settings) {
    assetIdModeEnabled = getBooleanOption(settings.getAssetIdModeEnabled(), DEFAULT_ASSET_ID_MODE_ENABLED);
    importPublicIdAs = settings.getImportPublicIdAs();
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

  @Override
  public String toString() {
    return "CloudinaryImportOptions{" +
            "assetIdModeEnabled=" + assetIdModeEnabled +
            ", importPublicIdAs='" + importPublicIdAs + '\'' +
            ", importVideoURL=" + importVideoURL +
            ", importVideoBlob=" + importVideoBlob +
            ", importImageMaxWidth=" + importImageMaxWidth +
            ", importImageQuality=" + importImageQuality +
            '}';
  }
}
