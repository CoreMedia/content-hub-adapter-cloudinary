package com.coremedia.labs.plugins.adapters.cloudinary.server;

import com.coremedia.cap.common.Blob;
import com.coremedia.contenthub.api.ContentCreationUtil;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentModel;
import com.coremedia.contenthub.api.ContentModelReference;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.coremedia.cotopaxi.common.blobs.BlobServiceImpl;
import com.coremedia.labs.plugins.adapters.cloudinary.server.rest.CloudinaryAsset;
import com.coremedia.mimetype.TikaMimeTypeService;
import com.coremedia.util.TempFileFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.HtmlUtils;

import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class CloudinaryContentHubTransformer implements ContentHubTransformer {

  private TikaMimeTypeService tikaMimeTypeService;

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
    ContentModel contentModel = ContentModel.createContentModel(item.getName(), item.getId(), item.getCoreMediaContentType());
    contentModel.put("title", item.getName());
    String type = item.getCoreMediaContentType();

    Map<String, Object> additionalProps = new HashMap<>();

    if (type.equals("CMAudio") || type.equals("CMPicture") || type.equals("CMVideo") || type.equals("CMDownload")) {
      additionalProps.putAll(Objects.requireNonNull(getMediaProperties(item)));
    }

    String description = extractDescription(item.getAsset());
    if (description != null) {
      contentModel.put("detailText", ContentCreationUtil.convertStringToRichtext(description));
    }

    //add additional properties
    additionalProps.forEach(contentModel::put);

    return contentModel;
  }

  @Nullable
  private String extractDescription(@Nullable CloudinaryAsset asset) {
    return asset == null ? null : HtmlUtils.htmlUnescape(asset.getFolder());
  }

  @Nullable
  private Map<String, Object> getMediaProperties(CloudinaryItem item) {

    Map<String, Object> result = new HashMap<>();

    try {
      InputStream stream = item.stream();
      BlobServiceImpl blobService = new BlobServiceImpl(new TempFileFactory(), getTika());
      Blob blob = blobService.fromInputStream(stream, getTika().getMimeTypeForResourceName(item.getName() + "." + item.getFormat()));
      result.put("data", blob);
      if (stream != null) {
        stream.close();
      }
      return result;
    } catch (IOException | MimeTypeParseException e) {
      e.printStackTrace();
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
