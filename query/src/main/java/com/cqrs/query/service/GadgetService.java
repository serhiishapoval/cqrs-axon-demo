package com.cqrs.query.service;

import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.GadgetToCountryResponse;
import com.cqrs.query.model.rest.StoreIdToGadgetResponse;
import java.util.List;

public interface GadgetService {

  GadgetResponse getGadget(String gadgetId);

  List<GadgetToCountryResponse> getGadgetsFromCountry(String country);

  List<StoreIdToGadgetResponse> getStoreIdToGadgets(String storeId);
}
