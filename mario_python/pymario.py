#########################################################################################
# PyMario: A simple Mario side-scroller written with Pyhton using Pygame API
# Programming Paradigms Assignment 9, Manuel Serna-Aguilera
#########################################################################################

import pygame
import time
import random

from pygame.locals import *
from time import sleep

SCREEN_W = 900
SCREEN_H = 650

#========================================================================================
# Sprite class
#========================================================================================
class Sprite():
    def __init__(self, inputX, inputY, inputW, inputH):
        # Initialze positions and dims
        self.xPos = inputX
        self.yPos = inputY
        self.width = inputW
        self.height = inputH
        
        # Scrolling position, adjust when handling collisions and sprite overlapping
        self.scrollPos = 0
        
    #------------------------------------------------------------------------------------
    # Detect collision between this sprite and another sprite
    # Logic: if this sprite is not outside of that sprite, then this sprite must be inside of that sprite
    #------------------------------------------------------------------------------------
    def collision(self, that):
        return(not(self.xPos + self.width <= that.xPos - self.scrollPos) and not(self.xPos >= that.xPos + that.width - self.scrollPos) and not(self.yPos + self.height <= that.yPos) and not(self.yPos >= that.yPos + that.height))

#========================================================================================
# Background class
#========================================================================================
class Background(Sprite):
    def __init__(self, inputX, inputY):
        super().__init__(inputX, inputY, SCREEN_W, SCREEN_H)
        
        self.frame0 = pygame.image.load("background.png")
    
    #------------------------------------------------------------------------------------
    # Update brackground coordinates with master scrolling position
    #------------------------------------------------------------------------------------
    def update(self, masterScrollPos):
        self.scrollPos = masterScrollPos
        
    #------------------------------------------------------------------------------------
    # Draw background
    #------------------------------------------------------------------------------------
    def draw(self, screen):
        screen.blit(self.frame0, (self.xPos - self.scrollPos, 0))

#========================================================================================
# Mario class
#========================================================================================
class Mario(Sprite):
    def __init__(self, inputX, inputY, inputM):
        super().__init__(inputX, inputY, 60, 95)
        # Be able to access model to check for collisions
        self.model = inputM
        
        # Direction flags and frame counters
        self.goingRight = False
        self.rightCounter = 0
        self.goingLeft = False
        self.leftCounter = 0
        
        # Downward acceleration
        self.downAcceleration = 2.2
        
        self.yVelocity = 0.0 # vertical velocity
        self.jumpFrames = 0 # number of frames in the air, determines if the player can jump
        
        # Offset location to see which way the player sprite moved
        self.locationOffset = (inputM.MASTER_SCROLL_SPEED + 1)
        
        # All player sprite frames
        self.targetImage = None
        self.frame0 = pygame.image.load("mario0.png") # still
        self.frame1 = pygame.image.load("mario1.png") # right frame 1
        self.frame2 = pygame.image.load("mario2.png") # right frame 2
        self.frame3 = pygame.image.load("mario3.png") # left frame 1
        self.frame4 = pygame.image.load("mario4.png") # left frame 2
        
    #------------------------------------------------------------------------------------
    # Update player mechanics
    #------------------------------------------------------------------------------------
    def update(self, masterScrollPos):
        self.scrollPos = masterScrollPos
        
        # Update gravity
        self.yVelocity += self.downAcceleration
        self.yPos += self.yVelocity
        
        # If Mario hits ground, keep him from falling on the floor
        if self.yPos >= self.model.floor:
            self.jumpFrames = 0
            self.yVelocity = 0.0
            self.yPos = self.model.floor
            
        self.jumpFrames += 1
        
        # Handle collisions
        for s in self.model.sprites:
            
            # Check for collision with anything not itself and the background (go for type method, since all sprites are of type Sprite
            if self.collision(s) and (type(s) is not Background):
                if type(s) is CoinBlock:
                    s.isHit = True
                
                if type(s) is Brick or type(s) is CoinBlock:
                    self.getOut(s)
            else:
                self.model.setMasterScrollSpeed(self.model.MASTER_SCROLL_SPEED)
                
                if type(s) is CoinBlock:
                    s.isHit = False
        
        # Adjust master scrolling position
        self.model.setMasterScrollPos(self.scrollPos)
    
    #------------------------------------------------------------------------------------
    # Draw and animate the player sprite
    #------------------------------------------------------------------------------------
    def draw(self, screen):
        if self.goingRight:
            if self.rightCounter % 9 == 0:
                self.targetImage = self.frame1
                
            elif self.rightCounter % 9 == 4:
                self.targetImage = self.frame2
                
            self.rightCounter += 1
            
        elif self.goingLeft:
            if self.leftCounter % 9 == 0:
                self.targetImage = self.frame3
                
            elif self.leftCounter % 9 == 4:
                self.targetImage = self.frame4
                
            self.leftCounter += 1
            
        else:
            self.rightCounter = 0   
            self.leftCounter = 0
            
            self.targetImage = self.frame0
        
        # Finally draw Mario image
        screen.blit(self.targetImage, (self.xPos, self.yPos))
    
    #------------------------------------------------------------------------------------
    # Prevent sprite overlap
    # If the player collides with an object in-game, make sure player sprite does not go through said object
    #------------------------------------------------------------------------------------
    def getOut(self, that):
        # Given this sprite and "that" sprite
        # Condition 1: check if right-side of this sprite overlaps with left side of that sprite
        # Condition 2: confirm that this sprite was coming from the left by offsetting current position left
        
        # LEFT side of that sprite
        if (self.xPos + self.width >= that.xPos - self.scrollPos) and (self.xPos + self.width - self.locationOffset < that.xPos - self.scrollPos):
            self.model.setMasterScrollSpeed(0)
            
            # Position = difference between overlapping entities
            self.scrollPos = that.xPos - (self.xPos + self.width)
            
            self.model.setMasterScrollPos(self.scrollPos)
            
        # RIGHT side of that
        elif (self.xPos <= that.xPos + that.width - self.model.getMasterScrollPos()) and (self.xPos + self.locationOffset > that.xPos + that.width - self.model.getMasterScrollPos()):
            self.model.setMasterScrollSpeed(0)
            self.scrollPos = that.xPos + that.width - self.xPos
            
            self.model.setMasterScrollPos(self.scrollPos)
            
        # BOTTOM of that
        elif (self.yPos <= that.yPos + that.height) and (self.yPos + self.height > that.yPos + that.height):
            self.yVelocity = 0.0 # reset down velocity for this
            self.yPos = that.yPos + that.height + 1 # reposition this sprite
            
        # TOP of that
        elif (self.yPos + self.height >= that.yPos) and (self.yPos - self.locationOffset < that.yPos):           
            self.yVelocity = 0.0
            self.jumpFrames = 0
            self.yPos = that.yPos - self.height

#========================================================================================
# Brick class
#========================================================================================
class Brick(Sprite):
    def __init__(self, startX, startY, startW, startH):
        super().__init__(startX, startY, startW, startH)
        
        # Load image and stretch it according to width and height
        self.frame0 = pygame.image.load("brick.png")
        self.frame0 = pygame.transform.scale(self.frame0, (self.width, self.height))
    
    #------------------------------------------------------------------------------------
    # Update brick coordinates
    #------------------------------------------------------------------------------------
    def update(self, masterScrollPos):
        self.scrollPos = masterScrollPos
    
    #------------------------------------------------------------------------------------
    # Draw brick
    #------------------------------------------------------------------------------------
    def draw(self, screen):
        screen.blit(self.frame0, (self.xPos - self.scrollPos, self.yPos))

#========================================================================================
# Coin Block class
#========================================================================================
class CoinBlock(Sprite):
    def __init__(self, startX, startY, inputM):
        super().__init__(startX, startY, 64, 64)
        
        self.model = inputM
        
        # Max number of coins this block can hold
        self.coinLimit = 5
        
        # Has a distinct coin block instance been hit?
        self.isHit = False
        
        # Coin block state frames, non-empty and empty
        self.frame0 = pygame.image.load("coin_block.png")
        self.frame1 = pygame.image.load("empty_coin_block.png")
        self.targetImage = self.frame0
    
    #------------------------------------------------------------------------------------
    # Update coin block mechanics
    #------------------------------------------------------------------------------------
    def update(self, masterScrollPos):
        self.scrollPos = masterScrollPos
        
        # Keep track of the number of coins a block has
        for s in self.model.sprites:
            if (type(s) == Mario) and (self.yPos + self.height <= s.yPos) and (self.yPos + self.height + 2 > s.yPos) and (self.isHit == True):
                if self.coinLimit > 0:
                    # Add a coin to the sprites list
                    c = Coin(self.xPos + self.width/4, self.yPos + self.height/4, len(self.model.sprites))
                    
                    self.model.sprites.append(c)
                    self.coinLimit -= 1 # reduce number of coins spawnable
            
            # Replace image to indicate that no more coins will pop out
            if self.coinLimit == 0:
                self.targetImage = self.frame1
        
        # Remove coins from array list when they get out of bounds. Go down the list since the size will most likely decrease with deletions
        for s in self.model.sprites:
            if type(s) is Coin and s.yPos > SCREEN_H:
                self.model.sprites.remove(s)
        
    #------------------------------------------------------------------------------------
    # Draw active or inactive coin block
    #------------------------------------------------------------------------------------
    def draw(self, screen):
        screen.blit(self.targetImage, (self.xPos - self.scrollPos, self.yPos))

#========================================================================================
# Coin class
#========================================================================================
class Coin(Sprite):
    def __init__(self, inputX, inputY, index):
        super().__init__(inputX, inputY, 20, 31)
        
        # Vertical velocity components
        self.maxYVelocity = 6.5
        self.minYVelocity = 3.2
        self.yVelocity = 0.0
        self.down_accel = 2.0
        
        # Horizontal velocity components
        self.maxXVelocity = 2.5
        self.minXVelocity = 1.7
        self.xVelocity = 0.0
        
        self.jumpFrames = 5
        
        self.frame0 = pygame.image.load("coin.png")
        
        # Assign random velocity when coin shoots out of block
        if index % 2 == 0:
            self.xVelocity = -random.uniform(self.minXVelocity, self.maxXVelocity) # coin lauches left
        else:
            self.xVelocity = random.uniform(self.minXVelocity, self.maxXVelocity) # coin lauches right
    
    #------------------------------------------------------------------------------------
    # Update coordinates of falling coin once spawned
    #------------------------------------------------------------------------------------
    def update(self, masterScrollPos):
        self.scrollPos = masterScrollPos
        
        # Add constant x velocity
        self.xPos += self.xVelocity
        
        # Gravity mechanic, start with random y velocity
        if self.jumpFrames > 0:
            self.yVelocity -= random.uniform(self.minYVelocity, self.maxYVelocity)
            
        self.yVelocity += self.down_accel
        self.yPos += self.yVelocity
        
        self.jumpFrames -= 1
    
    #------------------------------------------------------------------------------------
    # Draw flying coin
    #------------------------------------------------------------------------------------
    def draw(self, screen):
        screen.blit(self.frame0, (self.xPos - self.scrollPos, self.yPos))

#========================================================================================
# Model class: maintains collection of sprites
#========================================================================================
class Model():
    def __init__(self):
        # Player sprite width and height
        self.spriteWidth = 60
        self.spriteHeight = 95
        
        # Posiion on the floor
        self.floor = (SCREEN_H - 63 - 95) # tot height - floor - player height
        
        self.MASTER_SCROLL_SPEED = 6
        
        self.masterScrollPos = 0
        self.masterScrollSpeed = self.MASTER_SCROLL_SPEED
        
        # Make a collection of sprites
        self.sprites = []

        # TODO: load a map layout and load sprites into array
        bg = Background(-200, 0)
        self.mario = Mario(SCREEN_W/2, self.floor, self)
        
        self.sprites.append(bg)
        self.sprites.append(self.mario)
        
        b1 = Brick(583, 525, 68, 70)
        b2 = Brick(649, 448, 68, 86)
        b3 = Brick(718, 370, 59, 91)
        b4 = Brick(650, 524, 71, 57)
        b5 = Brick(714, 434, 70, 89)
        b6 = Brick(718, 524, 69, 59)
        b7 = Brick(161, 523, 68, 56)
        
        cb1 = CoinBlock(159, 294, self)
        cb2 = CoinBlock(330, 364, self)
        cb3 = CoinBlock(396, 365, self)
        cb4 = CoinBlock(791, 362, self)
        
        self.sprites.append(b1)
        self.sprites.append(b2)
        self.sprites.append(b3)
        self.sprites.append(b4)
        self.sprites.append(b5)
        self.sprites.append(b6)
        self.sprites.append(b7)
        
        self.sprites.append(cb1)
        self.sprites.append(cb2)
        self.sprites.append(cb3)
        self.sprites.append(cb4)
        
    #------------------------------------------------------------------------------------
    # Setters
    #------------------------------------------------------------------------------------
    def setMasterScrollPos(self, inputPos):
        self.masterScrollPos = inputPos
    
    def setMasterScrollSpeed(self, inputSpeed):
        self.masterScrollSpeed = inputSpeed
    
    #------------------------------------------------------------------------------------
    # Getters
    #------------------------------------------------------------------------------------
    def getMasterScrollPos(self):
        return self.masterScrollPos
    
    def getMasterScrollSpeed(self):
        return self.masterScrollSpeed
    
    #------------------------------------------------------------------------------------
    # Update all sprites in the array
    #------------------------------------------------------------------------------------
    def update(self):
        for i in self.sprites:
            i.update(self.masterScrollPos)

    #------------------------------------------------------------------------------------
    # Add a brick to the sprites array
    #------------------------------------------------------------------------------------
    def placeBrick(self, startX, startY, startW, startH):
        brick = Brick(startX, startY, startW, startH)
        self.sprites.append(brick)

    #------------------------------------------------------------------------------------
    # Add a coin block to the sprites array
    #------------------------------------------------------------------------------------
    def placeCoinBlock(self, startX, startY):
        cb = CoinBlock(startX, startY, self)
        self.sprites.append(cb)
    
#========================================================================================
# View class: draw every sprite
#========================================================================================
class View():
    def __init__(self, model):
        # Set screen size
        screen_size = (SCREEN_W, SCREEN_H)
        self.screen = pygame.display.set_mode(screen_size, 32)
        
        self.screenStartPos = -200
        
        self.model = model

    #------------------------------------------------------------------------------------
    # Update drawing all sprites on the screen
    #------------------------------------------------------------------------------------
    def update(self):
        self.screen.fill([0,255,0])
        for i in self.model.sprites:
            i.draw(self.screen)
        pygame.display.flip()

#========================================================================================
# Controller class: listen for user input and update sprite coords
#========================================================================================
class Controller():
    def __init__(self, model):
        self.model = model
        self.keep_going = True
        
        self.key_right = False
        self.key_left = False
        self.key_space = False
        
        self.x1 = 0
        self.x2 = 0
        self.y1 = 0
        self.y2 = 0

    #------------------------------------------------------------------------------------
    # Keep listening for user input
    #------------------------------------------------------------------------------------
    def update(self):        
        for event in pygame.event.get():
            # listen if user exits
            if event.type == QUIT:
                self.keep_going = False
            
            # listen for user pressing keys
            elif event.type == KEYDOWN:
                if event.key == K_ESCAPE:
                    self.keep_going = False
                if event.key == K_RIGHT:
                    self.key_right = True
                if event.key == K_LEFT:
                    self.key_left = True
                if event.key == K_SPACE:
                    self.key_space = True
                    
            # Key is released
            elif event.type == KEYUP:
                if event.key == K_RIGHT:
                    self.key_right = False
                    self.model.mario.goingRight = False
                    
                if event.key == K_LEFT:
                    self.key_left = False
                    self.model.mario.goingLeft = False
                    
                if event.key == K_SPACE:
                    self.key_space = False
            
            # Mouse button was pressed
            elif event.type == MOUSEBUTTONDOWN:
                self.x1 = pygame.mouse.get_pos()[0]
                self.y1 = pygame.mouse.get_pos()[1]
                
            elif event.type == MOUSEBUTTONUP:
                self.x2 = pygame.mouse.get_pos()[0]
                self.y2 = pygame.mouse.get_pos()[1]
                
                # Differentiate between making a brick or coin block...
                # Get corners
                self.left = min(self.x1, self.x2)
                self.right = max(self.x1, self.x2)
                self.top = min(self.y1, self.y2)
                self.bottom = max(self.y1, self.y2)
                
                # Place a decently-sized brick on the screen, else, make a coin block
                if (self.right - self.left > 20) and (self.bottom - self.top > 20):                   
                    self.model.placeBrick(self.left + self.model.getMasterScrollPos(), self.top, self.right - self.left, self.bottom - self.top)
                else:
                    self.model.placeCoinBlock(self.x1 + self.model.getMasterScrollPos(), self.y1)
        
        # Move master scrolling position
        if self.key_right == True:
            self.model.setMasterScrollPos(self.model.getMasterScrollPos() + self.model.getMasterScrollSpeed())
            self.model.mario.goingRight = True
            
        if self.key_left == True:
            self.model.setMasterScrollPos(self.model.getMasterScrollPos() - self.model.getMasterScrollSpeed())
            self.model.mario.goingLeft = True
            
        if self.key_space == True:
            if self.model.mario.jumpFrames < 4:
                self.model.mario.yVelocity -= 10.1

#========================================================================================
# Start the game
#========================================================================================
print("---------------------------------------------------------------------------")
print("It's-a-Me, Mario!")
print("Use the left and right arrow keys to move. Press space to jump.")
print("Click and draw your mouse to create bricks, click to create coin blocks!")
print(" > Press Esc to quit.")
print("---------------------------------------------------------------------------")

pygame.init()

m = Model()
v = View(m)
c = Controller(m)

# Enter game loop
while c.keep_going:
    c.update()
    m.update()
    v.update()
    pygame.time.delay(20)

print("Thanks for playing! Bye Bye!")
