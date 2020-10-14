package com.coremedia.blueprint.contenthub.adapters.cloudinary;


import com.coremedia.blueprint.contenthub.adapters.cloudinary.rest.CloudinaryCategory;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.*;

class CloudinaryFolder extends CloudinaryHubObject implements Folder {

  private CloudinaryCategory category;
  private String name;
  private CloudinaryFolder parent;
  private List<CloudinaryItem> childItems = new ArrayList<>();
  private CloudinaryService service;

  CloudinaryFolder(CloudinaryService service, ContentHubContext context, ContentHubObjectId id, String name) {
    super(id, context);
    this.name = name;
    this.service = service;
  }

  CloudinaryFolder(CloudinaryService service, ContentHubContext context, ContentHubObjectId id, CloudinaryCategory category, @Nullable CloudinaryFolder parent) {
    super(id, context);
    this.service = service;
    this.category = category;
    this.parent = parent;
  }

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return new ContentHubType("cloudinary_folder");
  }

  public CloudinaryCategory getCategory() {
    return category;
  }

  @NonNull
  public List<CloudinaryItem> getItems() {
    return childItems;
  }


  @Nullable
  public Map<String, Object> getMetaData() {
    Map<String, Object> data = new HashMap<>();
    if (getCategory() != null) {
      data.put("path", "/" + getCategory().getPath());
    }
    else {
      data.put("path", "/");
    }
    return data;
  }

  @NonNull
  @Override
  public String getName() {
    if (name != null) {
      return name;
    }
    return category.getName();
  }

  @Nullable
  public CloudinaryFolder getParent() {
    return parent;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @Nullable
  public Date getLastModified() {
    if (category != null) {
      return category.getLastModified();
    }
    return null;
  }

  @Nullable
  public String getManagementUrl() {
    String url = FOLDER_BASE_URL + ASSET_TYPE_IMAGES;
    if (getCategory() != null) {
      url = url + "/" + getCategory().getPath();
    }
    return url;
  }

  public void setChildItems(List<CloudinaryItem> childItems) {
    this.childItems = childItems;
  }
}
