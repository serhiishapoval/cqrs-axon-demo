package com.cqrs.command.model.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class UpdateStoreNameCommand {
  @TargetAggregateIdentifier private String id;
  private String name;
}
