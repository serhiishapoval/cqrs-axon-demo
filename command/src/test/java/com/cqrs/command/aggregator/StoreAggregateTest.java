package com.cqrs.command.aggregator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cqrs.command.model.command.CreateStoreCommand;
import com.cqrs.command.model.command.GadgetCommand;
import com.cqrs.command.model.command.UpdateStoreNameCommand;
import com.cqrs.dto.event.store.GadgetState;
import com.cqrs.dto.event.store.StoreCreatedEvent;
import com.cqrs.dto.event.store.StoreNameUpdatedEvent;
import java.util.List;
import java.util.stream.Stream;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StoreAggregateTest {

  private static final String STORE_ID = "store-1";
  private static final String GADGET_ID = "gadget-1";

  private static GadgetCommand gadgetCommand() {
    return GadgetCommand.builder()
        .id(GADGET_ID)
        .name("phone")
        .type("mobile")
        .color("black")
        .build();
  }

  private static CreateStoreCommand createCommand(
      final String name, final int year, final List<GadgetCommand> gadgets) {
    return CreateStoreCommand.builder()
        .id(STORE_ID)
        .name(name)
        .year(year)
        .country("PL")
        .gadgets(gadgets)
        .build();
  }

  static Stream<Arguments> invalidCreateCommands() {
    final List<GadgetCommand> gadgets = List.of(gadgetCommand());
    return Stream.of(
        Arguments.of(createCommand(null, 2020, gadgets), "Store name cannot be empty"),
        Arguments.of(createCommand("  ", 2020, gadgets), "Store name cannot be empty"),
        Arguments.of(createCommand("Shop", 0, gadgets), "Invalid year"),
        Arguments.of(createCommand("Shop", -1, gadgets), "Invalid year"),
        Arguments.of(createCommand("Shop", 2020, null), "Store must have at least one gadget"),
        Arguments.of(
            createCommand("Shop", 2020, List.of()), "Store must have at least one gadget"));
  }

  @ParameterizedTest(name = "invalid create command is rejected with: {1}")
  @MethodSource("invalidCreateCommands")
  void givenInvalidCreateCommand_whenCreatingAggregate_thenThrowsIllegalArgumentException(
      final CreateStoreCommand command, final String expectedMessage) {
    // given

    // when / then
    assertThatThrownBy(() -> new StoreAggregate(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(expectedMessage);
  }

  @Test
  void givenValidCreateCommand_whenHandled_thenPublishesStoreCreatedEvent() {
    // given
    final CreateStoreCommand command = createCommand("Shop", 2020, List.of(gadgetCommand()));
    final StoreCreatedEvent expected =
        StoreCreatedEvent.builder()
            .id(STORE_ID)
            .name("Shop")
            .year(2020)
            .country("PL")
            .gadgets(
                List.of(
                    GadgetState.builder()
                        .id(GADGET_ID)
                        .name("phone")
                        .type("mobile")
                        .color("black")
                        .build()))
            .build();

    // when / then
    new AggregateTestFixture<>(StoreAggregate.class)
        .givenNoPriorActivity()
        .when(command)
        .expectEvents(expected);
  }

  static Stream<Arguments> invalidUpdateCommands() {
    return Stream.of(
        Arguments.of(
            UpdateStoreNameCommand.builder().id(null).name("Shop").build(),
            "Store id cannot be empty"),
        Arguments.of(
            UpdateStoreNameCommand.builder().id("  ").name("Shop").build(),
            "Store id cannot be empty"),
        Arguments.of(
            UpdateStoreNameCommand.builder().id(STORE_ID).name(null).build(),
            "Store name cannot be empty"),
        Arguments.of(
            UpdateStoreNameCommand.builder().id(STORE_ID).name(" ").build(),
            "Store name cannot be empty"));
  }

  @ParameterizedTest(name = "invalid update command is rejected with: {1}")
  @MethodSource("invalidUpdateCommands")
  void givenInvalidUpdateCommand_whenHandled_thenThrowsIllegalArgumentException(
      final UpdateStoreNameCommand command, final String expectedMessage) {
    // given
    final StoreAggregate aggregate = new StoreAggregate();

    // when / then
    assertThatThrownBy(() -> aggregate.handle(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(expectedMessage);
  }

  @Test
  void givenExistingStore_whenUpdateStoreName_thenPublishesStoreNameUpdatedEvent() {
    // given
    final StoreCreatedEvent created =
        StoreCreatedEvent.builder()
            .id(STORE_ID)
            .name("Shop")
            .year(2020)
            .country("PL")
            .gadgets(List.of())
            .build();
    final UpdateStoreNameCommand command =
        UpdateStoreNameCommand.builder().id(STORE_ID).name("Renamed Shop").build();
    final StoreNameUpdatedEvent expected =
        StoreNameUpdatedEvent.builder().id(STORE_ID).name("Renamed Shop").build();

    // when / then
    new AggregateTestFixture<>(StoreAggregate.class)
        .given(created)
        .when(command)
        .expectEvents(expected);
  }
}
