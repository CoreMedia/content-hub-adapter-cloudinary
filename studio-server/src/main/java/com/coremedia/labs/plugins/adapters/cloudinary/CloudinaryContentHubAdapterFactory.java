package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.cap.common.BlobService;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubType;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;

/**
 *
 */
class CloudinaryContentHubAdapterFactory implements ContentHubAdapterFactory<CloudinaryContentHubSettings> {

  private final ContentHubMimeTypeService mimeTypeService;
  private final BlobService blobService;
  private final Map<ContentHubType, String> typeMapping;

  public CloudinaryContentHubAdapterFactory(ContentHubMimeTypeService mimeTypeService,
                                            BlobService blobService,
                                            Map<ContentHubType, String> typeMapping) {
    this.mimeTypeService = mimeTypeService;
    this.blobService = blobService;
    this.typeMapping = typeMapping;
  }

  @Override
  @NonNull
  public String getId() {
    return "cloudinary";
  }

  @NonNull
  @Override
  public ContentHubAdapter createAdapter(@NonNull CloudinaryContentHubSettings settings,
                                         @NonNull String connectionId) {
    return new CloudinaryContentHubAdapter(settings, connectionId, mimeTypeService, blobService, typeMapping);
  }

}
