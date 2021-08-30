[![Build Status](https://app.travis-ci.com/sitatec/Go4Lunch.svg?branch=master)](https://app.travis-ci.com/sitatec/Go4Lunch) [![codecov](https://codecov.io/gh/sitatec/Go4Lunch/branch/master/graph/badge.svg?token=2FDERQBVYZ)](https://codecov.io/gh/sitatec/Go4Lunch) [![Maintainability](https://api.codeclimate.com/v1/badges/8ef2025945b92db2af7c/maintainability)](https://codeclimate.com/github/sitatec/Go4Lunch/maintainability)
# Go4Lunch
A demo video is available at the [end of the page](#demo).

Go4Lunch is an application that brings you and your colleagues together for lunch. Go4Luch allows you to see all the restaurants near you on a map or a list. On the map, if at least one of your workmates go to a restaurant, the color of the marker of that restaurant will be green, and if you click on the marker you will see in the window info the number of the workmates who go there if you click on the window info you will be redirected in a new screen that shows the restaurant details and all of your workmates who go to that restaurant. To be able to interact with you workmates, you must select a workplace either when you lunch the app for the first time (the app will show a pop up) or in the settings. You can schedule a notification that will remind you every day for lunch time, you can search a restaurant by its name or address... to see all the features of the app, watch the [demo video](#demo).

![Go4Lunch](https://github.com/sitatec/Go4Lunch/blob/master/assets/Go4Lunch_banner.png)

> ⚠️ This image <img src="https://www.aaronsrubbishremoval.com.au/wp-content/uploads/2021/06/powered-google.png" alt="Powered by Google" width="120px" /> in the app doesn't mean that the app is a google product, but to show data from google place api in your app, you must show the _Powered by google_ image wherever the data are displayed.

# Architecture
![Go4Lunch architecture](https://github.com/sitatec/Go4Lunch/blob/master/assets/Go4Lunch_Architecture.png)

To prevent the repositories (in the [domain](https://github.com/sitatec/Go4Lunch/tree/master/app/src/main/java/com/berete/go4lunch/domain/)) depending on the data sources, I have created [interfaces](https://github.com/sitatec/Go4Lunch/tree/master/app/src/main/java/com/berete/go4lunch/domain/restaurants/services) that represent the data sources. The repositories depend on them because there are inside the domain. Then I use the [Adapter design pattern](https://en.wikipedia.org/wiki/Adapter_pattern) to adapt the data sources. 

Now, assuming that I no longer want to use firebase, because I have created my own server and I want to use it to store the user's data, all I have to do is to delete the firebase adapter and all its dependencies, create a client for my server and implement one or more interfaces (depending on the data the server provides), without touching either the domain layer or the UI layer `-> maintainability++ && testability++`. 

I have also created and deployed two functions (written in TypeScrip) on Firebase functions, one to initialize the user data on cloud firestore when his account is created, another to delete all the user's data when his account is deleted (the functions will be triggered by firebase auth).

# Dependencies
#### Prod
  - Navigation component
  - View binding
  - Hilt
  - View Model
  - Live Data
  - Retrofit
  - Gson
  - Firebase auth
  - Firebase firestore
#### Tests
  - Espresso
  - UI Automator (for some specifics cases that espresso doesn't support, i.g. clicking on google maps markers)
  - Junit4
  - Mockito

# Services
  - Google Place APIs (the real APIs i.e. accessible through HTTPS, not the android SDKs)
    - Nearby Search
    - Place Autocomplete
    - Place Details

# Tools
  - Travis CI
  - Jacoco
  - Codecov
  - Codeclimate

# Build
To be able to build the project you need google maps and google place API key(s), and store them in your local.properties file like this:
```
GOOGLE_PLACE_API_KEY=YOUR-API-KEY
GOOGLE_MAP_API_KEY=YOUR-API-KEY
```
If you don't want to store your keys in the local.properties, you will have to edit the [build.gradle](https://github.com/sitatec/Go4Lunch/blob/8b39e3783cdba66c6950c1f85ef74a43b99bbe3a/app/build.gradle#L23) inside the app module.

# Demo

[![Go4Lunch](https://github.com/sitatec/Go4Lunch/blob/master/assets/Screen%20Shot%202021-06-13%20at%209.42.35%20PM.png)](https://drive.google.com/file/d/1Av9_EWN-zYBSTeaSVVuNugZ5bxDoSevR/preview)

