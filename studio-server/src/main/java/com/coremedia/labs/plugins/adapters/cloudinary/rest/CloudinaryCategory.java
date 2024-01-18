package com.coremedia.labs.plugins.adapters.cloudinary.rest;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;

/**
 *
 */
public class CloudinaryCategory {
  private final String name;
  private final String path;

  public CloudinaryCategory(@NonNull String name, @NonNull String path) {
    this.name = name;
    this.path = path;
  }

  public CloudinaryCategory(@NonNull Map<String, Object> folderData) {
    this((String) folderData.get("name"), (String) folderData.get("path"));
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "CloudinaryCategory (" + path + ")";
  }
}
