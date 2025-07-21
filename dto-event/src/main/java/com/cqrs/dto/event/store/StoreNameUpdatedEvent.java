package com.cqrs.dto.event.store;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreNameUpdatedEvent {
  private String id;
  private String name;
}
