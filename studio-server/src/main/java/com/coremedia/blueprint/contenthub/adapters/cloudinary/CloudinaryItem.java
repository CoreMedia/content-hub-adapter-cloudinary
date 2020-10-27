package com.coremedia.blueprint.contenthub.adapters.cloudinary;


import com.coremedia.blueprint.contenthub.adapters.cloudinary.rest.CloudinaryAsset;
import com.coremedia.common.util.WordAbbreviator;
import com.coremedia.contenthub.api.*;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.coremedia.mimetype.TikaMimeTypeService;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

class CloudinaryItem extends CloudinaryHubObject implements Item {
  private static final WordAbbreviator ABBREVIATOR = new WordAbbreviator();
  private static final int BLOB_SIZE_LIMIT = 10000000;
  private CloudinaryService service;
  private CloudinaryAsset asset;

  private TikaMimeTypeService tikaservice;

  private static final Logger LOG = LoggerFactory.getLogger(CloudinaryItem.class);


  CloudinaryItem(ContentHubObjectId id, ContentHubContext context, CloudinaryService service, CloudinaryAsset asset) {
    super(id, context);
    this.service = service;
    this.asset = asset;
    initTika();
  }

  private void initTika() {
    if (tikaservice == null) {
      this.tikaservice = new TikaMimeTypeService();
      tikaservice.init();
    }
  }


  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return new ContentHubType("cloudinary_file");
  }

  @NonNull
  @Override
  public String getName() {
    return getDisplayName();
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
  }

  @NonNull
  @Override
  public String getCoreMediaContentType() {
    if (tikaservice != null) {
      String type = asset.getResourceType();

      if (type.equalsIgnoreCase("image"))
        return "CMPicture";
      else if (type.equalsIgnoreCase("audio"))
        return "CMAudio";
      else if (type.equalsIgnoreCase("video"))
        return "CMVideo";
      else if (type.equalsIgnoreCase("raw"))
        return "CMDownload";
      else
        return "CMArticle";
    }
    else
      return "CMArticle";
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    return List.of(
            new DetailsSection("main", List.of(
                    new DetailsElement<>(getName(), false, SHOW_TYPE_ICON)
            ), false, false, false),
            new DetailsSection("metadata", List.of(
                    new DetailsElement<>("text", formatPreviewString(getDescription())),
                    new DetailsElement<>("lastModified", formatPreviewDate(getLastModified())),
                    new DetailsElement<>("link", formatPreviewString(asset.getSecureUrl()))
            ).stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList())));
  }


  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {

    return null;
  }

  @Nullable
  private String formatPreviewString(@Nullable String str) {
    return str == null ? null : ABBREVIATOR.abbreviateString(str, 240);
  }

  @Nullable
  private Calendar formatPreviewDate(@Nullable Date date) {
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }



  public long getSize() {
    return asset.getSize();
  }

  @Nullable
  public InputStream stream() {
    try {
      return service.stream(asset);
    } catch (Exception e) {
      LOG.error("Failed to open resource input stream for " + asset.getName() + "/" + asset.getId() + ": " + e.getMessage(), e);
    }
    return null;
  }

  @Nullable
  public Map<String, Object> getMetaData() {
    Map<String, Object> data = new HashMap<>();

    data.put("height", asset.getHeight());
    data.put("width", asset.getWidth());
    data.put("folder", asset.getFolder());
    data.put("format", asset.getFormat());

    return data;
  }

  @NonNull
  public String getItemType() {
    return asset.getConnectorItemType();
  }

  public String getMimeType() {
    return asset.getMimeType();
  }

  public boolean isDownloadable() {
    return true;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    String itemType = getItemType();
    if (itemType.equals("default")) {
      return getName();
    }
    return asset.getName();
  }

  @Nullable
  public Date getLastModified() {
    return asset.getLastModificationDate();
  }

  @Nullable
  public String getManagementUrl() {
    try {
      String resourceType = asset.getResourceType();
      return ASSET_BASE_URL + resourceType + "/upload/" + URLEncoder.encode(getId().getExternalId(), "utf8");
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CloudinaryItem)) {
      return false;
    }
    CloudinaryItem that = (CloudinaryItem) o;
    return asset.equals(that.asset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(asset);
  }

  public CloudinaryAsset getAsset() {
    return asset;
  }

  public CloudinaryService getService() {
    return service;
  }
}
