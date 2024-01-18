package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.labs.plugins.adapters.cloudinary.rest.CloudinaryAsset;

import java.util.ArrayList;

public class CloudinaryAssetsPage extends ArrayList<CloudinaryAsset> {

  private String nextPageCursor;
  private int totalCount;

  public String getNextPageCursor() {
    return nextPageCursor;
  }

  public void setNextPageCursor(String nextPageCursor) {
    this.nextPageCursor = nextPageCursor;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }
}
