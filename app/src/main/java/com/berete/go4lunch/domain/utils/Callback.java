package com.berete.go4lunch.domain.utils;

public interface Callback<Result> {
  void onSuccess(Result result);
  void onFailure();
}
