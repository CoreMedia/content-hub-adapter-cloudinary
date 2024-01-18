package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryCategory;
import edu.umd.cs.findbugs.annotations.NonNull;

class CloudinaryFolder extends CloudinaryHubObject implements Folder {
  private final String name;
  private CloudinaryCategory category;
  private CloudinaryFolder parent;

  // only to be used for artificial root folder
  CloudinaryFolder(@NonNull ContentHubObjectId id, @NonNull String name) {
    super(id);
    this.name = name;
  }

  CloudinaryFolder(@NonNull ContentHubObjectId id, @NonNull CloudinaryCategory category) {
    this(id, category.getName());
    this.category = category;
  }

  CloudinaryFolder(@NonNull ContentHubObjectId id, @NonNull CloudinaryCategory category, @NonNull CloudinaryFolder parent) {
    this(id, category);
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
  @Override
  public String getName() {
    return name;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  public CloudinaryFolder getParent() {
    if (parent != null) {
      return parent;
    }
    if (category == null) {
      return null;
    }
    // construct parent from category path
    String connectionId = getId().getConnectionId();
    parent = getParentFromPath(category.getPath(), connectionId);
    return parent;
  }

  private CloudinaryFolder getParentFromPath(@NonNull String path, @NonNull String connectionId) {
    int lastPathDelimiter = path.lastIndexOf('/');
    // handle folders in root folder
    if (lastPathDelimiter == -1)
      return null;
    String parentPath = path.substring(0, lastPathDelimiter);
    lastPathDelimiter = parentPath.lastIndexOf('/');
    String parentName = (lastPathDelimiter == -1) ? parentPath : parentPath.substring(lastPathDelimiter + 1);
    ContentHubObjectId parentId = new ContentHubObjectId(connectionId, parentPath);
    CloudinaryCategory parentCloudinaryCategory = new CloudinaryCategory(parentName, parentPath);
    return new CloudinaryFolder(parentId, parentCloudinaryCategory);
  }

}
