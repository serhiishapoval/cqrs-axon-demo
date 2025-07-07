package com.cqrs.command.model.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GadgetCommand {
  private String id;
  private String name;
  private String type;
  private String color;
}
