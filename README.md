# MyS3Chat – Complete Open Source Real Time Android Chat Application using Firebase

In this tutorial we will be developing real time chat application using firebase, At the end of this tutorial, you will have a complete working android application ready to be published on playstore, excited? Let’s get started.

<a target="_blank" href="https://play.google.com/store/apps/details?id=com.mys3soft.mys3chat"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="80"/></a>

This tutorial assumes that you have basic knowledge of Java and Android SDK, you have build through small android appplications and you are ready to apply your learning on some real projects.

# Prerequisites:

- Android studio is installed and Android SDK is configured
- Android Physical or virtual device is ready for testing our application
- A working firebase account

# Development

To be able to start development on MyS3Chat make sure that you have the following pre-requisites installed.

Follow this tutorial for how to develop with android: https://developer.android.com/codelabs/basic-android-kotlin-compose-first-app#1

# Key Classes

All of these files can be found within app/src/main/java/com/mys3soft/mys3chat/

### ActivityMain.java

<img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen2.jpg" height="450" />

This will be our main activity where the user first lands, In this activity we will be first checking if the user is logged in and redirecting him to ActivityLogin if not logged in. After successfully logging in, the first thing we will be showing on our application’s main activity is chat history (if there is any), that’s what any user will be looking for.

At the very first login, we fecth data from firabase and store in local database using SQLite, and at the later we do fetch chat history from end user’s local storage.

To avoid main thread freezing, each request to network is made in a new task.

### ActivityChat.java

<img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen3.jpg" height="450" />

After successfully showing chat history a user can click on any friend’s chat and we will send him to chat activity. If user does not have any contacts he can go to search option, where he can search by name, email and send connection request.

### AppService.java

This class is responsible for listening to firebase and show the user a notication whenever there is any new activity, new message, new connection request etc.

### DataContext.java

This is the class where we handle all local data storage for getting chat history, getting contact lists, deleteing contact, saving new message to local storage etc.

### tools/IFireBaseAPI.java

In this class I have implemented retrofit api to get response from firabase as a json string.

# Contributing

To contribute, clone the git repository and add any code you wish. Then create a pull request and wait for it to be apporved.

Please feel free to contact me for any problem or if you don’t understand any part or if you just want to say hello. (Contact me on <a href="https://www.linkedin.com/in/mudasiryounas">Linkedin</a>)

# Screenshots:

<img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen4.jpg"  height="450" /> <img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen5.jpg"  height="450" /> <img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen6.jpg" height="450" /> <img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen7.jpg"  height="450" />

# Important Note:

The firebase account currently used in this application is public, which means anything you enter while testing the application is publicly available, you may keep using this account for testing purpose but please make sure you change the firebase account after testing.

## Official links:

- Android Studio: https://developer.android.com/studio
- Firebase: https://firebase.google.com/docs/database
- Firebase Android Setup: https://firebase.google.com/docs/database/android/start
- Retrofit2: https://square.github.io/retrofit/

## Reach me on: <a href="https://www.linkedin.com/in/mudasiryounas">LinkedIn</a>
