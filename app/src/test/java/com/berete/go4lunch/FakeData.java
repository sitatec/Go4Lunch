package com.berete.go4lunch;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;

import java.util.Arrays;

public interface FakeData {
  Callback<Place[]> fakePlaceCallback =
      new Callback<Place[]>() {
        @Override
        public void onSuccess(Place[] places) {}

        @Override
        public void onFailure() {}
      };

  Callback<Place> fakeSinglePlaceCallback =
      new Callback<Place>() {
        @Override
        public void onSuccess(Place places) {}

        @Override
        public void onFailure() {}
      };

  Callback<User> fakeSingleUserCallback =
      new Callback<User>() {
        @Override
        public void onSuccess(User places) {}

        @Override
        public void onFailure() {}
      };

  Callback<User[]> fakeUserCallback =
      new Callback<User[]>() {
        @Override
        public void onSuccess(User[] places) {}

        @Override
        public void onFailure() {}
      };

  Callback<Prediction[]> fakePredictionCallback =
      new Callback<Prediction[]>() {
        @Override
        public void onSuccess(Prediction[] places) {}

        @Override
        public void onFailure() {}
      };

  User fakeCurrentUser =
      new User(
          "id",
          "username",
          "url.photo/",
          "@mail",
          "workplaceId",
          "rstId",
          "rstNm",
          Arrays.asList("one", "two"),
          Arrays.asList("three", "next..."));

  Place.LangCode fakeLangCode = Place.LangCode.en;

  String fakePlaceId = "id";

  GeoCoordinates fakeGeoCoordinates = new GeoCoordinates(0., 0.);

  String fakeAutocompleteInput = "fake_input";

  Place.Type[] fakeFilter = new Place.Type[] {Place.Type.RESTAURANT};
}
