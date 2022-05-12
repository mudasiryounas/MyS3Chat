![MyS3Chat](https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen1.png)


# MyS3Chat – Complete Open Source Real Time Android Chat Application using Firebase

In this tutorial we will be developing real time chat application using firebase, At the end of this tutorial, you will have a complete working android application ready to be published on playstore, exited? Let’s get started.

<a target="_blank" href="https://play.google.com/store/apps/details?id=com.mys3soft.mys3chat"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="80"/></a>


This tutorial assume that you have basic knowledge of Java and Android SDK, you have build through small android appplications and you are ready to apply your learning on some real projects.


Prerequisites: 
*	Android studio is installed and Android SDK is configured
*	Android Physical or virtual device is ready for testing our application
*	An account of firebase real-time database

## What is firebase real-time datatabse?
The Firebase Realtime Database is a cloud-hosted NoSQL database that lets you store and sync data between your users in realtime. We will be using this datatabse to sync data between users, and Show notification whenever new message arrive.

## What is Retrofit:
Retrofit is an http cliend for android, we will be using this library to parse firebase responses.

## What is SQLite:
SQLite is local database which we will use to store data already fetched from server to avoid sending sending request each time a use open a chat acticity.

Alright, so i will not be going to each code and explain what the code is doing rather to explain some important classes and leave rest for you play with.

## ActivityMain.java

<img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen2.jpg" height="450" />

This will be our main activity where the user first lands, In this activity we will be first checking if user is logged in and redirecting him to ActivityLogin if not logged in. After successfully logging in, the first thing we will be showing on our application’s main activity is chat history (if there is any), that’s what any user will be looking for. 

At the very first login, we fecth data from firabase and store in local db using SQLite, and at the later we do fetch chat history from end user’s local storage.

To avoid main thread freezing, each request to network is made in a new task.


## ActivityChat.java
<img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen3.jpg" height="450" />

After successfully showing chat history user can click on any friend’chat and we will send him to chat activity. If user do not have any contacts he can g oto search option, where he can search by name, email and send connection request.

## AppService.java
This class is responsible for listening to firebase and Show user a notication whenever there is any new activity, new message, new connection request etc.

DataContext.java
This is the class where we handle all local data storage for getting chat history, getting contact lists, deleteing contact, saving new message to local storage etc.

IFireBaseAPI.java
İn this class i have iplemented retrofit api to get response from firabase as a json string. 


That’s it, clone the Project from github, build on your android studio and there you have complete working android application, as i promised.



This Project is not very complex, but it’s also not very easy to understand if you have just landed your feet on android World.

Please feel free to contact me for any problem or if you don’t understand any part or if you just want to say hello. (Contact me on <a href="https://www.linkedin.com/in/mudasiryounas">Linkedin</a>)


# Screenshots:

<img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen4.jpg"  height="450" /> <img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen5.jpg"  height="450" /> <img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen6.jpg" height="450" /> <img src="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen7.jpg"  height="450" />


# Important Note:
The firebase account currently used in this application is public, which means anything you enter while testing the application is publicly available, you may keep using this account for testing purpose but please make sure you change the firebase account after testing.



## Official links:
* Android Studio: https://developer.android.com/studio
* Firebase: https://firebase.google.com/docs/database
* Firebase Android Setup: https://firebase.google.com/docs/database/android/start
* Retrofit2: https://square.github.io/retrofit/


# Follow me on 
<a href="https://www.linkedin.com/in/mudasiryounas"><img src="http://icons.iconarchive.com/icons/limav/flat-gradient-social/48/Linkedin-icon.png"></a>







