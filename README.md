# Go4Lunch
Go4Lunch is an application that brings you and your colleagues together for lunch. (A demo video is available at the end of the page).

![Go4Lunch](https://github.com/sitatec/Go4Lunch/blob/master/assets/Go4Lunch_banner.png)
# Architecture
![Go4Lunch architecture](https://github.com/sitatec/Go4Lunch/blob/master/assets/Go4Lunch_Architecture.png)

To prevent the repositories (in the [domain](https://github.com/sitatec/Go4Lunch/tree/master/app/src/main/java/com/berete/go4lunch/domain/)) depending on the data sources, i have created [interfaces](https://github.com/sitatec/Go4Lunch/tree/master/app/src/main/java/com/berete/go4lunch/domain/restaurants/services) that represent the data sources, the repositories depend on them because there are inside the domain. Then I use the Adapter design pattern to adapt the data sources. Now, assuming that I no longer want to use firebase, because I have created my own server and I want to use it to store the user's data, all I have to do is to delete the firebase adapter and all its dependencies, create a client for my server and implement one or more interfaces (depending on the data the server provides), without touching either the domain layer or the UI layer. 
I have also created and deployed two functions on Firebase functions, one to initialize the user data on cloud firestore when its account is just created, another to delete all the user's data when its account is deleted (the functions will be triggered by firebase auth).

# Dependencies
#### Prod
  - Navigation component
  - View binding
  - Hilt
  - Retrofit
  - Gson
  - Firebase auth
  - Firebase firestore
#### Tests
  - Junit4
  - Espresso

# Build
To be able to build the project you need google maps and google place API key(s), and store them in your local.properties file like this:
```groovi
GOOGLE_PLACE_API_KEY=YOUR-API-KEY
GOOGLE_MAP_API_KEY=YOUR-API-KEY
```
If you don't want to store your keys in the local.properties, you will have to edit the [build.gradle](https://github.com/sitatec/Go4Lunch/blob/8b39e3783cdba66c6950c1f85ef74a43b99bbe3a/app/build.gradle#L23) inside the app module.

# Demo
[![Go4Lunch](https://github.com/sitatec/Go4Lunch/blob/master/assets/Screen%20Shot%202021-06-13%20at%209.42.35%20PM.png)](https://drive.google.com/file/d/1Av9_EWN-zYBSTeaSVVuNugZ5bxDoSevR/view?usp=sharing)

