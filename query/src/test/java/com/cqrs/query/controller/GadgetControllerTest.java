package com.cqrs.query.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.cqrs.query.model.query.GetGadgetByIdQuery;
import com.cqrs.query.model.query.GetGadgetsByCountryQuery;
import com.cqrs.query.model.query.GetGadgetsByStoreIdQuery;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.GadgetToCountryResponse;
import com.cqrs.query.model.rest.StoreIdToGadgetResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GadgetControllerTest {

  @Mock private QueryGateway queryGateway;

  @InjectMocks private GadgetController gadgetController;

  @Test
  void givenGadgetId_whenGetGadgetById_thenQueriesGatewayAndReturnsGadget() {
    // given
    final GadgetResponse gadget =
        GadgetResponse.builder().id("gadget-1").name("phone").type("mobile").color("black").build();
    doReturn(CompletableFuture.completedFuture(gadget))
        .when(this.queryGateway)
        .query(any(GetGadgetByIdQuery.class), any(ResponseType.class));

    // when
    final ResponseEntity<GadgetResponse> response = this.gadgetController.getGadgetById("gadget-1");

    // then
    verify(this.queryGateway)
        .query(eq(new GetGadgetByIdQuery("gadget-1")), any(ResponseType.class));
    assertThat(response.getBody()).isEqualTo(gadget);
  }

  @Test
  void givenCountry_whenGetGadgetsFromCountry_thenQueriesGatewayAndReturnsGadgets() {
    // given
    final List<GadgetToCountryResponse> gadgets =
        List.of(GadgetToCountryResponse.builder().country("PL").gadgetId("gadget-1").build());
    doReturn(CompletableFuture.completedFuture(gadgets))
        .when(this.queryGateway)
        .query(any(GetGadgetsByCountryQuery.class), any(ResponseType.class));

    // when
    final ResponseEntity<List<GadgetToCountryResponse>> response =
        this.gadgetController.getGadgetsFromCountry("PL");

    // then
    verify(this.queryGateway)
        .query(eq(new GetGadgetsByCountryQuery("PL")), any(ResponseType.class));
    assertThat(response.getBody()).isEqualTo(gadgets);
  }

  @Test
  void givenStoreId_whenGetGadgetsFromStore_thenQueriesGatewayAndReturnsGadgets() {
    // given
    final List<StoreIdToGadgetResponse> gadgets =
        List.of(StoreIdToGadgetResponse.builder().storeId("store-1").gadgetId("gadget-1").build());
    doReturn(CompletableFuture.completedFuture(gadgets))
        .when(this.queryGateway)
        .query(any(GetGadgetsByStoreIdQuery.class), any(ResponseType.class));

    // when
    final ResponseEntity<List<StoreIdToGadgetResponse>> response =
        this.gadgetController.getGadgetsFromStore("store-1");

    // then
    verify(this.queryGateway)
        .query(eq(new GetGadgetsByStoreIdQuery("store-1")), any(ResponseType.class));
    assertThat(response.getBody()).isEqualTo(gadgets);
  }
}
