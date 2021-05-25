package com.berete.go4lunch.domain.shared.models;

import java.util.List;

public class User {
  private final String id;
  private final String username;
  private final String photoUrl;
  private List<String> likedRestaurantsIds;
  private List<String> conversationsIds;
  private String workplaceId ="";
  private String chosenRestaurantId = "";
  private String chosenRestaurantName = "";

  public User(String id, String username, String photoUrl) {
    this.id = id;
    this.username = username;
    this.photoUrl = photoUrl;
  }

  public User(
      String id,
      String username,
      String photoUrl,
      String workplaceId,
      String chosenRestaurantId,
      String chosenRestaurantName,
      List<String> likedRestaurantsIds,
      List<String> conversationsIds ) {
    this(id, username, photoUrl);
    this.workplaceId = workplaceId;
    this.chosenRestaurantId = chosenRestaurantId;
    this.conversationsIds = conversationsIds;
    this.likedRestaurantsIds = likedRestaurantsIds;
    this.chosenRestaurantName = chosenRestaurantName;
  }

  public String getUsername() {
    return username;
  }

  public String getWorkplaceId() {
    return workplaceId;
  }

  public String getChosenRestaurantId() {
    return chosenRestaurantId;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public List<String> getConversationsIds() {
    return conversationsIds;
  }

  public List<String> getLikedRestaurantsIds() {
    return likedRestaurantsIds;
  }

  public String getId() {
    return id;
  }

  public String getChosenRestaurantName() {
    return chosenRestaurantName;
  }

  public void setWorkplaceId(String workplaceId) {
    this.workplaceId = workplaceId;
  }

  public void setChosenRestaurantId(String todayChosenRestaurantId) {
    this.chosenRestaurantId = todayChosenRestaurantId;
  }

  public void setConversationsIds(List<String> conversationsIds) {
    this.conversationsIds = conversationsIds;
  }

  public void setLikedRestaurantsIds(List<String> likedRestaurantsIds) {
    this.likedRestaurantsIds = likedRestaurantsIds;
  }

  public void setChosenRestaurantName(String chosenRestaurantName) {
    this.chosenRestaurantName = chosenRestaurantName;
  }
}
