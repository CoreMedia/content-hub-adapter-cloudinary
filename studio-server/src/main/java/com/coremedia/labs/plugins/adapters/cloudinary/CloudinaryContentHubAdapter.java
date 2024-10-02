package com.coremedia.labs.plugins.adapters.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.coremedia.cap.common.BlobService;
import com.coremedia.contenthub.api.*;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.pagination.PaginationRequest;
import com.coremedia.contenthub.api.pagination.PaginationResponse;
import com.coremedia.contenthub.api.search.ContentHubSearchService;
import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryAsset;
import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryCategory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class CloudinaryContentHubAdapter implements ContentHubAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(CloudinaryContentHubTransformer.class);

  private final CloudinaryContentHubSettings settings;
  private final String connectionId;
  private final ContentHubMimeTypeService mimeTypeService;
  private final BlobService blobService;
  private final Map<ContentHubType, String> itemTypeToContentTypeMapping;

  private final CloudinaryOptions cloudinaryOptions;
  private final CloudinaryService cloudinaryService;

  CloudinaryContentHubAdapter(@NonNull CloudinaryContentHubSettings settings,
                              @NonNull String connectionId,
                              @NonNull ContentHubMimeTypeService mimeTypeService,
                              @NonNull BlobService blobService,
                              @NonNull Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    this.settings = settings;
    this.connectionId = connectionId;
    this.mimeTypeService = mimeTypeService;
    this.blobService = blobService;
    this.itemTypeToContentTypeMapping = itemTypeToContentTypeMapping;

    String apiKey = settings.getApiKey();
    String cloudName = settings.getCloudName();
    String apiSecret = settings.getApiSecret();
    String uploadPrefix = settings.getUploadPrefix();
    // parse configuration
    cloudinaryOptions = new CloudinaryOptions(settings);
    LOG.info("Starting CloudinaryContentHubAdapter with " + cloudinaryOptions);

    if (apiKey == null || apiSecret == null || cloudName == null) {
      throw new ContentHubException("Invalid configuration for connector 'Cloudinary', ensure that apiKey, apiSecret and cloudName is set.");
    }

    Map<String, String> cloudinaryConfig = ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret);
    if (uploadPrefix != null)
      cloudinaryConfig.put("upload_prefix", uploadPrefix);
    this.cloudinaryService = new CloudinaryService(new Cloudinary(cloudinaryConfig), cloudinaryOptions);
  }

  @NonNull
  @Override
  public Folder getRootFolder(@NonNull ContentHubContext context) throws ContentHubException {
    String displayName = settings.getDisplayName();
    ContentHubObjectId rootId = new ContentHubObjectId(connectionId, connectionId);
    return new CloudinaryFolder(rootId, displayName);
  }

  @Nullable
  @Override
  public CloudinaryItem getItem(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    String externalId = id.getExternalId();
    CloudinaryAsset asset = cloudinaryService.getAsset(externalId);
    if (asset != null) {
      return new CloudinaryItem(id, cloudinaryService, asset, mimeTypeService, itemTypeToContentTypeMapping);
    }
    return null;
  }

  @Nullable
  @Override
  public Folder getFolder(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    // handling for CMS-23354
    Folder rootFolder = getRootFolder(context);
    if (rootFolder.getId().equals(id)) {
      return rootFolder;
    }
    // it seems this method is being called when the "reload" button is pressed for a folder
    String externalId = id.getExternalId();
    int lastPathDelimiter = externalId.lastIndexOf('/');
    String path = externalId;
    // handle root folders
    String name = ((lastPathDelimiter == -1)) ? externalId : externalId.substring(lastPathDelimiter + 1);
    ContentHubObjectId parentId = new ContentHubObjectId(connectionId, path);
    CloudinaryCategory parentCloudinaryCategory = new CloudinaryCategory(name, path);
    return new CloudinaryFolder(parentId, parentCloudinaryCategory);
  }

  @NonNull
  @Override
  public GetChildrenResult getChildren(@NonNull ContentHubContext context, @NonNull Folder folder, @Nullable PaginationRequest paginationRequest) {
    CloudinaryFolder realFolder = (CloudinaryFolder) folder;
    List<ContentHubObject> children = new ArrayList<>();
    GetChildrenResult assets;
    String cursor = (paginationRequest != null) ? paginationRequest.getNextPageCursor() : null;
    List<ContentHubObject> subFolders;
    // handle request for root
    if (realFolder.getCategory() == null) {
      subFolders = getRootFolders(context);
      assets = getChildItems("", context, cursor);
      // handle request for other folders
    } else {
      subFolders = getSubFolders(realFolder, context);
      assets = getChildItems(realFolder.getCategory().getPath(), context, cursor);
    }
    // only return subfolders for initial request
    if(paginationRequest == null)
      children.addAll(subFolders);
    children.addAll(assets.getChildren());
    PaginationResponse assetsPaginationResponse = assets.getPaginationResponse();
    int totalCount = subFolders.size() + assetsPaginationResponse.getTotalCount();
    PaginationResponse paginationResponse = new PaginationResponse(assetsPaginationResponse.isExistMore(), totalCount, assetsPaginationResponse.getNextPageCursor());
    return new GetChildrenResult(children, paginationResponse);
  }

  @Nullable
  @Override
  public Folder getParent(@NonNull ContentHubContext context, @NonNull ContentHubObject contentHubObject) throws ContentHubException {
    // handle folders
    if (contentHubObject instanceof CloudinaryFolder) {
      CloudinaryFolder cloudinaryFolder = (CloudinaryFolder) contentHubObject;
      // handle artificial root folder
      if (cloudinaryFolder.getId().equals(getRootFolder(context).getId())) {
        return null;
      }
      // handle root folders
      CloudinaryFolder parentFolder = cloudinaryFolder.getParent();
      return (parentFolder != null) ? parentFolder : getRootFolder(context);
    }
    // handle items
    if (contentHubObject instanceof CloudinaryItem) {
      CloudinaryItem cloudinaryItem = (CloudinaryItem) contentHubObject;
      return getParentForItem(cloudinaryItem, context);
    }
    return null;
  }

  private Folder getParentForItem(@NonNull CloudinaryItem item, @NonNull ContentHubContext context) {
    String path = item.getAsset().getPublicId();
    int lastPathDelimiter = path.lastIndexOf('/');
    // handle folders/items in root folder
    if (lastPathDelimiter == -1)
      return getRootFolder(context);
    String parentPath = path.substring(0, lastPathDelimiter);
    lastPathDelimiter = parentPath.lastIndexOf('/');
    String parentName = (lastPathDelimiter == -1) ? parentPath : parentPath.substring(lastPathDelimiter + 1);
    ContentHubObjectId parentId = new ContentHubObjectId(connectionId, parentPath);
    CloudinaryCategory parentCloudinaryCategory = new CloudinaryCategory(parentName, parentPath);
    return new CloudinaryFolder(parentId, parentCloudinaryCategory);
  }

  @Override
  @NonNull
  public ContentHubTransformer transformer() {
    return new CloudinaryContentHubTransformer(cloudinaryOptions, blobService, mimeTypeService);
  }

  private List<ContentHubObject> getRootFolders(ContentHubContext context) {
    CloudinaryFolder rootFolder = (CloudinaryFolder) getRootFolder(context);
    return cloudinaryService.getRootFolders()
            .stream()
            .map(item -> {
              ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getPath());
              return new CloudinaryFolder(id, item, rootFolder);
            })
            .collect(Collectors.toList());
  }

  private List<ContentHubObject> getSubFolders(@NonNull CloudinaryFolder parent, @NonNull ContentHubContext context) {
    return cloudinaryService.getSubFolders(parent.getCategory().getPath())
            .stream()
            .map(item -> {
              ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getPath());
              return new CloudinaryFolder(id, item, parent);
            })
            .collect(Collectors.toList());
  }


  private GetChildrenResult getChildItems(@NonNull String path, @NonNull ContentHubContext context, @Nullable String pageCursor) {
    CloudinaryAssetsPage assetsPage = cloudinaryService.getAssets(path, true, pageCursor);
    List<ContentHubObject> assetsList = assetsPage.stream()
            .map(item -> {
              ContentHubObjectId id = new ContentHubObjectId(connectionId, item.getId());
              return new CloudinaryItem(id, cloudinaryService, item, mimeTypeService, itemTypeToContentTypeMapping);
            })
            .collect(Collectors.toList());
    PaginationResponse response = new PaginationResponse(assetsPage.getNextPageCursor() != null, assetsPage.getTotalCount(), assetsPage.getNextPageCursor());
    return new GetChildrenResult(assetsList, response);
  }

  @Override
  public Optional<ContentHubSearchService> searchService() {
    return Optional.of(new CloudinarySearchService(cloudinaryService, cloudinaryOptions,
            connectionId, mimeTypeService, itemTypeToContentTypeMapping));
  }

}
