# Project Report

## Development Process
Initially we had a discussion about what we wanted to create, we thought that the lab3 expansion was pretty open as to what we could create,
and wanted to go from that base and make a game that we would like ourselves. Furthermore we also wanted to create a game where it would be
possible to compete for highscore against friends. So we wanted to have a pretty simple idea, so that it is also easier to expand upon
if we would want that.

So after initial discussion we ended up with the idea of inverting the ball velocity, so that the input would be "mirrored", and it would
simulate some kind of a "floating"/flying balloon. And for game objective we wanted some objects that the player has to avoid, and some items
to collect for score. Which we found out that oxygen fits in pretty well, as a mechanic it gives score and also makes the balloon bigger.
So that the game get's harder over time when player's increase in score.

So first we sketched some things on a blackboard, to get an overview of what we wanted and needed to include in this project.
When we started developing, we mostly went for a pair-programming approach, as we felt this was appropriate for the scale of this project,
and that we all would learn most of the concepts as good as possible.

Then during development we would have internal discussions about how we would implement different stuff, and how we would approach different problems.
At the end of each day, we usually tried to wrap up what we had done, and what we would start working on the next day. We would keep and maintain
a "To-Do"-list, and a "Known-Bugs"-list, to get some overview of things that we would have to work on.

## Design
<img src="https://github.com/Avokadoen/ballproject/blob/master/assets%20project%20files/protoIcon_1_00013.png" height="320" width="320">
<img src="https://github.com/Avokadoen/ballproject/blob/master/assets%20project%20files/31899992_2369250123089014_7105271333868535808_n.png" height="720" width="1280">

## Features Included

#### Features/Technology used:
- Google Play Integration
- Using sensors for input
- Canvas/Bitmaps
- Git for version control
- MediaPlayer for sounds


##### Main Menu Activity
- "Play"
- "Local Leaderboard"
- "Global Leaderboard"
- "Preferences"
- "Exit"

##### Play - The game
- GameView running with a MainThread for game logic and drawing
- Spawning spike-objects from bottom or top-side of the screen. (Random start- and end-position)
- Spawning oxygen-pickups from bottom or top-side of the screen (Random start- and end-position)
- Obtaining an oxygen provides the player with +10 score, and increases the balloon size
- When player obtains score, the current score is displayed
- Positions on game-objects are done with a simple lerp that are then fed to a controller-class who combines bitmaps and applies them to a canvas
- Game-objects calculate a rotation based on their movement-vector
- Player/Balloon is rotated correctly based on it's current "gravity" from the sensor
- When the player dies, we display the score and a menu that let's the player "Retry" or go back to the Main Menu

##### Local Leaderboard
- Local leaderboard score are updated upon "death" in the game, and we retrieve it from a file
- Retrieving the user's top 10 scores, and displaying them in a list

##### Global Leaderboard
- The user connects with Google Play, and is presented with a global leaderboard
- Global leaderboard connected with our game through Google Play

##### Preferences
- The user has an option to not share their score globally


## To-Do List

[Our To-Do List](TODO-LIST.md)

[Our Known-Bugs List](KNOWN-BUGS.md)


## What was easy/hard?


##### Easy:
- Basic activities with XML
- Lerp of positions

##### Challenges:
- To handle threading and because of this make sure objects was dealt with correctly
- Couple of hiccups during Google Play Integration, as we also struggled a bit with what documentation to follow from Google. (Different information from multiple of their sources)


## Learned from this project:

- Google Play Integration
- Thread-Safe variables (AtomicInteger's, ConcurrentLinkedQueue's)
- Bitmaps takes a lot of performance -> achieving good performance can be hard (optimization is important)
- Saving some application data as files (rather than preferences)
- Android assets tool
