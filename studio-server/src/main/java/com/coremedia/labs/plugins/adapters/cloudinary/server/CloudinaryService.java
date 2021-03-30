package com.coremedia.labs.plugins.adapters.cloudinary.server;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.exceptions.NotFound;
import com.cloudinary.api.exceptions.RateLimited;
import com.cloudinary.utils.ObjectUtils;
import com.coremedia.labs.plugins.adapters.cloudinary.server.rest.CloudinaryAsset;
import com.coremedia.labs.plugins.adapters.cloudinary.server.rest.CloudinaryCategory;
import com.coremedia.contenthub.api.exception.ContentHubException;
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

  public CloudinaryService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  public List<CloudinaryCategory> getRootFolders() {
    List<CloudinaryCategory> result = new ArrayList<>();
    try {
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
      List<Map<String, Object>> folders = (List<Map<String, Object>>) cloudinary.api().subFolders(path, getDefaultOptions()).get("folders");
      for (Map<String, Object> folder : folders) {
        result.add(new CloudinaryCategory(folder));
      }
    } catch (Exception e) {
      LOG.error("Error loading Cloudinary subfolders: " + e.getMessage(), e);
    }
    return result;
  }

  public List<CloudinaryAsset> getAssets  (String path, boolean filter) {
    List<CloudinaryAsset> result = new ArrayList<>();
    try {
      List<Map<String, Object>> collectedItems = new ArrayList<>();
      List<Map<String, Object>> items = (List<Map<String, Object>>) cloudinary.api().resources(ObjectUtils.asMap("type", "upload", "prefix", path, "resource_type", "image")).get("resources");
      collectedItems.addAll(items);
      items = (List<Map<String, Object>>) cloudinary.api().resources(ObjectUtils.asMap("type", "upload", "prefix", path, "resource_type", "raw")).get("resources");
      collectedItems.addAll(items);
      items = (List<Map<String, Object>>) cloudinary.api().resources(ObjectUtils.asMap("type", "upload", "prefix", path, "resource_type", "video")).get("resources");
      collectedItems.addAll(items);

      for (Map collectedItem : collectedItems) {
        CloudinaryAsset asset = new CloudinaryAsset(collectedItem);

        //filter for direct children
        if(filter) {
          if (asset.isInFolder(path)) {
            result.add(asset);
          }
        }
        else {
          result.add(asset);
        }
      }
    }
    catch (RateLimited rle) {
      throw new ContentHubException(rle);
    }
    catch (Exception e) {
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

    return asset;
  }


  private Map<String, Object> getDefaultOptions() {
    Map<String, Object> options = new HashMap<>();
    options.put("return_error", true);
    return options;
  }

  private CloudinaryAsset getAsset(String externalId, String resourceType) {
    try {
      String id = URLEncoder.encode(externalId, StandardCharsets.UTF_8);
      Map<String, Object> resource = cloudinary.api().resource(id, ObjectUtils.asMap("resource_type", resourceType));
      return new CloudinaryAsset(resource);
    } catch (NotFound nf) {
      LOG.info("Cloudinary asset " + externalId + " not found as resource type '" + resourceType + "'");
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
      }
      else {
        return ent.getContent();
      }
    } catch (Exception e) {
      LOG.error("Couldn't connect to resource " + url + ": " + e.getMessage(), e);
    }
    return null;
  }

}
