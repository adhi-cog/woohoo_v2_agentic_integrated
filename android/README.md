# Project Title: Woohoo Hub App
### Project Start date:
### Project Owner:

> Project Description: It is an Android project for the Woohoo hub Devices and it is developed in java language with Android Studio IDE.

# Table of contents
* [Project Release info](#project-release-info)
* [Project General info](#project-general-info)
* [Screenshots](#screenshots)
* [Technologies](#technologies)
* [Built With](#built-with)
* [Setup](#setup)
* [Tests](#tests)
* [Deployment](#deployment)
* [Contributing ](#Contributing)
* [Authors](#authors)


## Project Release Info

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

### Version 1.20.1 - 1st Jan 2020
 *  Added Language Support Marathi
 *  Added Language Support Kannada
Design multilingual Search engine - Pending

### Version 1.15 - 31 Dec 2019
* Add Push Notifications messaging - Pending
* Profile page - Added Image compresssion & resize before upload 


## Project General info

This project is based on java language.Here we have used MVP Articture with a dagger for dependency injection.to test the application we have to build/generate an apk which will be installed in our woohoo device and then we can test it.

## Screenshots
![Example screenshot](./img/screenshot.png)

## Technologies

Mention all technologies/languages used in this project and their versions being used.
* Android studio : 3.5.2
* Java: 8
* Java JDK Version: jdk1.8.0_112
	
### Built With
Mention all frameworks used here
* Gradle Version: 5.4.1
* Gradle Plugin Version: 3.4.2
* CompileSdkVersion: 28
* BuildToolsVersion: 28.0.3
* Sensory SDK: 6.12.0
* Dagger Depedency Injection : 2.15
* Sensory SDK: 6.12.0
* Butterknife: 10.0.0
* Retrofit: 2.5.0
* Rxjava: 3.1.8
* Facebook Fresco: 0.14.1
* Twilio: 4.1.1
* Spotify-player-24-noconnect-2.20b
* Exoplayer:exoplayer-ui:r2.4.0
* Geckoview-nightly: 71.0.20190927094817
* Anrwatchdog: 1.3.0
* Lottie: 2.5.4
* Facebook Shimmer: 0.3.0
* Fabric: 1.27.0


## Prerequisites and Setup 
1. clone the android repo in your system.
2. install java JDK version 1.8.0_112.
3. install sensory SDK 
4. give the sensory path and version in the android gradle.properties file
```
SNSR_REPOSITORY=/Users/subhamnaik/Desktop/RevArun/TrulyHandsfreeSDK/TrulyHandsfreeSDK/6.12.0/m2repositorySNSR_LIB_VERSION=6.12.0
```
5. clean and build the project using android studio.


## Tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

1. In android studio go to build>Generate Signed bundle/apk>apk
2. enter your build credentials and select next 
3. select the destination folder
4. select build type
5. select both V1 and V2 signature 
6. click on finish 

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.



## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

