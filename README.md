WeatherGPS-Android
==================

Android app that extracts weather information for a zipcode/GPS from Weather Underground and displays it in a custom ListView

How To Run
----------

The WeatherGPS.apk is located inside the /bin folder. Simply sideload it onto your Android phone or emulator and you're good to go.

Coding Environment
------------------

Written, built and tested on [Eclipse-ADT (Android Development Tools)](http://developer.android.com/tools/sdk/eclipse-adt.html)

Description
-----------

WeatherGPS is an Android 4.2 app that takes the user's zipcode input or calculates it using the device's GPS, and then queries the Weather Underground database for weather information for that area. It extracts the JSON data and displays selected bits of importation information in a custom ListView. 

Weather Underground allows any developer make GET requests to their servers and get weather information by zipcode, city, etc. There is no authentication protocol, so the whole thing can be done with just a browser request if you want to test this. Type this on your browser and you should get back JSON weather information for Central Park in New York City, NY:
http://api.wunderground.com/api/API-KEY/conditions/q/10019.json (NOTE: You must have an API Key for this to work. My API Key has been removed from this project for obvious security reasons. However, the .apk should still work)

In case the user requests information for an invalid zipcode, Weather Underground will send back a JSON response with an error message. This scenario is also dealt with by the app and an appropriate response is displayed to the user.

The application itself contains 2 activities. "MainActivity" containts the homepage with a textbox for manually entering zipcodes and a button to process this. Another button is present to get information from the GPS. This button also initiates the Settings panel in case the user's GPS is off. The next activity is "GetWeather" where the REST query and ListView processing goes on. Since this is an Android 4.2 application, any networking has to be done outside the main thread. The reason for this requirement is that network calls are blocking and hence can hit performance unpredictably, and it has been in effect since Android 4.0. The requirement is fulfilled using an AsnycTask method, that outsources  any tasks requiring network access to an asynchronous process. This process can further drive the application after it is finished.

Since the time taken to GET information from a web resource can vary wildly, a ProgressDialog was eventually added to give the user some feedback as to what is happening. Instead of waiting on a blank page for random periods of time, the user is shown a "Loading..." message until the application is ready.

Screenshots
-----------

Screenshots of the application in action are available in the /screenshots folder.

Dependencies/Requirements
-------------------------

[Jackson Java JSON Processor](http://jackson.codehaus.org)
* jackson-annotations-2.1.1.jar
* jackson-core-2.1.1.jar
* jackson-databind-2.1.1.jar

Everything else is part of the standard libraries.
