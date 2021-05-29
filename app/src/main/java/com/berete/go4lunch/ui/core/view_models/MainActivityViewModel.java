package com.berete.go4lunch.ui.core.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.restaurants.repositories.PlaceNamePredictionsRepository;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.shared.repositories.UserRepository;
import com.berete.go4lunch.domain.utils.Callback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {
  private final PlaceNamePredictionsRepository placeNamePredictionsRepository;
  private final UserRepository userRepository;
  private final MutableLiveData<User> currentUser = new MutableLiveData<>();

  @Inject
  public MainActivityViewModel(
      PlaceNamePredictionsRepository placeNamePredictionsRepository,
      UserRepository userRepository) {
    this.placeNamePredictionsRepository = placeNamePredictionsRepository;
    this.userRepository = userRepository;
    userRepository.addUserLoginCompleteListener(currentUser::setValue);
  }

  public void getWorkplacePredictions(String input) {
    Log.i("ViewModel", "mainActivityViewModel.getWorkplacePredictions");
    placeNamePredictionsRepository.predict(input, null, Place.LangCode.getSystemLanguage());
  }

  public void getRestaurantPrediction(String input, GeoCoordinates currentLocation) {
    Log.i("ViewModel", "mainActivityViewModel.getRestaurantPrediction");
    placeNamePredictionsRepository.predictWithFilter(
        input,
        currentLocation,
        Place.LangCode.getSystemLanguage(),
        new Place.Type[] {Place.Type.RESTAURANT});
  }

  public void setWorkplacePredictionListener(Callback<Prediction[]> listener) {
    placeNamePredictionsRepository.subscribeForResults(listener);
  }

  public void setRestaurantPredictionResultListener(Callback<Prediction[]> listener) {
    placeNamePredictionsRepository.subscribeForFilteredResult(listener);
  }

  public LiveData<User> getCurrentUser() {
    return currentUser;
  }

  public void updateUserData(String dataType, Object data) {
    userRepository.updateUserData(dataType, data);
  }
}
