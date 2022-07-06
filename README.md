## Incorrect Password Blocker


####Tools used: 
  * [Java Corretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) 
  * [Gradle 7.4.2](https://gradle.org/install/#with-a-package-manager)
  * [AssertJ](http://joel-costigliola.github.io/assertj/)    
  

#### To run the application:
    ./gradlew run
    
#### You can also add a parameter to the gradle task in order to have cleaner CLI interaction:
    ./gradlew run --console=plain

#### To test the application:
    ./gradlew clean test

#### To build the application:
    ./gradlew clean build

#### To run the jar after the build:
    java -jar app/build/libs/app.jar
