package com.coremedia.labs.plugins.adapters.cloudinary.server;

import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {
  @Bean
  public ContentHubAdapterFactory<?> cloudinaryContentHubAdapterFactory() {
    return new CloudinaryContentHubAdapterFactory();
  }
}
