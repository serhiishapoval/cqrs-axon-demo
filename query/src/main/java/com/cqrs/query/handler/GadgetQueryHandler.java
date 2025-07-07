package com.cqrs.query.handler;

import com.cqrs.query.model.query.GetGadgetByIdQuery;
import com.cqrs.query.model.query.GetGadgetsByCountryQuery;
import com.cqrs.query.model.query.GetGadgetsByStoreIdQuery;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.GadgetToCountryResponse;
import com.cqrs.query.model.rest.StoreIdToGadgetResponse;
import com.cqrs.query.service.GadgetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GadgetQueryHandler {

  private final GadgetService gadgetService;

  @QueryHandler
  public GadgetResponse getGadgetById(final GetGadgetByIdQuery getGadgetByIdQuery) {
    return this.gadgetService.getGadget(getGadgetByIdQuery.getGadgetId());
  }

  @QueryHandler
  public List<GadgetToCountryResponse> getGadgetsFromCountry(
      final GetGadgetsByCountryQuery getGadgetsByCountryQuery) {
    return this.gadgetService.getGadgetsFromCountry(getGadgetsByCountryQuery.getCountry());
  }

  @QueryHandler
  public List<StoreIdToGadgetResponse> getGadgetsFromStore(
      final GetGadgetsByStoreIdQuery getGadgetsByStoreIdQuery) {
    return this.gadgetService.getStoreIdToGadgets(getGadgetsByStoreIdQuery.getStoreId());
  }
}
