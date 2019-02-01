Mario Game Collection
by Manuel Serna-Aguilera, University of Arkansas

This repository contains a collection of all the Mario games I have developed for my Honors Programming Paradigms course, taught by Michael Gashler PhD, Universiy of Arkansas, Fall 2018.
Below is a short description of each project.

mario_java
---------------------------------------
This project is written in Java using the Swing library.
The code uses the Model-View-Controller game architecture to implement the game.
Model updates all the sprite objects, Controller listens and handles user input, and then View updates the sprite locations on screen according to what was input.
The objects in the game include: 
    Mario: controlled by user
    bricks user can click and drag on the screen to place a brick
    coin blocks: user clicks on screen to place
    coins: if mario hits a coin block from below, coins pop out

The map is loaded from a JSON file, the JSON object's contents are unmarshalled and put into the sprites list in the Model class. The user can also edit or delete the map layout and save it, overwritting anything on the JSON file.
Here is a list of Mario's actions
    Run: the user presses the LR arrow keys to move
    Jump: press the space bar to jump, the strength of the jump depends on how long space is pressed

Note: mario_java was run using ItelliJ.


MarioGame
---------------------------------------
This is an Android port of my Mario game above. 
However, it is a much more basic implementation.
I was required to only make the Mario sprite appear, move (no collision detection was implemented), jump, and also place bricks.

Touch controls are implemented by "dividing" the screen into quadrants:
    QI:   No action
    QII:  Jump
    QIII: Go left
    QIV:  Go right

Note: MarioGame was developed with Android Studio.


mario_ai
---------------------------------------
This is the final version of the Mario game in Java.
In this project enumerates actions like: running, jumping, and both.
When the artificial intelligence is allowed to run, it evaluates every single possible action (in this case just three actions listed above) to obtain a score. The highest scored action is then performed in the Controller.
After Mario performs an action, it simulates futures with a decision tree with k branches each with a depth of d.
After implementing this simple brute-force approach, I gave the AI an obstacle to traverse.

Note: A private boolean flag dictates who is in control of movement.
Other Note: This project was developed with IntelliJ.


mario_js
---------------------------------------
After developing my Mario game in Java, I created a web version of it. 
This project is written in JavaScript (ECMAScript 6) and implements almost everything the Java version has.
Due to this shift from Java to JS, I hardcoded the map into the Model file, and given my time constraint, I could not implement a way for users to save their map layout.
The HTML file that houses the scripts places the game in a window, the rest of the page is blank. 
This game is hosted by my university’s own server called Turing, thus people can access it online.
Here is the link if you want to play!

http://csce.uark.edu/~mserna/JSMario/main.html


mario_client_server
---------------------------------------
This Mario project involves two JavaScript clients communicating with a Java server.
The project involves two clients, the first client controls Mario, while the second controls a turtle.
Each of these clients sends a JSON containing a certain client's client ID, sprite positions and how much they moved to the Java server as an http request.
The Java server parses this JSON object to extract the sent information, the server updates its own data about each of the clients.
The server then sends the client updated information about the other client.
That client receives the other client's information and updates its own view of the other client's sprite.
This allows for two people to interact with eachother.

Note: This project is not perfect, updating values at the rate I do makes the other sprites move with considerable lag. Then, for some reason, I could not fix a problem with a broken pipe error that appears semi-frequently.
This project works better in Mozilla Firefox web browser.


mario_python
---------------------------------------
This final version of my Mario games was written in Python.
This project does almost the same thing as my Java Mario project.
The key difference is that it, like the JavaScript project, the map is hardcoded and not drawn from a file due to time constraints.