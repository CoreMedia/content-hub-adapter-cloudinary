package com.coremedia.labs.plugins.adapters.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Search;
import com.cloudinary.Transformation;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.api.exceptions.NotFound;
import com.cloudinary.api.exceptions.RateLimited;
import com.cloudinary.utils.ObjectUtils;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryAsset;
import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryCategory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CloudinaryService {
  private static final Logger LOG = LoggerFactory.getLogger(CloudinaryService.class);
  private final Cloudinary cloudinary;
  private final boolean assetIdModeEnabled;

  public CloudinaryService(Cloudinary cloudinary, boolean assetIdModeEnabled) {
    this.cloudinary = cloudinary;
    this.assetIdModeEnabled = assetIdModeEnabled;
  }

  public List<CloudinaryCategory> getRootFolders() {
    List<CloudinaryCategory> result = new ArrayList<>();
    try {
      if(LOG.isDebugEnabled())
        LOG.debug("Getting Cloudinary root folders");
      List<Map<String, Object>> folders = (List<Map<String, Object>>) cloudinary.api().rootFolders(getDefaultOptions()).get("folders");
      for (Map<String, Object> folder : folders) {
        result.add(new CloudinaryCategory(folder));
      }
    } catch (Exception e) {
      LOG.error("Error loading Cloudinary root folders: " + e.getMessage(), e);
    }
    return result;
  }

  public List<CloudinaryCategory> getSubFolders(String path) {
    List<CloudinaryCategory> result = new ArrayList<>();
    try {
      if(LOG.isDebugEnabled())
        LOG.debug("Getting Cloudinary subfolders with path='{}'", path);
      List<Map<String, Object>> folders = (List<Map<String, Object>>) cloudinary.api().subFolders(path, getDefaultOptions()).get("folders");
      for (Map<String, Object> folder : folders) {
        result.add(new CloudinaryCategory(folder));
      }
    } catch (Exception e) {
      LOG.error("Error loading Cloudinary subfolders: " + e.getMessage(), e);
    }
    return result;
  }

  public Search search() {
    return cloudinary.search();
  }

  public CloudinaryAssetsPage getAssets(@NonNull String path, boolean filter, @Nullable String pageCursor) {
    CloudinaryAssetsPage result = new CloudinaryAssetsPage();
    try {
      if(LOG.isDebugEnabled())
        LOG.debug("Getting Cloudinary assets with path='{}' and cursor='{}'", path, pageCursor);
      List<Map<String, Object>> collectedItems;
      String query = "(resource_type:image OR resource_type:video OR resource_type:raw) AND folder=\"" + path + "\"";
      Search search = cloudinary.search().expression(query);
      search.sortBy("filename", "asc");
      if (pageCursor != null) {
        search.nextCursor(pageCursor);
      }

      ApiResponse response = search.execute();
      result.setNextPageCursor((String) response.get("next_cursor"));
      result.setTotalCount((Integer) response.get("total_count"));
      collectedItems = (List<Map<String, Object>>) response.get("resources");

      for (Map<String, Object> collectedItem : collectedItems) {
        CloudinaryAsset asset = createCloudinaryAsset(collectedItem);
        //  filter for direct children
        if (!filter || asset.isInFolder(path))
          result.add(asset);
      }
    } catch (RateLimited rle) {
      throw new ContentHubException(rle);
    } catch (Exception e) {
      LOG.error("Error loading Cloudinary assets: " + e.getMessage(), e);
    }
    return result;
  }

  public CloudinaryAsset getAsset(String externalId) {
    CloudinaryAsset asset = getAsset(externalId, "image");
    if (asset == null) {
      asset = getAsset(externalId, "video");
    }
    if (asset == null) {
      asset = getAsset(externalId, "raw");
    }
    LOG.warn("Cloudinary asset " + externalId + " not found");
    return asset;
  }


  private Map<String, Object> getDefaultOptions() {
    Map<String, Object> options = new HashMap<>();
    options.put("return_error", true);
    return options;
  }

  private CloudinaryAsset getAsset(String externalId, String resourceType) {
    try {
      if(LOG.isDebugEnabled())
        LOG.debug("Getting Cloudinary asset with externalId='{}' and resourceType='{}'", externalId, resourceType);
      String id = URLEncoder.encode(externalId, StandardCharsets.UTF_8);
      Map<String, Object> resource = cloudinary.api().resource(id, ObjectUtils.asMap("resource_type", resourceType));
      return createCloudinaryAsset(resource);
    } catch (NotFound nf) {
      LOG.debug("Cloudinary asset " + externalId + " not found as resource type '" + resourceType + "'");
      return null;
    } catch (Exception e) {
      LOG.error("Error loading Cloudinary asset: " + e.getMessage(), e);
    }
    return null;
  }

  public InputStream stream(CloudinaryAsset asset) {
    String url = asset.getUrl();
    try {
      HttpGet httpGet = new HttpGet(url);
      CloseableHttpClient client = HttpClientBuilder.create().build();
      CloseableHttpResponse response = client.execute(httpGet);
      org.apache.http.HttpEntity ent = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode > 200) {
        String result = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
        LOG.error("Error getting Cloudinary resource: " + result);
      } else {
        return ent.getContent();
      }
    } catch (Exception e) {
      LOG.error("Couldn't connect to resource " + url + ": " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Get the thumbnail url for the given asset.
   *
   * @param asset Cloudinary asset
   * @return the thumbnail url
   */
  public String getThumbnailUrl(CloudinaryAsset asset) {
    String thumbnailUrl = cloudinary.url()
            .resourceType(asset.getResourceType())
            .transformation(new Transformation().width(400))
            .generate(asset.getPublicId());
    if ("video".equals(asset.getResourceType())) {
      thumbnailUrl = thumbnailUrl + ".jpg";
    }
    return thumbnailUrl;
  }

  public CloudinaryAsset createCloudinaryAsset(Map<String, Object> resource) {
    return new CloudinaryAsset(resource, assetIdModeEnabled);
  }
}
