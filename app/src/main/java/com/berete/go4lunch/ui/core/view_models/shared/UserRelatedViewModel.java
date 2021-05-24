package com.berete.go4lunch.ui.core.view_models.shared;

import androidx.lifecycle.ViewModel;

import com.berete.go4lunch.domain.shared.models.User;
import com.berete.go4lunch.domain.shared.repositories.UserRepository;
import com.berete.go4lunch.domain.utils.Callback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserRelatedViewModel extends ViewModel {

  private final UserRepository userRepository;

  @Inject
  public UserRelatedViewModel(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  public void getCurrentUserWorkmates(Callback<User[]> callback) {
    userRepository.getCurrentUserWorkmates(callback);
  }


}
