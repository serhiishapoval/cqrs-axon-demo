package com.cqrs.command.model.command;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateStoreCommand {

  @TargetAggregateIdentifier
  private String id;
  private String name;
  private int year;
  private String country;
  private List<GadgetCommand> gadgets;
}
