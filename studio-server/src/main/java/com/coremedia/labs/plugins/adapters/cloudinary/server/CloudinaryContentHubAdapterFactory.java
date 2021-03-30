package com.coremedia.labs.plugins.adapters.cloudinary.server;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 */
class CloudinaryContentHubAdapterFactory implements ContentHubAdapterFactory<CloudinaryContentHubSettings> {

  @Override
  @NonNull
  public String getId() {
    return "cloudinary";
  }

  @NonNull
  @Override
  public ContentHubAdapter createAdapter(@NonNull CloudinaryContentHubSettings settings,
                                         @NonNull String connectionId) {
    return new CloudinaryContentHubAdapter(settings, connectionId);
  }

}
