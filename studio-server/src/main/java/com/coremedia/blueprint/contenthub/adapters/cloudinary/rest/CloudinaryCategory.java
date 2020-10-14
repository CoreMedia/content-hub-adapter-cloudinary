package com.coremedia.blueprint.contenthub.adapters.cloudinary.rest;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class CloudinaryCategory {
  private String name;
  private String path;

  public CloudinaryCategory(Map folderData) {
    this.name = (String) folderData.get("name");
    this.path = (String) folderData.get("path");
  }

  public CloudinaryCategory(String path, String name) {
    this.name = name;
    this.path = path;
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

  public String getParentFolder() {
    if(path.contains("/")) {
      String parent = path;
      if(path.endsWith("/")) {
        path = path.substring(0, path.lastIndexOf("/"));
      }

      parent = path.substring(0, path.lastIndexOf('/'));
      return parent;
    }
    return null;
  }

  @Override
  public String toString() {
    return "CloudinaryCategory (" + path + ")";
  }
}
