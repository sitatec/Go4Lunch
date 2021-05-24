package com.berete.go4lunch.data_souces.shared.remote_source;

import android.net.Uri;
import android.util.Log;

import com.berete.go4lunch.domain.restaurants.services.RestaurantSpecificDataProvider;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO REFACTORING
public class FirebaseServicesClient implements UserProvider, RestaurantSpecificDataProvider {
  final FirebaseFirestore firestoreDb;
  final FirebaseAuth firebaseAuth;
  User currentUser;

  private OnAuthStateChangesListener authStateChangesListener;
  private boolean userCompletlySignedIn = false;

  public FirebaseServicesClient(FirebaseFirestore firestoreDb, FirebaseAuth firebaseAuth) {
    this.firestoreDb = firestoreDb;
    this.firebaseAuth = firebaseAuth;
    setCurrentUserFromFirebaseUser(firebaseAuth);
    firebaseAuth.addAuthStateListener(this::setCurrentUserFromFirebaseUser);
  }

  public void setCurrentUserFromFirebaseUser(FirebaseAuth firebaseAuth) {
    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    if (firebaseUser != null) {
      final Uri userPhotoUrl = firebaseUser.getPhotoUrl();
      currentUser =
          new User(
              firebaseUser.getUid(),
              firebaseUser.getDisplayName(),
              userPhotoUrl != null ? userPhotoUrl.toString() : DEFAULT_USER_PHOTO_URL);
      fetchCurrentUserAdditionalData();
    } else currentUser = null;
  }

  private void fetchCurrentUserAdditionalData() {
    firestoreDb
        .collection("users")
        .document(currentUser.getId())
        .get()
        .addOnSuccessListener(this::onUserAdditionalDataFetched);
  }

  private void onUserAdditionalDataFetched(DocumentSnapshot userDocument) {
    currentUser.setChosenRestaurantId(userDocument.getString(CHOSEN_RESTAURANT_ID));
    currentUser.setWorkplaceId(userDocument.getString(WORKPLACE));
    currentUser.setChosenRestaurantName(userDocument.getString(CHOSEN_RESTAURANT_NAME));
    currentUser.setConversationsIds((List<String>) userDocument.get(CONVERSATIONS));
    currentUser.setLikedRestaurantsIds((List<String>) userDocument.get(LIKED_RESTAURANTS));
    if (authStateChangesListener != null) {
      authStateChangesListener.onFetched(currentUser);
      authStateChangesListener = null;
    } else userCompletlySignedIn = true;
  }

  @Override
  public void getUserById(String userId, Callback<User> callback) {}

  private User[] jsonArrayToUserArray(String json) {
    final Type jsonArrayType = new TypeToken<Map<String, String>[]>() {}.getType();
    final Map<String, String>[] listOfUserJson = new Gson().fromJson(json, jsonArrayType);
    final List<User> users = new ArrayList<>();
    User currentUserInList;
    for (Map<String, String> userJson : listOfUserJson) {
      currentUserInList =
          new User(
              userJson.get("id"),
              userJson.get("name"),
              getDefaultPhotoIfNull(userJson.get("photoUrl")));
      currentUserInList.setChosenRestaurantName(userJson.get("restaurantName"));
      currentUserInList.setChosenRestaurantId(userJson.get("restaurantId"));
      users.add(currentUserInList);
    }
    return users.toArray(new User[0]);
  }

  private String getDefaultPhotoIfNull(String photoUrl) {
    return photoUrl != null ? photoUrl : DEFAULT_USER_PHOTO_URL;
  }

  @Override
  public void getUsersByWorkplace(String workplaceId, Callback<User[]> callback) {
    Log.i("getWorkplaceEmployees", "ON_REQUEST : " + workplaceId);
    firestoreDb
        .collection("users")
        .whereEqualTo(WORKPLACE, workplaceId)
        .get()
        .addOnSuccessListener(
            snapshots -> callback.onSuccess(usersDocumentsToArray(snapshots.getDocuments())))
        .addOnFailureListener(__ -> callback.onFailure());
  }

  private User[] usersDocumentsToArray(List<DocumentSnapshot> usersDocuments) {
    final List<User> userList = new ArrayList<>();
    for (DocumentSnapshot userDoc : usersDocuments) {
      userList.add(
          new User(
              userDoc.getId(),
              userDoc.getString("name"),
              userDoc.getString("photoUrl"),
              userDoc.getString(WORKPLACE),
              userDoc.getString(CHOSEN_RESTAURANT_ID),
              userDoc.getString(CHOSEN_RESTAURANT_NAME),
              (List<String>) userDoc.get(LIKED_RESTAURANTS),
              (List<String>) userDoc.get(CONVERSATIONS)));
    }
    return userList.toArray(new User[0]);
  }

  @Override
  public void getUsersByChosenRestaurant(
      String restaurantId, String usersWorkplaceId, Callback<User[]> callback) {
    firestoreDb
        .collection("users")
        .whereEqualTo(WORKPLACE, usersWorkplaceId)
        .whereEqualTo(CHOSEN_RESTAURANT_ID, restaurantId)
        .get()
        .addOnSuccessListener(
            snapshots -> callback.onSuccess(usersDocumentsToArray(snapshots.getDocuments())))
        .addOnFailureListener(__ -> callback.onFailure());
  }

  @Override
  public void pushCurrentUserData() {
    final Map<String, Object> userData = new HashMap<>();
    userData.put(CHOSEN_RESTAURANT_ID, currentUser.getChosenRestaurantId());
    userData.put(CHOSEN_RESTAURANT_NAME, currentUser.getChosenRestaurantName());
    userData.put(CONVERSATIONS, currentUser.getConversationsIds());
    userData.put(WORKPLACE, currentUser.getWorkplaceId());
    userData.put(LIKED_RESTAURANTS, currentUser.getLikedRestaurantsIds());
    firestoreDb.collection("users").document(currentUser.getId()).update(userData);
    updateUserWorkplaceEmployees();
    updateWorkplaceRestaurantRelatedData();
  }

  @Override
  public void resetCurrentUserChosenRestaurant() {
    firestoreDb
        .collection("workplaces")
        .document(currentUser.getWorkplaceId())
        .collection("restaurantsChosenByEmployees")
        .document(currentUser.getChosenRestaurantId())
        .update("clients", FieldValue.arrayRemove(currentUser.getId()));
    currentUser.setChosenRestaurantName("");
    currentUser.setChosenRestaurantId("");
    final Map<String, Object> userData = new HashMap<>();
    userData.put(CHOSEN_RESTAURANT_ID, "");
    userData.put(CHOSEN_RESTAURANT_NAME, "");
    firestoreDb.collection("users").document(currentUser.getId()).update(userData);
  }

  private void updateUserWorkplaceEmployees() {
    final Map<String, Object> userData = new HashMap<>();
    userData.put("employees", Arrays.asList(currentUser.getId()));
    if (currentUser.getWorkplaceId() != null && !currentUser.getWorkplaceId().isEmpty()) {
      final DocumentReference workplaceDocRef =
          firestoreDb.collection("workplaces").document(currentUser.getWorkplaceId());
      workplaceDocRef
          .get()
          .addOnSuccessListener(
              workplaceDoc -> {
                if (workplaceDoc.exists() && workplaceDoc.get("employees") != null) {
                  workplaceDocRef.update("employees", FieldValue.arrayUnion(currentUser.getId()));
                } else {
                  workplaceDocRef.set(userData, SetOptions.mergeFields("employees"));
                }
              });
    }
  }

  @Override
  public void updateUserData(String dataType, Object data) {
    final Map<String, Object> userData = new HashMap<>();
    userData.put(dataType, data);
    if (dataType.equals(CHOSEN_RESTAURANT_ID)) {
      updateWorkplaceRestaurantRelatedData();
      userData.put(CHOSEN_RESTAURANT_NAME, currentUser.getChosenRestaurantName());
    } else if (dataType.equals(WORKPLACE)) {
      updateUserWorkplaceEmployees();
    }
    firestoreDb.collection("users").document(currentUser.getId()).update(userData);
  }

  private void updateWorkplaceRestaurantRelatedData() {
    if (currentUser.getChosenRestaurantId() != null
        && !currentUser.getChosenRestaurantId().isEmpty()) {
      final Map<String, Object> userData = new HashMap<>();
      userData.put("clients", Arrays.asList(currentUser.getId()));
      final DocumentReference restaurantRef =
          firestoreDb
              .collection("workplaces")
              .document(currentUser.getWorkplaceId())
              .collection("restaurantsChosenByEmployees")
              .document(currentUser.getChosenRestaurantId());

      restaurantRef.update("clients", FieldValue.arrayRemove(currentUser.getId()));

      restaurantRef
          .set(userData, SetOptions.mergeFields("clients"))
          .addOnFailureListener(f -> Log.i("updateUserChosenRes", "FAILED"));
    }
  }

  @Override
  public boolean isCurrentUserNew() {
    final FirebaseUserMetadata userMetadata = firebaseAuth.getCurrentUser().getMetadata();
    return userMetadata.getCreationTimestamp() != userMetadata.getLastSignInTimestamp();
  }

  @Override
  public void logout(Runnable onLogout) {
    firebaseAuth.signOut();
    onLogout.run();
  }

  @Override
  public void deleteCurrentUserAccount(Callback<Boolean> callback) {
    firebaseAuth
        .getCurrentUser()
        .delete()
        .addOnSuccessListener(__ -> callback.onSuccess(true))
        .addOnFailureListener(__ -> callback.onFailure());
  }

  @Override
  public void addAuthStateChangesListener(OnAuthStateChangesListener listener) {
    if (userCompletlySignedIn) {
      listener.onFetched(currentUser);
      userCompletlySignedIn = false;
    } else authStateChangesListener = listener;
  }

  @Override
  public User getCurrentUser() {
    return currentUser;
  }

  @Override
  public void getRestaurantClientCountByWorkplace(
      String workplaceId, Callback<Map<String, Integer>> callback) {
    firestoreDb
        .collection("users")
        .whereEqualTo(WORKPLACE, workplaceId)
        .get()
        .addOnSuccessListener(
            usersList -> {
              final Map<String, Integer> employeesCountByRestaurant = new HashMap<>();
              String userChosenRestaurantId;
              Integer employeesCount;
              for (DocumentSnapshot userDoc : usersList.getDocuments()) {
                userChosenRestaurantId = userDoc.getString(CHOSEN_RESTAURANT_ID);
                employeesCount = employeesCountByRestaurant.get(userChosenRestaurantId);
                if (employeesCount == null) {
                  employeesCountByRestaurant.put(userChosenRestaurantId, 1);
                } else employeesCountByRestaurant.put(userChosenRestaurantId, ++employeesCount);
              }
              callback.onSuccess(employeesCountByRestaurant);
            });
    //    firestoreDb
    //        .collection("workplaces")
    //        .document(workplaceId)
    //        .collection("restaurantsChosenByEmployees")
    //        .get()
    //        .addOnSuccessListener(
    //            collectionSnapshot -> {
    //              final Map<String, Integer> employeesCountByRestaurant = new HashMap<>();
    //              final List<DocumentSnapshot> restaurants = collectionSnapshot.getDocuments();
    //              int currentRestaurantClientCount;
    //              for (DocumentSnapshot currentDoc : restaurants) {
    //                currentRestaurantClientCount = ((List<String>)
    // currentDoc.get("clients")).size();
    //                employeesCountByRestaurant.put(currentDoc.getId(),
    // currentRestaurantClientCount);
    //              }
    //              callback.onSuccess(employeesCountByRestaurant);
    //            });
  }

  //  private static String getFirebaseUserPotentialFirstName(String firebaseUserDisplayName){
  //    if(!firebaseUserDisplayName.contains(" ")) return firebaseUserDisplayName;
  //    final List<String> names = Arrays.asList(firebaseUserDisplayName.split(" "));
  //    names.remove(names.size() -1);
  //    return TextUtils.join(" ", names);
  //  }

}
