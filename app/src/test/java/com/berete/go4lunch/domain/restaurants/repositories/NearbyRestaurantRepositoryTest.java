package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.FakeData;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;
import com.berete.go4lunch.domain.utils.Callback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class NearbyRestaurantRepositoryTest {

  NearbyPlaceProvider nearbyPlaceProvider;
  NearbyRestaurantRepository nearbyRestaurantRepository;
  @Before
  public void setUp() {
    nearbyPlaceProvider = mock(NearbyPlaceProvider.class);
    nearbyRestaurantRepository = new NearbyRestaurantRepository(nearbyPlaceProvider);
  }

  @Test
  public void shouldGetNearbyRestaurants() {
    final InOrder inOrder = Mockito.inOrder(nearbyPlaceProvider);
    nearbyRestaurantRepository.getNearbyRestaurants(FakeData.fakePlaceCallback, FakeData.fakeLangCode, FakeData.fakeGeoCoordinates);
    inOrder
        .verify(nearbyPlaceProvider)
        .setNearbySearchQueryParams(
            new Place.Type[] {Place.Type.RESTAURANT},
            NearbyPlaceProvider.DEFAULT_FIELDS,
            FakeData.fakeGeoCoordinates,
            FakeData.fakeLangCode,
            NearbyPlaceProvider.DEFAULT_SEARCH_RADIUS);
    inOrder.verify(nearbyPlaceProvider).getPlaceData(any(Callback.class));
  }
}
