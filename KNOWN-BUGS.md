# Severity explained:

level 1: Balance issues or minor visual issues.

level 2: A nuisance for the user, either from a technical perspective or mechanical one.
         These bugs are a result of big semantic mistakes in the code.

level 3: Game breaking in nature. Will probably have the same origin as a level 2 bug.

# Bug template:
-*Title
Description: *Mandatory
Possible solution: *Optional
Cause: *Optional *guessing is allowed


# LEVEL 3:
# Crash on close
Description: App does not behave properly and/or crash when you close it and then open it
Possible solution:
Cause: On pause and resume does not deal with app state

# LEVEL 2:
# Stuck in corner
Description: When the balloon scales by a corner it can scale beyond the frame
Possible solution:
Cause: guess: Balloon can exit the frame with scale/rotation

# LEVEL 1:

# Score is not synced
- Description: Picking up score and then dying in a short timeframe will yield wrong end score
- Possible solution:
- Cause: Score is not retrieved but "mocked" by the score class

# Upon dying shows the previous frame
- Description:  When you die you will probably see collisions that don't look correct
              as you see the previous frame
- Possible solution:
Cause: We do our last draw on the loop before checking if you died

# Spazzing
- Description: Balloon spaz when user are doing slow movement
- Possible solution:
- Cause: accelerometer give a low magnitude vector and therefore you can change rotation
       too fast.
