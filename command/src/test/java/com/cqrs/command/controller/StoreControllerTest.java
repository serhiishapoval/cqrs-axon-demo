package com.cqrs.command.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cqrs.command.model.rest.StoreRequest;
import com.cqrs.command.model.rest.UpdateStoreNameRequest;
import com.cqrs.command.service.StoreProcessingService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

  @Mock private StoreProcessingService storeProcessingService;

  @InjectMocks private StoreController storeController;

  @Test
  void givenStoreRequest_whenCreateNewStore_thenReturnsCreatedStoreId() {
    // given
    final StoreRequest request = new StoreRequest();
    request.setName("Shop");
    when(this.storeProcessingService.createStore(request)).thenReturn("store-1");

    // when
    final ResponseEntity<String> response = this.storeController.createNewStore(request);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("store-1");
  }

  @Test
  void givenUpdateRequest_whenUpdateStoreName_thenDelegatesAndReturnsOk() {
    // given
    final UpdateStoreNameRequest request = new UpdateStoreNameRequest();
    request.setStoreId(UUID.randomUUID());
    request.setName("Renamed Shop");

    // when
    final ResponseEntity<Void> response = this.storeController.updateStoreName(request);

    // then
    verify(this.storeProcessingService).updateStoreName(request);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
