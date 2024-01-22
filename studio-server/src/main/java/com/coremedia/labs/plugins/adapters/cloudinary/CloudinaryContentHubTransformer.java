package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.cap.common.Blob;
import com.coremedia.contenthub.api.*;
import com.coremedia.cotopaxi.common.blobs.BlobServiceImpl;
import com.coremedia.mimetype.TikaMimeTypeService;
import com.coremedia.util.TempFileFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class CloudinaryContentHubTransformer implements ContentHubTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(CloudinaryContentHubTransformer.class);

  private final CloudinaryImportOptions importOptions;

  private TikaMimeTypeService tikaMimeTypeService;

  public CloudinaryContentHubTransformer(@NonNull CloudinaryImportOptions importOptions) {
    this.importOptions = importOptions;
  }

  @Override
  @NonNull
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    if (!(item instanceof CloudinaryItem)) {
      throw new IllegalArgumentException("Not my item: " + item);
    }
    return transformCloudinaryItem((CloudinaryItem) item);
  }

  @Override
  @Nullable
  public ContentModel resolveReference(ContentHubObject owner, ContentModelReference reference, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    String imageUrl = (String) reference.getData();
    String imageName = ContentCreationUtil.extractNameFromUrl(imageUrl);
    if (imageName == null) {
      return null;
    }
    ContentModel referenceModel = ContentModel.createReferenceModel(imageName, reference.getCoreMediaContentType());
    referenceModel.put("data", new UrlBlobBuilder(owner, "cloudinaryPicture").withUrl(imageUrl).withEtag().build());
    referenceModel.put("title", "Image " + imageName);

    return referenceModel;
  }


  // --- internal ---------------------------------------------------

  @NonNull
  private ContentModel transformCloudinaryItem(CloudinaryItem item) {
    String contentName = FilenameUtils.removeExtension(item.getName());
    ContentModel contentModel = ContentModel.createContentModel(contentName, item.getId(), item.getCoreMediaContentType());

    contentModel.put("title", contentName);
    String importPublicIdAs = importOptions.getImportPublicIdAs();
    if (importPublicIdAs != null && !importPublicIdAs.isEmpty())
      contentModel.put(importPublicIdAs, item.getAsset().getPublicId());

    String type = item.getCoreMediaContentType();
    Map<String, Object> additionalProps = new HashMap<>();

    if (type.equals("CMAudio") || type.equals("CMPicture") || type.equals("CMVideo") || type.equals("CMDownload")) {
      additionalProps.putAll(Objects.requireNonNull(getMediaProperties(item)));
    }

    // example how to populate a Richtext property
    // contentModel.put("detailText", ContentCreationUtil.convertStringToRichtext(description));

    //add additional properties
    additionalProps.forEach(contentModel::put);

    return contentModel;
  }

  @Nullable
  private Map<String, Object> getMediaProperties(CloudinaryItem item) {
    Map<String, Object> result = new HashMap<>();
    try {
      // handling for Videos import options
      String type = item.getCoreMediaContentType();
      // check if we are importing the blob
      if (!type.equals("CMVideo") || importOptions.isImportVideoBlob()) {
        InputStream stream = item.stream(true);
        BlobServiceImpl blobService = new BlobServiceImpl(new TempFileFactory(), getTika());
        Blob blob = blobService.fromInputStream(stream, getTika().getMimeTypeForResourceName(item.getName() + "." + item.getFormat()));
        result.put("data", blob);
        if (stream != null) {
          stream.close();
        }
      }
      // check if we are importing the URL
      if(type.equals("CMVideo") && importOptions.isImportVideoURL()) {
        result.put("dataUrl", item.getAsset().getSecureUrl());
      }
      return result;
    } catch (IOException | MimeTypeParseException e) {
      LOG.error("Failed to determine media properties", e);
    }
    return null;
  }

  private TikaMimeTypeService getTika() {
    if (tikaMimeTypeService == null) {
      this.tikaMimeTypeService = new TikaMimeTypeService();
      tikaMimeTypeService.init();
    }
    return tikaMimeTypeService;
  }
}
