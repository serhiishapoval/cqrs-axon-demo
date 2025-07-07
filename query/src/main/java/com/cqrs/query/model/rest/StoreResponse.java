package com.cqrs.query.model.rest;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {

  private String id;
  private String name;
  private int year;
  private String country;
  private List<GadgetResponse> gadgets;
}
