package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;

public interface NearbyPlaceProvider {

    int DEFAULT_SEARCH_RADIUS = 3000;

    void setQueryParameters(Place.Type[] placeTypes, Place.Field[] placeFields,
                            GeoCoordinates searchArea, Place.LangCode langCode,
                            Integer maxDistanceInMeter);

    void setQueryParameters(Place.Type[] placeTypes, Place.Field[] placeFields,
                            GeoCoordinates searchArea, Place.LangCode langCode);

    void getPlaceData(Callback listener);


    interface Callback {
        void onSuccess(Place[] places);
        void onFailure();
    }
}
