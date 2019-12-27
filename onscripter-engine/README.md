# ONScripter Engine for Android
This project is forked from the original author at Studio O.G.A (http://onscripter.osdn.jp/onscripter.html).
This fork is suppose to fix many issues and play almost any ONScripter game of any language.

The original project only supported Japanese text however my project supports
Japanese, English, Korean, Chinese, and UTF-8 (like Spanish, Russian, French, German etc)
which covers the bulk of Europe and other non-Asian countries.


## Changes/Fixes from original ONScripter

- Fixes a bunch of crashes
- Supports higher quality audio
- Multiple languages which is detected at runtime (Japanese, English, Korean, Chinese, UTF-8)
- Android logging and ndk error handling
- When loading corrupted save file, it will not crash
- Compatible with ONScripter-EN
- Supports building for newer versions of Android (the original has issues with newer versions of Android)
- Supports using .nsa and .sar resource files in the same directory
- Handles font size differently to allow increasing font size
- Buildable with Android studio with CMake and the newest NDK versions (and 64bit as well)
- Get events to Java such as game ready, load, state changes (auto, text speed, and skip)
- Cleanly exit ONScripter and not crash
- Optionally save screenshots to folder
- Supports SDCards/external devices and [Android Scoped Storage](https://developer.android.com/preview/privacy/scoped-storage)

## Sample integration

This is a very simple example of enabling ONScripter in your app with events.

    public class GameActivity extends Activity {
        private ONScripterView mGame;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Defined uri either content:// (external devices like sdcard) or file://
            Uri uri = Uri.fromFile(new File("/path/to/folder/with/game"));

            mGameView = new ONScripterView.Builder(getContext(), uri)
                    // If you specify a screenshot folder name, relative to the save folder in game,
                    // full sized screenshots are saved after each save
                    .setScreenshotPath("")
                    // Outline of text
                    .useRenderOutline()
                    // Plays higher quality audio
                    .useHQAudio()
                    // Set a default font path
                    .setFontPath(defaultFontPath)
                    .create();
            setContentView(mGame);

            // [Optional] Receive Events from the game
            mGame.setONScripterEventListener(new ONScripterView.ONScripterEventListener() {
                @Override
                public void autoStateChanged(boolean selected) {
                    // User has toggled auto mode
                }

                @Override
                public void skipStateChanged(boolean selected) {
                    // User has toggled skip mode
                }

                @Override
                public void singlePageStateChanged(boolean selected) {
                    // User has toggled single page mode
                }

                @Override
                public void onUserMessage(UserMessage messageId) {
                    if (messageId == ONScripterView.CORRUPT_SAVE_FILE) {
                        Toast.makeText(GameActivity.this, "Cannot open save file, it is corrupted",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void videoRequested(String filename, boolean clickToSkip, boolean shouldLoop) {
                    // Request playing this video in an external video player
                    // If you have your own video player built into your app, you can
                    // pause this thread and play the video. Unfortunately I was unable
                    // to get smpeg library to work within this library
                    try {
                        String filename2 = filename.replace('\\', '/');
                        Uri uri = Uri.parse(filename2);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setDataAndType(uri, "video/*");
                        startActivityForResult(i, -1);
                    }
                    catch(Exception e){
                        Log.e("ONScripter", "playVideo error:  " + e.getClass().getName());
                    }
                }

                @Override
                public void onReady() {
                    Log.w("ONScripter", "Game is ready");
                    // Load save file, save1.dat
                    // mGame.loadSaveFile(1);
                }

                @Override
                public void onNativeError(NativeONSException e, String line, String backtrace) {
                    Toast.makeText(GameActivity.this, "An error has occured: " + line, Toast.LENGTH_SHORT).show();
                    Log.w("ONScripter", backtrace);
                }

                @Override
                public void onUserMessage(ONScripterView.UserMessage messageId) {
                    if (messageId == ONScripterView.UserMessage.CORRUPT_SAVE_FILE) {
                        Toast.makeText(ONScripterActivity.this, "Cannot open save file, it is corrupted",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onGameFinished() {
                    // Game ended
                    finish();
                }
            });

            // Center the game in the middle of the screen
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mGame.getLayoutParams();
            p.gravity = Gravity.CENTER;
            mGame.setLayoutParams(p);

            // Set black background behind the engine
            findViewById(android.R.id.content).setBackgroundColor(Color.BLACK);
        }

        @Override
        protected void onPause() {
            super.onPause();
            if (mGame != null) {
                mGame.onPause();
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (mGame != null) {
                mGame.onResume();
            }

            // Set immersive mode
            getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (mGame != null) {
                mGame.onStop();
            }
        }

        @Override
        public void onDestroy() {
            // DO NOT EXIT APP HERE, DO IT BEFORE OR PREVIOUS ACTIVITY WILL FREEZE, will fix one day
            super.onDestroy();
        }
    }

## API Features

Most of the features are specified in [ONScripterView.java](https://github.com/matthewn4444/onscripter-engine-android/blob/master/src/com/onscripter/ONScripterView.java)

**setONScripterEventListener(ONScripterEventListener listener)**

- Specify an event listener to get all the events from the C++ side

**sendNativeKeyPress(int keycode)**

- Run a key event to SDL
- *KeyEvent.KEYCODE_O*: toggle text speed
- *KeyEvent.KEYCODE_S*: toggle skip mode
- *KeyEvent.KEYCODE_A*: toggle auto mode
- *KeyEvent.KEYCODE_BACK*: simulate right click
- *KeyEvent.KEYCODE_DPAD_LEFT*: simulate scrolling up
- *KeyEvent.KEYCODE_DPAD_RIGHT*: simulate scrolling down

**getGameFontSize()**

- Returns the pixel size of the font currently in game

**getGameWidth()**

- Returns the game's width (not the width the view is taking up). For most games it may be 680px
- Use to calculate and resize the game to your activity

**getGameHeight()**

- Returns the game's height (not the height the view is taking up). For most games it may be 480px
- Use to calculate and resize the game to your activity

**setFontScaling(double scaleFactor)**

- Input a scaling factor for the text where 1.0 is default

**loadSaveFile(int no)**

- Loads a save file in the save folder with the number represented by *save<no>.dat*. Make sure this happens after *onReady()*

## Cloning and Building

1. Clone the project to the root directory of your project:

    ``https://github.com/matthewn4444/onscripter-engine-android.git``

2. Add this project to your Android Studio project either by adding as module or
   add this in **settings.gradle**

    ``include ':onscripter-engine-android'`

    Also include as dependency in **build.gradle**

    ```
    dependencies {
        ...
        compile project(':onscripter-engine-android')
        ...
    }
    ```
3. If asked for NDK directory, you should specify it

4. Sync and build project

## License
Copyright (C) 2019 Matthew Ng
Licensed under the Apache license

onscripter, bzip, freetype, jpeg, libmad, lua, png, sdl, sdl_image, sdl_mixer, sdl_ttf, and tremor are distributed on theirs own license.

## Credits
This library was modified by Matthew Ng from the original author Studio O.G.A. (http://onscripter.osdn.jp/onscripter.html)
