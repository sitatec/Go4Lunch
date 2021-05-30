package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.services.NearbyPlaceProvider;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.berete.go4lunch.FakeData.fakeLangCode;
import static com.berete.go4lunch.FakeData.fakePlaceId;
import static com.berete.go4lunch.FakeData.fakeSinglePlaceCallback;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class PlaceDetailsRepositoryTest {

  PlaceDetailsProvider placeDetailsProvider;
  PlaceDetailsRepository placeDetailsRepository;

  @Before
  public void setUp() {
    placeDetailsProvider = mock(PlaceDetailsProvider.class);
    placeDetailsRepository = new PlaceDetailsRepository(placeDetailsProvider);
  }

  @Test
  public void getRestaurantDetails() {
    placeDetailsProvider.getPlaceDetail(
        fakePlaceId, NearbyPlaceProvider.DEFAULT_FIELDS, fakeLangCode, fakeSinglePlaceCallback);
    verify(placeDetailsProvider)
        .getPlaceDetail(
            fakePlaceId, NearbyPlaceProvider.DEFAULT_FIELDS, fakeLangCode, fakeSinglePlaceCallback);
  }
}
