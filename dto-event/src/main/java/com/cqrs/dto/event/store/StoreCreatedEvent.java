package com.cqrs.dto.event.store;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreCreatedEvent {

  private String id;
  private String name;
  private int year;
  private String country;
  private List<GadgetState> gadgets;
}
