# Severity explained:

level 1: Balance issues or minor visual issues.

level 2: A nuisance for the user, either from a technical perspective or mechanical one.
         These bugs are a result of big semantic mistakes in the code.

level 3: Game breaking in nature. Will probably have the same origin as a level 2 bug.


###### Bug template:
- Description: *Mandatory
- Possible solution: *Optional
- Cause: *Optional *guessing is allowed


## LEVEL 3:

##### Crash on close
- Description: App does not behave properly and/or crash when you close it and then open it
- Possible solution:
- Cause: On pause and resume does not deal with app state
- State: If user close app, and then open again, app will start from main menu

##### Possible memory leak on play/menu transition
- Description: Going from play to menu, the app over time slows down, slow on input, slow on loading, eventually goes into a total crash.
- Possible solution:
- Cause: Guess: Memory leak, possibly from bitmaps of some sort. Dead menu or game bitmaps?

##### Not able to login on release apk
- Description: Debug apk lets you login to Google Play Services, but release apk does not
- Possible solution: Add an extra Linked App with the proper release signed certificate
- Cause: Probably that the oauth2 client id doesn't match the release apk certificate.

## LEVEL 2:

###### Main Menu landscape not working
- Description: When viewing the main menu in landscape mode, there are issues with not all buttons being displayed
- Possible solution: Fix XML? Change to a different UI? Or force that landscape isn't possible
- Cause: 

##### Global Leaderboards not working properly
- Description: Sometimes, certain units will not connect properly to the global Leaderboards
- Possible solution: Change the way we do login to Google Play (/or do a different check when trying to load leaderboards)
- Cause: Guess: Possibly not connected properly to Google Play, or isSignedIn()-function is not working properly

##### Stuck in corner
- Description: When the balloon scales by a corner it can scale beyond the frame
- Possible solution:
- Cause: guess: Balloon can exit the frame with scale/rotation

##### Phone goes to 'sleep' while playing
- Description: While playing the screen fades and eventually phone goes to sleep.
- Possible solution:
- Cause: guess: We are not dealing properly with media mode?

##### Scoring offline
- Description: If you get a new highscore, it wont be saved in the future in the cloud
- Possible solution:
- Cause: not sending highest local score when online

## LEVEL 1:

##### Score is not synced
- Description: Picking up score and then dying in a short timeframe will yield wrong end score
- Possible solution:
- Cause: Score is not retrieved but "mocked" by the score class

##### Upon dying shows the previous frame
- Description:  When you die you will probably see collisions that don't look correct
              as you see the previous frame
- Possible solution:
- Cause: We do our last draw on the loop before checking if you died

##### Spazzing/Jittering balloon
- Description: Balloon spaz when user are doing slow movement
- Possible solution: Lerp the newAngle for rotation // Include Z-axis gravity for noise-cancellation? // Use of GYROSCOPE sensor or GAME_ROTATION_VECTOR?
- Cause: accelerometer give a low magnitude vector and therefore you can change rotation
       too fast.
