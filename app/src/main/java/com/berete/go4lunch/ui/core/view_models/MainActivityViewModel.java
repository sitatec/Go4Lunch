package com.berete.go4lunch.ui.core.view_models;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;
import com.berete.go4lunch.domain.restaurants.repositories.RestaurantNamePredictionsRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {
    final RestaurantNamePredictionsRepository restaurantNamePredictionsRepository;

    @Inject
    public MainActivityViewModel(RestaurantNamePredictionsRepository restaurantNamePredictionsRepository){
      this.restaurantNamePredictionsRepository = restaurantNamePredictionsRepository;
    }

    public void getPredictions(String input, GeoCoordinates currentLocation){
      restaurantNamePredictionsRepository.predict(input, currentLocation, Place.LangCode.getSystemLanguage());
    }

    public void setPredictionResultListener(AutocompleteService.ResultListener listener){
      restaurantNamePredictionsRepository.subscribeForResults(listener);
    }
}
