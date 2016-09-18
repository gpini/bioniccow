# BionicCow
Android App Todo list client for Remember the Milk.

Available on [Google Play](https://play.google.com/store/apps/details?id=it.bova.bioniccow)

GTD (Getting Things Done) users can benefit from Folders, consisting of groups of tags, lists and/or locations beginning with the same character pattern (fully configurable).
This product uses the Remember The Milk API but is not endorsed or certified by Remember The Milk.

I'm not able to work on this project due to my job and other reasons, so I have decided to release it as it is.
Feel free to fork it.

This is one of my first projects ever so I'm sorry that it has a very bad programming style and for comments in Italian language (I can provide translations). Feel free to ask if you are interested in developing this app.

## Instructions
First of all the imported library must be set. I made a very bad choice but, again, this is one of my fisrt projects as a programmer.

If you are using Android Studio I think it would be better to use a more modern library management, like Gradle. An import for ActionBarSherlock 4.2 and [RtmApi](https://github.com/gpini/rtmapi-android) for android jars must be defined.
If you are using Eclipse, you could import ActionBarSherlock 4.2 setting a correct path in project.properties, but I do not recommend it.

In order to communicate with the API an **API key** and a **shared secret** must be defined in `src/it/bova/bioniccow/data/ApiSingleton.java`.
```
private static final String API_KEY = "REPLACE_ME";
private static final String SHARED_SECRET = "REPLACE_ME";
```
The keys can be obtained [here](https://www.rememberthemilk.com/services/api/keys.rtm) 

