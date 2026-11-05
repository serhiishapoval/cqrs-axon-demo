package com.cqrs.command.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.cqrs.command.model.command.CreateStoreCommand;
import com.cqrs.command.model.command.GadgetCommand;
import com.cqrs.command.model.command.UpdateStoreNameCommand;
import com.cqrs.command.model.rest.GadgetRequest;
import com.cqrs.command.model.rest.StoreRequest;
import com.cqrs.command.model.rest.UpdateStoreNameRequest;
import java.util.List;
import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreProcessingServiceImplTest {

  @Mock private CommandGateway commandGateway;

  @InjectMocks private StoreProcessingServiceImpl storeProcessingService;

  @Test
  void givenStoreRequest_whenCreateStore_thenSendsCreateStoreCommandAndReturnsStoreId() {
    // given
    final GadgetRequest gadgetRequest = new GadgetRequest();
    gadgetRequest.setName("phone");
    gadgetRequest.setType("mobile");
    gadgetRequest.setColor("black");
    final StoreRequest storeRequest = new StoreRequest();
    storeRequest.setName("Shop");
    storeRequest.setYear(2020);
    storeRequest.setCountry("PL");
    storeRequest.setGadgets(List.of(gadgetRequest));
    final ArgumentCaptor<CreateStoreCommand> captor =
        ArgumentCaptor.forClass(CreateStoreCommand.class);

    // when
    final String storeId = this.storeProcessingService.createStore(storeRequest);

    // then
    verify(this.commandGateway).sendAndWait(captor.capture());
    final CreateStoreCommand sent = captor.getValue();
    assertThat(storeId).isEqualTo(sent.getId());
    final GadgetCommand sentGadget = sent.getGadgets().get(0);
    final CreateStoreCommand expected =
        CreateStoreCommand.builder()
            .id(storeId)
            .name("Shop")
            .year(2020)
            .country("PL")
            .gadgets(
                List.of(
                    GadgetCommand.builder()
                        .id(sentGadget.getId())
                        .name("phone")
                        .type("mobile")
                        .color("black")
                        .build()))
            .build();
    assertThat(sent).isEqualTo(expected);
    assertThat(UUID.fromString(sentGadget.getId())).isNotNull();
  }

  @Test
  void givenUpdateStoreNameRequest_whenUpdateStoreName_thenSendsUpdateStoreNameCommand() {
    // given
    final UUID storeId = UUID.randomUUID();
    final UpdateStoreNameRequest request = new UpdateStoreNameRequest();
    request.setStoreId(storeId);
    request.setName("Renamed Shop");
    final ArgumentCaptor<UpdateStoreNameCommand> captor =
        ArgumentCaptor.forClass(UpdateStoreNameCommand.class);

    // when
    this.storeProcessingService.updateStoreName(request);

    // then
    verify(this.commandGateway).sendAndWait(captor.capture());
    final UpdateStoreNameCommand expected =
        UpdateStoreNameCommand.builder().id(storeId.toString()).name("Renamed Shop").build();
    assertThat(captor.getValue()).isEqualTo(expected);
  }
}
