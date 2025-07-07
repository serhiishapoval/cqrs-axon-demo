package com.cqrs.command.model.rest;

import java.util.List;
import lombok.Data;

@Data
public class StoreRequest {

  private String name;
  private int year;
  private String country;
  private List<GadgetRequest> gadgets;
}
