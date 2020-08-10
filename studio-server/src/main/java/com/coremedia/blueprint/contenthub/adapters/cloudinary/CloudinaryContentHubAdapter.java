package com.coremedia.blueprint.contenthub.adapters.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.coremedia.blueprint.contenthub.adapters.cloudinary.rest.CloudinaryAsset;
import com.coremedia.contenthub.api.*;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.pagination.PaginationRequest;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class CloudinaryContentHubAdapter implements ContentHubAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryContentHubAdapter.class);

  private final CloudinaryContentHubSettings settings;
  private final String connectionId;

  private final String cloudName;
  private final String apiKey;
  private final String apiSecret;

  private CloudinaryService cloudinaryService;

  CloudinaryContentHubAdapter(@NonNull CloudinaryContentHubSettings settings, @NonNull String connectionId) {
    this.settings = settings;
    this.connectionId = connectionId;
    this.apiKey = settings.getApiKey();
    this.cloudName = settings.getCloudName();
    this.apiSecret = settings.getApiSecret();


    if (apiKey == null || apiSecret == null || cloudName == null) {
      throw new ContentHubException("Invalid configuration for connector 'Cloudinary', ensure that apiKey, apiSecret and cloudName is set.");
    }

    this.cloudinaryService = new CloudinaryService(new Cloudinary(ObjectUtils.asMap(
            "cloud_name",
            cloudName,
            "api_key",
            apiKey,
            "api_secret",
            apiSecret)));
  }

  @NonNull
  @Override
  public Folder getRootFolder(@NonNull ContentHubContext context) throws ContentHubException {
    String displayName = settings.getDisplayName();
    ContentHubObjectId rootId = new ContentHubObjectId(connectionId, connectionId);
    return new CloudinaryFolder(cloudinaryService, context, rootId, displayName);
  }

  @Nullable
  @Override
  public CloudinaryItem getItem(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    String externalId = id.getExternalId();
    CloudinaryAsset asset = cloudinaryService.getAsset(externalId);
    if(asset != null) {
      return new CloudinaryItem(id, context, cloudinaryService, asset);
    }
    return null;
  }

  @Nullable
  @Override
  public Folder getFolder(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    return null;
  }

  @NonNull
  @Override
  public GetChildrenResult getChildren(@NonNull ContentHubContext context, @NonNull Folder folder, @Nullable PaginationRequest paginationRequest) {
    CloudinaryFolder realFolder = (CloudinaryFolder) folder;

    List<ContentHubObject> children = new ArrayList<>();

    if (realFolder.getCategory() == null){
      children.addAll(getRootFolders(context));
      children.addAll(getChildItems("/", context));
    }
    else {
      children.addAll(getSubFolders(realFolder, context));
      children.addAll(getChildItems(realFolder.getCategory().getPath(), context));

    }
    return new GetChildrenResult(children);
  }

  @Nullable
  @Override
  public Folder getParent(@NonNull ContentHubContext context, @NonNull ContentHubObject contentHubObject) throws ContentHubException {
    if (!contentHubObject.getId().equals(getRootFolder(context).getId())) {
      return getRootFolder(context);
    }
    return null;
  }

  @Override
  @NonNull
  public ContentHubTransformer transformer() {
    return new CloudinaryContentHubTransformer();
  }


  private List<ContentHubObject> getRootFolders(ContentHubContext context){
    return cloudinaryService.getRootFolders()
            .stream()
            .map(item -> {
              ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getPath());
              return new CloudinaryFolder(cloudinaryService, context, id, item, null);
            })
            .collect(Collectors.toList());
  }

  private List<ContentHubObject> getSubFolders(CloudinaryFolder parent, ContentHubContext context){
    return cloudinaryService.getSubFolders(parent.getCategory().getPath())
            .stream()
            .map(item -> {
              ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getPath());
              return new CloudinaryFolder(cloudinaryService, context, id, item, parent);
            })
            .collect(Collectors.toList());
  }


  private List<ContentHubObject> getChildItems(String path, ContentHubContext context) {
    return cloudinaryService.getAssets(path, true)
            .stream()
            .map(item -> {
              ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getId());
              return new CloudinaryItem(id, context, cloudinaryService, item);
            })
            .collect(Collectors.toList());
  }
}
