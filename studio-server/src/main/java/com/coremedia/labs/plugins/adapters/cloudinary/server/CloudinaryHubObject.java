package com.coremedia.labs.plugins.adapters.cloudinary.server;


import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import edu.umd.cs.findbugs.annotations.NonNull;

abstract class CloudinaryHubObject implements ContentHubObject {

  private final ContentHubObjectId hubId;

  CloudinaryHubObject(ContentHubObjectId hubId, ContentHubContext context) {
    this.hubId = hubId;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @NonNull
  @Override
  public ContentHubObjectId getId() {
    return hubId;
  }

}
