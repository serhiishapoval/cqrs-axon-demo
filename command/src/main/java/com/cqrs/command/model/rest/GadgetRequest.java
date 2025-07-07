package com.cqrs.command.model.rest;

import lombok.Data;

@Data
public class GadgetRequest {

  private String name;
  private String type;
  private String color;
}
