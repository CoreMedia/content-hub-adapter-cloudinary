package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.common.util.WordAbbreviator;
import com.coremedia.contenthub.api.*;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryAsset;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

class CloudinaryItem extends BaseFileSystemItem implements Item {
  private static final Logger LOG = LoggerFactory.getLogger(CloudinaryItem.class);
  private static final WordAbbreviator ABBREVIATOR = new WordAbbreviator();
  private final CloudinaryService service;
  private final CloudinaryAsset asset;

  CloudinaryItem(@NonNull ContentHubObjectId id,
                 @NonNull CloudinaryService service,
                 @NonNull CloudinaryAsset asset,
                 @NonNull ContentHubMimeTypeService mimeTypeService,
                 @NonNull Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    super(id, asset.getName(), mimeTypeService, itemTypeToContentTypeMapping);
    this.service = service;
    this.asset = asset;
  }

  @NonNull
  @Override
  public String getName() {
    return asset.getName();
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public ContentHubType getContentHubType() {
    return Optional.ofNullable(getAsset())
            .map(CloudinaryAsset::getResourceType)
            .map(ContentHubType::new)
            .orElse(new ContentHubType("default"));
  }

  public String getFormat() {
    return asset.getFormat();
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    ContentHubBlob blob = getBlob("preview");

    return List.of(
            // Name & thumbnail
            new DetailsSection("main", List.of(
                    new DetailsElement<>(getName(), false, Objects.requireNonNullElse(blob, SHOW_TYPE_ICON))
            ), false, false, false),

            // Metadata
            new DetailsSection("metadata", List.of(
                    new DetailsElement<>("text", formatPreviewString(getDescription())),
                    new DetailsElement<>("size", formatSize(asset.getSize())),
                    new DetailsElement<>("lastModified", formatPreviewDate(getLastModified())),
                    new DetailsElement<>("link", formatPreviewString(asset.getSecureUrl()))
            ).stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList())));
  }

  @Nullable
  @Override
  public ContentHubBlob getThumbnailBlob() {
    return getBlob(ContentHubBlob.THUMBNAIL_BLOB_CLASSIFIER);
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    ContentHubBlob blob;
    try {
      String thumbnailUrl = service.getThumbnailUrl(asset);
      blob = new UrlBlobBuilder(this, classifier).withUrl(thumbnailUrl).build();
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot create blob for " + this, e);
    }
    return blob;
  }

  @Nullable
  private String formatPreviewString(@Nullable String str) {
    return str == null ? null : ABBREVIATOR.abbreviateString(str, 240);
  }

  private String formatSize(long sizeInBytes) {
    double sizeInKB = (double) sizeInBytes / 1024;
    DecimalFormat format = new DecimalFormat("#,###.##");
    return format.format(sizeInKB) + " KB";
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

  @Nullable
  public InputStream stream(boolean forImport) {
    try {
      return service.stream(asset, forImport);
    } catch (Exception e) {
      LOG.error("Failed to open resource input stream for " + asset.getName() + "/" + asset.getId() + ": " + e.getMessage(), e);
    }
    return null;
  }

  @NonNull
  public String getItemType() {
    return asset.getConnectorItemType();
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @Nullable
  public Date getLastModified() {
    return asset.getLastModificationDate();
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

}
