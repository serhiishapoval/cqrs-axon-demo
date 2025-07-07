package com.cqrs.query.model.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GadgetToCountryResponse {

  private String country;
  private String gadgetId;
  private String name;
  private String type;
  private String color;
}
