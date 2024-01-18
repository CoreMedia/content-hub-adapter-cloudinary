package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

abstract class CloudinaryHubObject implements ContentHubObject {

  private final ContentHubObjectId hubId;

  CloudinaryHubObject(@NonNull ContentHubObjectId hubId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CloudinaryHubObject that = (CloudinaryHubObject) o;
    return Objects.equals(hubId, that.hubId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hubId);
  }
}
