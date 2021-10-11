# LWJGL_Flappy_Bird

This is a tweaked version of Flappy Bird to introduce more features for interesting gaming experience.
It is made for desktop platforms, which was originally created for a video tutorial on YouTube where Cherno demonstrates how to make this game from scratch, in about 3 hours including short explanations of what is being typed, and why. 

<br /><br />
  You can find the Youtube video [here](http://youtu.be/527bR2JHSR0). <br /> 

## Added Features
* Text Item, Hud classes for Text and Score rendering
* State machine format is adopted to process each state of game differently 
 : "Start Screen", "Running" and "Game over" 
* Collision detection / resolution
* The speed of pipe movement increases gradually 
## Dependencies
* `LWJGL 3` : Light Weight Java Game Library 3, which you can download [here](http://www.lwjgl.org/download). You'll have to manually add the JAR and native files to your classpath in order to build and run the game.
