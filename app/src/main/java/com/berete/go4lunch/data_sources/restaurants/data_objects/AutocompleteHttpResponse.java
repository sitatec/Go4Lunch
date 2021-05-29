package com.berete.go4lunch.data_sources.restaurants.data_objects;

import android.util.Log;

import com.berete.go4lunch.domain.restaurants.models.Place;
import com.berete.go4lunch.domain.restaurants.models.Prediction;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class AutocompleteHttpResponse {

  @SerializedName("predictions")
  @Expose
  public List<PredictionDataObject> predictionDataObjects = null;

  @SerializedName("status")
  @Expose
  public String status;

  public Prediction[] getPredictions() {
    if(!status.equals("OK")) return new Prediction[0];
    return convertResult(PredictionDataObject::toPrediction);
  }

  public Prediction[] getFilteredPredictions(Place.Type[] filter){
    if(!status.equals("OK")) return new Prediction[0];
    final List<Place.Type> placeList = Arrays.asList(filter);
    if(placeList.contains(Place.Type.RESTAURANT)){
      return convertResult(PredictionDataObject::toRestaurantPrediction);
    }
    return new Prediction[0];// Only the restaurant Type is available yet.
  }

  private Prediction[] convertResult(Function<PredictionDataObject, Prediction> convertToPrediction){
    Log.i("HTTP_RESPONSE", "convertResult");
    return predictionDataObjects.stream()
        .map(convertToPrediction)
        .filter(Objects::nonNull)
        .toArray(Prediction[]::new);
  }
}

class PredictionDataObject {
  @SerializedName("description")
  @Expose
  public String description;

  @SerializedName("distance_meters")
  @Expose
  public Float distance_meters;

  @SerializedName("matched_substrings")
  @Expose
  public List<Matched_substring> matched_substrings = null;

  @SerializedName("place_id")
  @Expose
  public String place_id;

  @SerializedName("reference")
  @Expose
  public String reference;

  @SerializedName("structured_formatting")
  @Expose
  public Structured_formatting structured_formatting;

  @SerializedName("terms")
  @Expose
  public List<Term> terms = null;

  @SerializedName("types")
  @Expose
  public List<String> types = null;

  public Prediction toRestaurantPrediction() {
    if (types.contains("restaurant")
        || types.contains("food")
        || types.contains("meal_takeaway")
        || types.contains("meal_delivery")) {
      return toPrediction();
    }
    return null;
  }

  public Prediction toPrediction(){
    return new Prediction(
        structured_formatting.main_text,
        structured_formatting.secondary_text,
        place_id,
        distance_meters);
  }
}

class Main_text_matched_substring {
  @SerializedName("length")
  @Expose
  public Integer length;

  @SerializedName("offset")
  @Expose
  public Integer offset;
}

class Matched_substring {
  @SerializedName("length")
  @Expose
  public Integer length;

  @SerializedName("offset")
  @Expose
  public Integer offset;
}

class Structured_formatting {
  @SerializedName("main_text")
  @Expose
  public String main_text;

  @SerializedName("main_text_matched_substrings")
  @Expose
  public List<Main_text_matched_substring> main_text_matched_substrings = null;

  @SerializedName("secondary_text")
  @Expose
  public String secondary_text;
}

class Term {
  @SerializedName("offset")
  @Expose
  public Integer offset;

  @SerializedName("value")
  @Expose
  public String value;
}
