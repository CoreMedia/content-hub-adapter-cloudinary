package com.coremedia.labs.plugins.adapters.cloudinary;

import com.coremedia.cap.common.BlobService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cms.common.plugins.beans_for_plugins_container.CommonBeansForPluginsContainer;
import com.coremedia.contenthub.api.BaseFileSystemConfiguration;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.cotopaxi.common.blobs.BlobServiceImpl;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.util.TempFileFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

@Configuration
@Import({BaseFileSystemConfiguration.class})
public class CloudinaryConfiguration implements DisposableBean {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Nullable
  private TempFileFactory tempFileFactory;

  private static Map<ContentHubType, String> typeMapping() {
    return Map.of(
            new ContentHubType("default"), "CMDownload",
            new ContentHubType("audio"), "CMAudio",
            new ContentHubType("css", new ContentHubType("text")), "CMCSS",
            new ContentHubType("html", new ContentHubType("text")), "CMHTML",
            new ContentHubType("javascript", new ContentHubType("text")), "CMJavaScript",
            new ContentHubType("image"), "CMPicture",
            new ContentHubType("video"), "CMVideo",
            new ContentHubType("msword", new ContentHubType("application")), "CMArticle",
            new ContentHubType("vnd.openxmlformats-officedocument.wordprocessingml.document", new ContentHubType("application")), "CMArticle"
    );
  }

  @Bean
  public ContentHubAdapterFactory<?> cloudinaryContentHubAdapterFactory(@NonNull ContentHubMimeTypeService mimeTypeService,
                                                                        BlobService blobService) {
    return new CloudinaryContentHubAdapterFactory(mimeTypeService, blobService, typeMapping());
  }

  @Bean
  public BlobService contentHubMimeTypeService(ObjectProvider<CommonBeansForPluginsContainer> commonBeansForPluginsContainer,
                                               MimeTypeService mimeTypeService) {
    return commonBeansForPluginsContainer.stream()
            .map(CommonBeansForPluginsContainer::getConnection)
            .map(CapConnection::getBlobService)
            .findFirst()
            .orElseGet(() -> createBlobService(mimeTypeService));
  }

  private BlobService createBlobService(MimeTypeService mimeTypeService) {
    LOG.warn("Creating fallback BlobService for Cloudinary adapter because CapConnection is not available. Please check your plugin configuration.");
    tempFileFactory = new TempFileFactory();
    return new BlobServiceImpl(tempFileFactory, mimeTypeService);
  }

  @Override
  public void destroy() {
    if (tempFileFactory != null) {
      tempFileFactory.destroy();
    }
  }
}
