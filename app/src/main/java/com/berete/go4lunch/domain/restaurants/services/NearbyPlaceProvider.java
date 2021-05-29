package com.berete.go4lunch.domain.restaurants.services;

import com.berete.go4lunch.domain.restaurants.models.GeoCoordinates;
import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.utils.Callback;

public interface NearbyPlaceProvider {

    int DEFAULT_SEARCH_RADIUS = 3000;
    Place.Field[] DEFAULT_FIELDS = new Place.Field[] {
        Place.Field.ADDRESS,
        Place.Field.GEO_COORDINATES,
        Place.Field.NAME,
        Place.Field.OPENING_HOURS,
        Place.Field.PHONE_NUMBER,
        Place.Field.PHOTO_URL,
        Place.Field.WEBSITE_URL,
        Place.Field.RATE
    };

    void setNearbySearchQueryParams(Place.Type[] placeTypes, Place.Field[] placeFields,
                                    GeoCoordinates searchArea, Place.LangCode langCode,
                                    Integer maxDistanceInMeter);

    void setNearbySearchQueryParams(Place.Type[] placeTypes, Place.Field[] placeFields,
                                    GeoCoordinates searchArea, Place.LangCode langCode);

    void getPlaceData(Callback<Place[]> listener);

}
