# BionicCow
Todo list client for Remember the Milk. Let the Bionic Cow organize your life!
GTD (Getting Things Done) users can benefit from Folders, consisting of groups of tags, lists and/or locations beginning with the same character pattern (fully configurable).
This product uses the Remember The Milk API but is not endorsed or certified by Remember The Milk.

I'm not able to work on this project so I have decided to release it as it is.
Feel free to fork it.
This is one of my first projects ever so I'm sorry that it has a very bad programming style and for comments in Italian language (I can provide translations).

## Instructions
First of all the imported library must be set. I made a very bad choice but, again, this is one of my fisrt projects as a programmer.

If you are using Android Studio I think it would be better to use a more modern library management, like Gradle.
If you are using Eclipse, you could import ActionBarSherlock 4.2 setting a correct path in project.properties, but I do not recommend it.

In order to communicate with the API an **API key** and a **shared secret** must be defined in `src/it/bova/bioniccow/data/ApiSingleton.java`.
```
private static final String API_KEY = "REPLACE_ME";
private static final String SHARED_SECRET = "REPLACE_ME";
```
The keys can be obtained [here](https://www.rememberthemilk.com/services/api/keys.rtm) 

