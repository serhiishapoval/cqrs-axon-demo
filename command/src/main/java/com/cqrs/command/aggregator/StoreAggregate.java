package com.cqrs.command.aggregator;

import com.cqrs.command.model.command.CreateStoreCommand;
import com.cqrs.dto.event.store.GadgetState;
import com.cqrs.dto.event.store.StoreCreatedEvent;
import java.util.ArrayList;
import java.util.List;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class StoreAggregate {

  @AggregateIdentifier
  private String id;
  private String name;
  private int year;
  private String country;
  private List<GadgetState> gadgets = new ArrayList<>();

  public StoreAggregate() {
    // Required by Axon
  }

  @CommandHandler
  public StoreAggregate(final CreateStoreCommand command) {
    this.validateStore(command);

    final List<GadgetState> gadgetStates = new ArrayList<>();
    command.getGadgets().forEach(gadget -> {
      GadgetState gadgetState = GadgetState.builder()
          .id(gadget.getId())
          .name(gadget.getName())
          .type(gadget.getType())
          .color(gadget.getColor())
          .build();
      gadgetStates.add(gadgetState);
    });

    final StoreCreatedEvent storeEvent = StoreCreatedEvent.builder()
        .id(command.getId())
        .name(command.getName())
        .year(command.getYear())
        .country(command.getCountry())
        .gadgets(gadgetStates)
        .build();

    AggregateLifecycle.apply(storeEvent);
  }

  private void validateStore(final CreateStoreCommand command) {
    if (command.getName() == null || command.getName().isBlank()) {
      throw new IllegalArgumentException("Store name cannot be empty");
    }
    if (command.getYear() <= 0) {
      throw new IllegalArgumentException("Invalid year");
    }
    if (command.getGadgets() == null || command.getGadgets().isEmpty()) {
      throw new IllegalArgumentException("Store must have at least one gadget");
    }
  }

  @EventSourcingHandler
  public void on(final StoreCreatedEvent event) {
    this.id = event.getId();
    this.name = event.getName();
    this.year = event.getYear();
    this.country = event.getCountry();
    this.gadgets = new ArrayList<>(event.getGadgets());
  }


}
