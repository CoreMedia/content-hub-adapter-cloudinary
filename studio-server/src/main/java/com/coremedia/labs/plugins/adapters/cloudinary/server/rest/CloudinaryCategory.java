package com.coremedia.labs.plugins.adapters.cloudinary.server.rest;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class CloudinaryCategory {
  private final String name;
  private final String path;

  public CloudinaryCategory(Map<String, Object> folderData) {
    this.name = (String) folderData.get("name");
    this.path = (String) folderData.get("path");
  }

  public String getName() {
    return name;
  }

  public Date getLastModified() {
    return null;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "CloudinaryCategory (" + path + ")";
  }
}
