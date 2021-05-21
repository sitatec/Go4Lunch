package com.berete.go4lunch.ui.restaurant.details;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.repositories.RestaurantDetailsRepository;
import com.berete.go4lunch.domain.restaurants.services.PlaceDetailsProvider;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantDetailViewModel extends ViewModel {

  private static final Place.Field[] DEFAULT_FIELDS = {
      Place.Field.NAME,
      Place.Field.ADDRESS,
      Place.Field.PHONE_NUMBER,
      Place.Field.WEBSITE_URL,
      Place.Field.RATE,
      Place.Field.PHOTO_URL
  };

  private final RestaurantDetailsRepository restaurantDetailsRepository;

  @Inject
  public RestaurantDetailViewModel(RestaurantDetailsRepository restaurantDetailsRepository) {
    this.restaurantDetailsRepository = restaurantDetailsRepository;
  }

  public void getRestaurantDetails(
      String restaurantId, PlaceDetailsProvider.ResponseListener listener) {
    restaurantDetailsRepository.getRestaurantDetails(
        restaurantId, Place.Field.values(), Place.LangCode.getSystemLanguage(), listener);
  }
}
