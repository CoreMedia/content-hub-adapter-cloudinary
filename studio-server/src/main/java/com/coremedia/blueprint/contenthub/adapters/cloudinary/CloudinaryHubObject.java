package com.coremedia.blueprint.contenthub.adapters.cloudinary;


import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import edu.umd.cs.findbugs.annotations.NonNull;

abstract class CloudinaryHubObject implements ContentHubObject {

  protected final static String FOLDER_BASE_URL = "https://cloudinary.com/console/media_library/folders/";
  protected final static String ASSET_BASE_URL = "https://cloudinary.com/console/media_library/asset/";
  protected final static String ASSET_TYPE_IMAGES = "images";
  protected final static String ASSET_TYPE_AUDIO_VIDEO = "videos";
  protected final static String ASSET_TYPE_OTHER = "other";

  private ContentHubObjectId hubId;
  private ContentHubContext context;

  CloudinaryHubObject(ContentHubObjectId hubId, ContentHubContext context) {
    this.hubId = hubId;
    this.context = context;
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
