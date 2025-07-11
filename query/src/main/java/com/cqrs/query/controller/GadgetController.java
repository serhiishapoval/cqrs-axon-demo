package com.cqrs.query.controller;

import com.cqrs.query.model.query.GetGadgetByIdQuery;
import com.cqrs.query.model.query.GetGadgetsByCountryQuery;
import com.cqrs.query.model.query.GetGadgetsByStoreIdQuery;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.GadgetToCountryResponse;
import com.cqrs.query.model.rest.StoreIdToGadgetResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gadget")
@RequiredArgsConstructor
public class GadgetController {

  private final QueryGateway queryGateway;

  @GetMapping("/id/{gadgetId}")
  public ResponseEntity<GadgetResponse> getGadgetById(@PathVariable final String gadgetId) {
    final GetGadgetByIdQuery getGadgetByIdQuery = new GetGadgetByIdQuery(gadgetId);

    return ResponseEntity.ok(
        this.queryGateway.query(getGadgetByIdQuery, GadgetResponse.class).join());
  }

  @GetMapping("/country/{country}")
  public ResponseEntity<List<GadgetToCountryResponse>> getGadgetsFromCountry(
      @PathVariable final String country) {
    final GetGadgetsByCountryQuery getGadgetsByCountryQuery = new GetGadgetsByCountryQuery(country);

    return ResponseEntity.ok(
        this.queryGateway
            .query(
                getGadgetsByCountryQuery,
                ResponseTypes.multipleInstancesOf(GadgetToCountryResponse.class))
            .join());
  }

  @GetMapping("/storeId/{storeId}")
  public ResponseEntity<List<StoreIdToGadgetResponse>> getGadgetsFromStore(
      @PathVariable final String storeId) {
    final GetGadgetsByStoreIdQuery getGadgetsByStoreIdQuery = new GetGadgetsByStoreIdQuery(storeId);

    return ResponseEntity.ok(
        this.queryGateway
            .query(
                getGadgetsByStoreIdQuery,
                ResponseTypes.multipleInstancesOf(StoreIdToGadgetResponse.class))
            .join());
  }
}
