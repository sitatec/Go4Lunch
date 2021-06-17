package com.berete.go4lunch.domain.shared.repositories;

import com.berete.go4lunch.domain.shared.UserProvider;
import com.berete.go4lunch.domain.utils.Callback;

import org.junit.Before;
import org.junit.Test;

import static com.berete.go4lunch.FakeData.fakeCurrentUser;
import static com.berete.go4lunch.FakeData.fakeSingleUserCallback;
import static com.berete.go4lunch.FakeData.fakeUserCallback;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {

  UserProvider userProvider;
  UserRepository userRepository;

  @Before
  public void setUp() {
    userProvider = mock(UserProvider.class);
    userRepository = new UserRepository(userProvider);
    when(userProvider.getCurrentUser()).thenReturn(fakeCurrentUser);
  }

  @Test
  public void should_get_user_by_id() {
    final String fakeId = "id";
    userRepository.getUserById(fakeId, fakeSingleUserCallback);
    verify(userProvider).getUserById(fakeId, fakeSingleUserCallback);
  }

  @Test
  public void should_get_the_current_user_workmates() {
    userRepository.getCurrentUserWorkmates(fakeUserCallback);
    verify(userProvider)
        .getUsersByWorkplace(eq(fakeCurrentUser.getWorkplaceId()), any(Callback.class));
  }

  @Test
  public void should_get_users_by_chosen_restaurant() {
    userRepository.getUsersByChosenRestaurant(fakeCurrentUser.getChosenRestaurantId(), fakeCurrentUser.getWorkplaceId(), fakeUserCallback);
    verify(userProvider).getUsersByChosenRestaurant(fakeCurrentUser.getChosenRestaurantId(), fakeCurrentUser.getWorkplaceId(), fakeUserCallback);
  }

  @Test
  public void should_get_current_user() {
    assertSame(userRepository.getCurrentUser(), fakeCurrentUser);
    verify(userProvider).getCurrentUser();
  }
}
