package com.berete.go4lunch.domain.restaurants.models;

import com.berete.go4lunch.domain.utils.DistanceUtils;

public class Prediction {

  private final String bestMatch;
  private final String relatedText;
  private final String correspondingPlaceId;
  private final String displayableDistance;

  public Prediction(String bestMatch, String relatedText, String correspondingPlaceId, String displayableDistance) {
    this.bestMatch = bestMatch;
    this.relatedText = relatedText;
    this.correspondingPlaceId = correspondingPlaceId;
    this.displayableDistance = displayableDistance;
  }

  public Prediction(String bestMatch, String relatedText, String correspondingPlaceId, Float distance) {
    this.bestMatch = bestMatch;
    this.relatedText = relatedText;
    this.correspondingPlaceId = correspondingPlaceId;
    this.displayableDistance = DistanceUtils.convertToDisplayableDistance(distance);
  }

  public String getBestMatch() {
    return bestMatch;
  }

  public String getRelatedText() {
    return relatedText;
  }

  public String getCorrespondingPlaceId() {
    return correspondingPlaceId;
  }

  public String getDisplayableDistance() {
    return displayableDistance;
  }
}
