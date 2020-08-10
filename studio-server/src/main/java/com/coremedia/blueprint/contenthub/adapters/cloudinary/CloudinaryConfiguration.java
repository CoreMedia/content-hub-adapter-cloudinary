package com.coremedia.blueprint.contenthub.adapters.cloudinary;

import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {
  @Bean
  public ContentHubAdapterFactory cloudinaryContentHubAdapterFactory() {
    return new CloudinaryContentHubAdapterFactory();
  }
}
