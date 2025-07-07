package com.cqrs.query.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@Configurable
public class SerializerConfiguration {

  @Bean
  public XStream xStream() {
    final XStream xStream = new XStream();

    // First, setup security
    xStream.addPermission(AnyTypePermission.ANY);

    // Setup default collections handling
    xStream.registerConverter(new com.thoughtworks.xstream.converters.collections.CollectionConverter(xStream.getMapper()));

    // Allow types explicitly
    xStream.allowTypesByWildcard(new String[]{
        "com.cqrs.dto.**",
        "com.cqrs.query.**",
        "java.util.*",
        "java.lang.*",
        "[L*"  // Array types
    });

    // Configure XStream to use Collection interfaces instead of implementations
    xStream.alias("list", java.util.List.class);
    xStream.alias("set", java.util.Set.class);
    xStream.alias("map", java.util.Map.class);

    return xStream;
  }

  @Bean
  @Primary
  public XStreamSerializer xStreamSerializer(final XStream xStream) {
    return XStreamSerializer.builder()
        .xStream(xStream)
        .lenientDeserialization()
        .build();
  }

  @Autowired
  public void configure(final EventProcessingConfigurer configurer) {
    configurer.usingTrackingEventProcessors();
  }


}
