package com.berete.go4lunch.ui.restaurant;

import androidx.annotation.DrawableRes;

import com.berete.go4lunch.R;

public interface RestaurantUtils {

  @DrawableRes
   static int getStarsDrawableId(int starsCount){
    if (starsCount == 1){
      return R.drawable.ic_one_star_24;
    } else if(starsCount == 2){
      return R.drawable.ic_two_stars_24;
    }
    return R.drawable.ic_three_stars_24;
  }

}
