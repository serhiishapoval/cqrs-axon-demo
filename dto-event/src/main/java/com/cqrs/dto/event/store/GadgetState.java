package com.cqrs.dto.event.store;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GadgetState {

  private String id;
  private String name;
  private String type;
  private String color;
}
