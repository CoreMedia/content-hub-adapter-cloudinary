package com.coremedia.labs.plugins.adapters.cloudinary.server;

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

    ContentHubMimeTypeService mimeTypeService;
    private final Map<ContentHubType, String> typeMapping;

    public CloudinaryContentHubAdapterFactory(ContentHubMimeTypeService mimeTypeService,
                                              Map<ContentHubType, String> typeMapping) {
        this.mimeTypeService = mimeTypeService;
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
        return new CloudinaryContentHubAdapter(settings, connectionId, mimeTypeService, typeMapping);
    }

}
