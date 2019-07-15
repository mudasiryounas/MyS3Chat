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

<img scr="https://github.com/mudasiryounas/MyS3Chat/blob/master/screenshots/screen2.jpg" />

This will be our main activity where the user first lands, In this activity we will be first checking if user is logged in and redirecting him to ActivityLogin if not logged in. After successfully logging in, the first thing we will be showing on our application’s main activity is chat history (if there is any), that’s what any user will be looking for. 

At the very first login, we fecth data from firabase and store in local db using SQLite, and at the later we do fetch chat history from end user’s local storage.

To avoid main thread freezing, each request to network is made in a new task.















