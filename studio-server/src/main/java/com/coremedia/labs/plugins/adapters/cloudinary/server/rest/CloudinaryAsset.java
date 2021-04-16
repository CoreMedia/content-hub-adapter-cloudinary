package com.coremedia.labs.plugins.adapters.cloudinary.server.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class CloudinaryAsset {
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  private final String id;
  private final long size;
  private final String name;
  private final String type;
  private final String format;
  private final String resourceType;
  private final String url;
  private final String secureUrl;
  private Date lastModificationDate;
  private int width;
  private int height;
  private String folder;


  public CloudinaryAsset(Map<String, Object> data) {
    this.id = (String) data.get("public_id");
    this.type = (String) data.get("type");
    this.url = (String) data.get("url");
    this.secureUrl = (String) data.get("secure_url");
    this.resourceType = (String) data.get("resource_type");
    this.format = (String) data.get("format");
    this.size = (Integer) data.get("bytes");

    String[] split = id.split("/");
    this.name = split[split.length-1];
    this.folder = "";
    if(id.contains("/")) {
      this.folder = id.substring(0, id.lastIndexOf('/'));
    }

    String dateString = (String) data.get("created_at");
    try {
      this.lastModificationDate = new SimpleDateFormat(DATE_FORMAT).parse(dateString);
    } catch (ParseException e) {
      //ignore
    }

    if(data.containsKey("width")) {
      width = (int) data.get("width");
    }
    if(data.containsKey("height")) {
      height = (int) data.get("height");
    }
  }

  public String getFolder() {
    return folder;
  }

  public long getSize() {
    return size;
  }

  public String getName() {
    return name;
  }

  public Date getLastModificationDate() {
    return lastModificationDate;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getFormat() {
    return format;
  }

  public String getResourceType() {
    return resourceType;
  }

  public String getUrl() {
    return url;
  }

  public String getSecureUrl() {
    return secureUrl;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean isInFolder(String folder) {
    if(id.startsWith(folder)) {
      if(!folder.endsWith("/")) {
        folder = folder + "/";
      }

      String idSegment = id.substring(folder.length());
      return !idSegment.contains("/");
    }

    return false;
  }

  public String getConnectorItemType() {
    String format = getResourceType();
    if(format.equals("image")) {
      return "picture";
    }
    if(format.equals("video")) {
      return "video";
    }
    return "default";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CloudinaryAsset that = (CloudinaryAsset) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
