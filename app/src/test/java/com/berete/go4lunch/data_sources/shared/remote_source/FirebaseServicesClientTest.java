package com.berete.go4lunch.data_sources.shared.remote_source;

import android.net.Uri;

import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.utils.Callback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.berete.go4lunch.FakeData.fakeCurrentUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirebaseServicesClientTest {

  final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
  FirebaseServicesClient firebaseServicesClient;
  // Firestore
  final FirebaseFirestore firebaseFirestoreMock = mock(FirebaseFirestore.class);
  final CollectionReference usersCollectionMock = mock(CollectionReference.class);
  final DocumentReference userDocumentMock = mock(DocumentReference.class);
  final Task<DocumentSnapshot> userDocSnapshotTask = mock(Task.class);
  final DocumentSnapshot userDocumentSnapshot = mock(DocumentSnapshot.class);
  final Query queryMock = mock(Query.class);
  final Task<QuerySnapshot> querySnapshotTask = mock(Task.class);
  final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
  final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);

  @Before
  public void setUp() {
    setUpFirebaseAuthMocks();
    setUpFirebaseFirestoreMocks();
    initFirebaseServicesClient();
  }

  @After
  public void tearDown(){
    clearInvocations(
        firebaseFirestoreMock,
        firebaseAuthMock,
        usersCollectionMock,
        userDocumentMock,
        userDocSnapshotTask,
        userDocumentSnapshot,
        queryMock,
        querySnapshot,
        querySnapshotTask);
  }

  @Test
  public void should_initialize_FirebaseServicesClient_correctly() {
    //    initFirebaseServicesClient(); ---> Called in setUp() method.
    assertNotNull(firebaseServicesClient.getCurrentUser());
  }

  @Test
  public void should_fetch_user_additional_data_when_initializing_the_client() {
    verify(firebaseFirestoreMock).collection("users");
    verify(usersCollectionMock).document(fakeCurrentUser.getId());
    verify(userDocumentMock).get();

    verify(userDocumentSnapshot).getString(UserProvider.CHOSEN_RESTAURANT_ID);
    verify(userDocumentSnapshot).getString(UserProvider.CHOSEN_RESTAURANT_NAME);
    verify(userDocumentSnapshot).getString(UserProvider.WORKPLACE);
    verify(userDocumentSnapshot).get(UserProvider.CONVERSATIONS);
    verify(userDocumentSnapshot).get(UserProvider.LIKED_RESTAURANTS);
  }

  @Test
  public void should_get_user_by_workplace() {
    clearInvocations(firebaseFirestoreMock);// otherwise some method expected to be invoked once
    // will be twice (when initializing the client and here).
    firebaseServicesClient.getUsersByWorkplace(
        fakeCurrentUser.getWorkplaceId(),
        new Callback<User[]>() {
          @Override
          public void onSuccess(User[] users) {
            assertEquals(users[0].getWorkplaceId(), fakeCurrentUser.getWorkplaceId());
          }

          @Override
          public void onFailure() {}
        });
    verify(firebaseFirestoreMock).collection("users");
    verify(usersCollectionMock)
        .whereEqualTo(UserProvider.WORKPLACE, fakeCurrentUser.getWorkplaceId());

    final ArgumentCaptor<OnSuccessListener<QuerySnapshot>> onSuccessListenerCaptor =
        ArgumentCaptor.forClass(OnSuccessListener.class);
    verify(querySnapshotTask).addOnSuccessListener(onSuccessListenerCaptor.capture());
    onSuccessListenerCaptor.getValue().onSuccess(querySnapshot);
  }

  @Test
  public void should_get_user_by_chosen_restaurant() {
    firebaseServicesClient.getUsersByChosenRestaurant(
        fakeCurrentUser.getChosenRestaurantId(),
        fakeCurrentUser.getWorkplaceId(),
        new Callback<User[]>() {
          @Override
          public void onSuccess(User[] users) {
            assertEquals(users[0].getChosenRestaurantId(), fakeCurrentUser.getChosenRestaurantId());
            assertEquals(
                users[0].getChosenRestaurantName(), fakeCurrentUser.getChosenRestaurantName());
          }

          @Override
          public void onFailure() {}
        });

    verify(usersCollectionMock)
        .whereEqualTo(UserProvider.WORKPLACE, fakeCurrentUser.getWorkplaceId());
    verify(queryMock)
        .whereEqualTo(UserProvider.CHOSEN_RESTAURANT_ID, fakeCurrentUser.getChosenRestaurantId());

    final ArgumentCaptor<OnSuccessListener<QuerySnapshot>> onSuccessListenerCaptor =
        ArgumentCaptor.forClass(OnSuccessListener.class);
    verify(querySnapshotTask).addOnSuccessListener(onSuccessListenerCaptor.capture());
    onSuccessListenerCaptor.getValue().onSuccess(querySnapshot);
  }

  @Test
  public void should_push_current_user_data_to_firebase() {
    firebaseServicesClient.pushCurrentUserData();

    final ArgumentCaptor<Map<String, Object>> userDataCaptor = ArgumentCaptor.forClass(HashMap.class);
    verify(userDocumentMock).update(userDataCaptor.capture());

    assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getWorkplaceId()));
    assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getChosenRestaurantId()));
    assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getChosenRestaurantName()));
    assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getLikedRestaurantsIds()));
    assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getConversationsIds()));
  }

  @Test
  public void should_reset_current_user_chosen_restaurant() {
    firebaseServicesClient.resetCurrentUserChosenRestaurant();

    final ArgumentCaptor<Map<String, Object>> userDataCaptor = ArgumentCaptor.forClass(HashMap.class);
    verify(userDocumentMock).update(userDataCaptor.capture());

    assertEquals(userDataCaptor.getValue().get(UserProvider.CHOSEN_RESTAURANT_ID), "");
  }

  @Test
  public void should_update_current_user_data() {
    final ArgumentCaptor<Map<String, Object>> userDataCaptor = ArgumentCaptor.forClass(HashMap.class);

    // WORKPLACE
    final String fakeWorkplaceId = "another_fake_id";
    firebaseServicesClient.updateUserData(UserProvider.WORKPLACE, fakeWorkplaceId);
    verify(userDocumentMock).update(userDataCaptor.capture());
    assertEquals(userDataCaptor.getValue().get(UserProvider.WORKPLACE), fakeWorkplaceId);

    clearInvocations(userDocumentMock);

    // RESTAURANT_NAME
    final String fakeRestaurantName = "namerestaurant";
    firebaseServicesClient.updateUserData(UserProvider.CHOSEN_RESTAURANT_NAME, fakeRestaurantName);
    verify(userDocumentMock).update(userDataCaptor.capture());
    assertEquals(userDataCaptor.getValue().get(UserProvider.CHOSEN_RESTAURANT_NAME), fakeRestaurantName);
  }

  @Test
  public void should_logout_current_user() {
    firebaseServicesClient.logout(()->{});
    verify(firebaseAuthMock).signOut();
  }

  @Test
  public void should_delete_current_user_account() {
    final Task<Void> deleteTask = mock(Task.class);
    final ArgumentCaptor<OnSuccessListener> deleteCallbackCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
    when(firebaseUserMock.delete()).thenReturn(deleteTask);
    when(deleteTask.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(deleteTask);

    firebaseServicesClient.deleteCurrentUserAccount(new Callback<Boolean>() {
      @Override
      public void onSuccess(Boolean deletedSuccessfully) {
        assertTrue(deletedSuccessfully);
      }

      @Override
      public void onFailure() {

      }
    });

    verify(firebaseUserMock).delete();
    verify(deleteTask).addOnSuccessListener(deleteCallbackCaptor.capture());
    deleteCallbackCaptor.getValue().onSuccess(null);
  }

  @Test
  public void should_get_current_user() {
    assertEquals(firebaseServicesClient.getCurrentUser().getId(), fakeCurrentUser.getId());
  }

  // ----------- UTILS ----------- //

  private void setUpFirebaseAuthMocks() {
    // FirebaseUser mock
    when(firebaseUserMock.getEmail()).thenReturn(fakeCurrentUser.getEmail());
    when(firebaseUserMock.getDisplayName()).thenReturn(fakeCurrentUser.getUsername());
    final Uri photoUriMock = mock(Uri.class);
    when(photoUriMock.toString()).thenReturn(fakeCurrentUser.getPhotoUrl());
    when(firebaseUserMock.getPhotoUrl()).thenReturn(photoUriMock);
    when(firebaseUserMock.getUid()).thenReturn(fakeCurrentUser.getId());
    when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);
  }

  private void setUpFirebaseFirestoreMocks() {
    // Collection
    when(firebaseFirestoreMock.collection("users")).thenReturn(usersCollectionMock);
    when(usersCollectionMock.document(fakeCurrentUser.getId())).thenReturn(userDocumentMock);
    // Document
    when(userDocumentMock.get()).thenReturn(userDocSnapshotTask);
    when(userDocumentSnapshot.getString(UserProvider.CHOSEN_RESTAURANT_ID))
        .thenReturn(fakeCurrentUser.getChosenRestaurantId());
    when(userDocumentSnapshot.getString(UserProvider.CHOSEN_RESTAURANT_NAME))
        .thenReturn(fakeCurrentUser.getChosenRestaurantName());
    when(userDocumentSnapshot.getString(UserProvider.WORKPLACE))
        .thenReturn(fakeCurrentUser.getWorkplaceId());
    when(userDocumentSnapshot.get(UserProvider.CONVERSATIONS))
        .thenReturn(fakeCurrentUser.getConversationsIds());
    when(userDocumentSnapshot.get(UserProvider.LIKED_RESTAURANTS))
        .thenReturn(fakeCurrentUser.getLikedRestaurantsIds());
    // Query
    when(usersCollectionMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);
    when(queryMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);
    when(queryMock.get()).thenReturn(querySnapshotTask);
    when(querySnapshotTask.addOnSuccessListener(any(OnSuccessListener.class)))
        .thenReturn(querySnapshotTask);
    when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(userDocumentSnapshot));
  }

  private void initFirebaseServicesClient() {
    firebaseServicesClient = new FirebaseServicesClient(firebaseFirestoreMock, firebaseAuthMock);
    verify(firebaseAuthMock).addAuthStateListener(isA(FirebaseAuth.AuthStateListener.class));
    final ArgumentCaptor<OnSuccessListener<DocumentSnapshot>> onSuccessListenerCaptor =
        ArgumentCaptor.forClass(OnSuccessListener.class);
    verify(userDocSnapshotTask).addOnSuccessListener(onSuccessListenerCaptor.capture());
    onSuccessListenerCaptor.getValue().onSuccess(userDocumentSnapshot);
  }
}
