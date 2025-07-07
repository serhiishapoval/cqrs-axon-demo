package com.cqrs.query.model.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GadgetResponse {

  private String id;
  private String name;
  private String type;
  private String color;
}
