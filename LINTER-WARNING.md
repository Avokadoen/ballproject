# Linter warning explanation
## This document is for pointing out known linter warning and why they are not solved.

### Android Lint: Usability:
- Png in drawable folder: We saw it fit to ignore as we don't use any vector graphics, and don't have assets of same type with different pixel density and/or ratio.
- No firebase indexing: The app is a client sided game with minimal web elements. You can find the app through google play anyways.

### Declaration redundancy
- Parameter sent with constant: We do this for greater clarity of what they do. In theory it would be easier to modify code in the future as classes can change in behaviour.

### Probable bugs
- Constant conditions & exceptions: We did our best to mitigate possible null pointer from wakelock, but android studio did not like any of our solutions. After quite some googling we deemed it something to look at in the future. As for the constants, they are needed to change state of our game thread and we don't really see any problem with sending it in.
- Suspicious variable combinations: Because we force landscape x and y is flipped for our logic. Of course android studio is not happy about this as we spit "x" variable in a "y" parameter but it is needed  to translate our coordinates.

### Spelling
- This is a issue we have mainly ignored as android is not perfect at accepting standard variable names.
