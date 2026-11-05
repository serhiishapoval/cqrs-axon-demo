package com.cqrs.query.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.cqrs.query.model.query.GetStoreByIdQuery;
import com.cqrs.query.model.rest.StoreResponse;
import java.util.concurrent.CompletableFuture;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

  @Mock private QueryGateway queryGateway;

  @InjectMocks private StoreController storeController;

  @Test
  void givenStoreId_whenGetStoreById_thenQueriesGatewayAndReturnsStore() {
    // given
    final StoreResponse store =
        StoreResponse.builder().id("store-1").name("Shop").year(2020).country("PL").build();
    doReturn(CompletableFuture.completedFuture(store))
        .when(this.queryGateway)
        .query(any(GetStoreByIdQuery.class), eq(StoreResponse.class));

    // when
    final ResponseEntity<StoreResponse> response = this.storeController.getStoreById("store-1");

    // then
    verify(this.queryGateway).query(new GetStoreByIdQuery("store-1"), StoreResponse.class);
    assertThat(response.getBody()).isEqualTo(store);
  }
}
