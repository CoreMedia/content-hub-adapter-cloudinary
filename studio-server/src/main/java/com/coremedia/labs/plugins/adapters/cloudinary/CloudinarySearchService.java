package com.coremedia.labs.plugins.adapters.cloudinary;

import com.cloudinary.Search;
import com.cloudinary.api.ApiResponse;
import com.coremedia.contenthub.api.*;
import com.coremedia.contenthub.api.search.ContentHubSearchResult;
import com.coremedia.contenthub.api.search.ContentHubSearchService;
import com.coremedia.contenthub.api.search.Sort;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CloudinarySearchService implements ContentHubSearchService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CloudinarySearchService.class);

  private static final Logger LOG = LoggerFactory.getLogger(CloudinarySearchService.class);
  private final CloudinaryService cloudinaryService;
  private final CloudinaryOptions cloudinaryOptions;
  private final String connectionId;
  private final ContentHubMimeTypeService mimeTypeService;
  private final Map<ContentHubType, String> itemTypeToContentTypeMapping;

  public CloudinarySearchService(CloudinaryService cloudinaryService,
                                 CloudinaryOptions cloudinaryOptions,
                                 String connectionId,
                                 ContentHubMimeTypeService mimeTypeService,
                                 Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    this.cloudinaryService = cloudinaryService;
    this.cloudinaryOptions = cloudinaryOptions;
    this.connectionId = connectionId;
    this.mimeTypeService = mimeTypeService;
    this.itemTypeToContentTypeMapping = itemTypeToContentTypeMapping;
  }

  @Override
  public ContentHubSearchResult search(String query, @Nullable Folder belowFolder, @Nullable ContentHubType type,
                                       Collection<String> filterQueries,
                                       List<Sort> sortCriteria, int limit) {
    if (query.isEmpty()) {
      return new ContentHubSearchResult(Collections.emptyList());
    }
    Search search = cloudinaryService.search();
    try {
      String searchExpression = String.format(cloudinaryOptions.getSearchQuery(), query);
      if (LOG.isDebugEnabled())
        LOG.debug("Searching Cloudinary assets with expression='{}'", searchExpression);
      search.expression(searchExpression);
      ApiResponse response = search.execute();
      if (!response.containsKey("resources")) {
        return new ContentHubSearchResult(Collections.emptyList());
      }
      List<Map> resourceList = (List) response.get("resources");
      List<ContentHubObject> assets = resourceList
              .stream()
              .map(cloudinaryService::createCloudinaryAsset)
              .map(asset -> new CloudinaryItem(new ContentHubObjectId(connectionId, asset.getId()), cloudinaryService, asset, mimeTypeService, itemTypeToContentTypeMapping))
              .collect(Collectors.toList());
      return new ContentHubSearchResult(assets);
    } catch (Exception ex) {
      LOGGER.error("Encountered error when executing Cloudinary search: " + ex.getMessage(), ex);

    }
    return new ContentHubSearchResult(Collections.emptyList());
  }

  @Override
  public Collection<ContentHubType> supportedTypes() {
    return List.of(new ContentHubType("cloudinary_file"));
  }

  @Override
  public Set<Sort> supportedSortCriteria() {
    return ContentHubSearchService.super.supportedSortCriteria();
  }

  @Override
  public boolean supportsSearchBelowFolder() {
    return false;
  }

  @Override
  public int supportedLimit() {
    return 100;
  }
}
