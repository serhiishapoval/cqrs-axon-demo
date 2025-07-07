package com.cqrs.command.service.impl;

import com.cqrs.command.model.command.CreateStoreCommand;
import com.cqrs.command.model.command.GadgetCommand;
import com.cqrs.command.model.rest.StoreRequest;
import com.cqrs.command.service.ProcessNewStoreCreationService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessNewStoreCreationServiceImpl implements ProcessNewStoreCreationService {

  private final CommandGateway commandGateway;

  @Override
  public String processNewStore(final StoreRequest storeRequest) {
    final String storeId = UUID.randomUUID().toString();
    final List<GadgetCommand> gadgetCommands = new ArrayList<>();
    storeRequest.getGadgets().forEach(gadgetRequest -> {
      GadgetCommand gadgetCommand = GadgetCommand.builder()
          .id(UUID.randomUUID().toString())
          .name(gadgetRequest.getName())
          .type(gadgetRequest.getType())
          .color(gadgetRequest.getColor())
          .build();
      gadgetCommands.add(gadgetCommand);
    });

    final CreateStoreCommand createStoreCommand = CreateStoreCommand.builder()
        .id(storeId)
        .name(storeRequest.getName())
        .year(storeRequest.getYear())
        .country(storeRequest.getCountry())
        .gadgets(gadgetCommands)
        .build();

    this.commandGateway.sendAndWait(createStoreCommand);

    log.info("processNewStore. createStoreCommand: {}", createStoreCommand);
    return storeId;
  }

}
