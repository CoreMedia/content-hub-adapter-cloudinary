package com.coremedia.labs.plugins.adapters.cloudinary.server;


import com.coremedia.labs.plugins.adapters.cloudinary.server.rest.CloudinaryCategory;
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

  CloudinaryFolder(ContentHubContext context, ContentHubObjectId id, String name) {
    super(id, context);
    this.name = name;
  }

  CloudinaryFolder(ContentHubContext context, ContentHubObjectId id, CloudinaryCategory category, @Nullable CloudinaryFolder parent) {
    super(id, context);
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

  public void setChildItems(List<CloudinaryItem> childItems) {
    this.childItems = childItems;
  }
}
