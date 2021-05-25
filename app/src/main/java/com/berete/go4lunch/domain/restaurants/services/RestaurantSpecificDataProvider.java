package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.utils.Callback;

import java.util.Map;

public interface RestaurantSpecificDataProvider {
  void getRestaurantClientCountByWorkplace(String workplaceId, Callback<Map<String, Integer>> callback);
}
