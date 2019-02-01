import java.util.ArrayList;

class Model
{
    // Note: depth and k defined above method evaluateAction
    private static final int DEPTH = 35;// 25, 30, 35, 40
    private static final int K = 8;// 8,10, 12, 15, 18, 20

    // Hold every sprite in a collection
    ArrayList<Sprite> sprites;

    // Add redundant Mario reference, easier to access
    Mario mario;

    // Master scroll position and (variable) scrolling speed
    private int masterScrollPos;
    private int masterScrollSpeed;

    // Screen dimensions
    private int screenWidth;
    private int screenHeight;

	Model(int inputWidth, int inputHeight)
	{
        // Initialize an empty list of sprites
        this.sprites = new ArrayList<>();

        masterScrollPos = 0;
        masterScrollSpeed = Sprite.SCROLL_CONST;

        // Screen size
        this.screenWidth = inputWidth;
        this.screenHeight = inputHeight;

        // Load a preset layout
        this.load("map.json");
	}

    //=================================================================================================================
	/* Copy Constructor

       Copy objects into new list, but does not reference the same objects.
       - Go through all sprites from the input model,
         add specific type of sprite based on return value of get method.

       * Note: coins NOT added
    */
    //=================================================================================================================
    Model(Model inputModel)
    {
        this.sprites = new ArrayList<>();

        for(int i = 0; i < inputModel.sprites.size(); i++)
        {
            // Background
            if(inputModel.sprites.get(i).isBackground())
            {
                this.sprites.add(new Background((Background) inputModel.sprites.get(i)));
            }
            // Mario
            else if(inputModel.sprites.get(i).isMario())
            {
                this.sprites.add(new Mario((Mario) inputModel.sprites.get(i), this));
            }
            // Brick
            else if(inputModel.sprites.get(i).isBrick())
            {
                this.sprites.add(new Brick((Brick) inputModel.sprites.get(i)));
            }
            // Coin Block
            else if(inputModel.sprites.get(i).isCoinBlock())
            {
                this.sprites.add(new CoinBlock((CoinBlock) inputModel.sprites.get(i), this));
            }
            // Coin
            else if(inputModel.sprites.get(i).isCoin())
            {
                this.sprites.add(new Coin((Coin) inputModel.sprites.get(i), sprites.size()));
            }
            // Sprite that is not accounted for, throw a runtime exception
            else
            {
                throw new RuntimeException("Unknown sprite type encountered");
            }
        }

        // Reference the Mario in this model using a redundant reference
        int i = 0;
        while( !sprites.get(i).isMario() ) { i++; }
        this.mario = (Mario)this.sprites.get(i);

        // Set scrolling position and speed variables
        masterScrollPos = inputModel.masterScrollPos;
        masterScrollSpeed = inputModel.masterScrollSpeed;

        // Screen size, copy integer literals
        this.screenWidth = inputModel.screenWidth;
        this.screenHeight = inputModel.screenHeight;
    }

    //=================================================================================================================
    // Setters
    //=================================================================================================================
    public void setMasterScrollPos(int inputPos)
    {
        this.masterScrollPos = inputPos;
    }

    public void setMasterScrollSpeed(int inputSpeed)
    {
        this.masterScrollSpeed = inputSpeed;
    }

    //=================================================================================================================
    // Getters
    //=================================================================================================================
    public int getMasterScrollPos()
    {
        return masterScrollPos;
    }

    public int getMasterScrollSpeed()
    {
        return masterScrollSpeed;
    }

    //=================================================================================================================
    // Update all sprites in the sprites collection
    //=================================================================================================================
    void update()
    {
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite s = sprites.get(i);
            s.update(masterScrollPos);
        }
    }

    //=================================================================================================================
    // Perform Action: perform action (type Action)
    //=================================================================================================================
    void performAction(Action a)
    {
        // Only run
        if(a == Action.RUN)
        {
            masterScrollSpeed = Sprite.SCROLL_CONST;
            this.masterScrollPos += masterScrollSpeed;
        }
        // Only jump, so stop, then jump
        else if(a == Action.JUMP)
        {
            masterScrollSpeed = 0;

            if (mario.jumpFrames < 4)
            {
                mario.yVelocity -= 10.1;
                mario.posYVelocity = true;
            }
        }
        else if(a == Action.JUMP_AND_RUN)
        {
            masterScrollSpeed = Sprite.SCROLL_CONST;
            this.masterScrollPos += masterScrollSpeed;

            if (mario.jumpFrames < 4)
            {
                mario.yVelocity -= 10.1;
                mario.posYVelocity = true;
            }
        }
    }

    //=================================================================================================================
    // Evaluate Action: return value will indicate the estimated value of performing a specified action, recursive
    //=================================================================================================================

    // Mario AI should be able to "see" x number of pixels ahead in order to find the best route

    // depth = how deep you are in a decision tree
    // k is how frequently we branch, then branch into n number of children (children being number of actions), bigger = less mem usage
    // d is max depth, smaller = more mem usage

    /*
    purpose of evaluate action:
        - how good is it to perform action a?
        - pass depth to go into tree
     */
    double evaluateAction(Action action, int depth)
    {
        // Evaluate the state
        if(depth >= DEPTH)
        {
            // Objective: for mario to get as much coins as possible
            return this.masterScrollPos + (5000 * this.mario.coins) - (2 * this.mario.numJumps);
        }

        // Simulate the action
        Model copy = new Model(this);// uses the copy constructor
        copy.performAction(action);// like what Controller.update did before
        copy.update();// advance simulated time

        // Recurse: want to see how future actions play out
        if(depth % K != 0)
        {
            return copy.evaluateAction(action, depth + 1);
        }
        else
        {
           double best = copy.evaluateAction(Action.RUN, depth + 1);
           best = Math.max(best, copy.evaluateAction(Action.JUMP, depth + 1));
           best = Math.max(best, copy.evaluateAction(Action.JUMP_AND_RUN, depth + 1));

           return best;
        }
    }

    //=================================================================================================================
    // Add a brick to the sprites list
    //=================================================================================================================
    void addBrick(int x, int y, int w, int h)
	{
	    Brick b = new Brick(x, y, w, h);
        sprites.add(b);
	}

    //=================================================================================================================
    // Add a coin block to the sprites list
    //=================================================================================================================
	void addBlock(int x, int y)
    {
        CoinBlock c = new CoinBlock(x, y, this);
        sprites.add(c);
    }

    //=================================================================================================================
	// Save Json objects into a (json) file
    //=================================================================================================================
	void save(String filename)
	{
        Json ob = marshall();// marshall method in this class
        ob.save(filename);
	}

    //=================================================================================================================
	// Load Json objects from a .json file
    //=================================================================================================================
	void load(String filename)
    {
        unmarshall(Json.load(filename));
    }

    //=================================================================================================================
    // Marshall - turn bricks into Json objects (essentially saving the layout)
    //=================================================================================================================
    private Json marshall()
    {
        Json ob = Json.newObject();
        Json json_sprites = Json.newList();
        ob.add("sprites", json_sprites);

        // Turn all sprites into Json objects
        for(int i = 0; i < this.sprites.size(); i++)
        {
            Sprite s = this.sprites.get(i);
            Json j = s.spriteMarshall();// sprite marshall
            json_sprites.add(j);
        }

        return ob;
    }

    //=================================================================================================================
    // Unmarshall - take Json models and turn them back into models, read from a Json file
    // * Note: coins NOT added
    //=================================================================================================================
    private void unmarshall(Json ob)
    {
        Json json_sprites = ob.get("sprites");

        if(json_sprites == null)
        {
            this.sprites.clear();// throw out all the sprites
        }
        else
        {
            Json j = null;

            for (int i = 0; i < json_sprites.size(); i++)
            {
                j = json_sprites.get(i);// go through every element in json object
                String type = j.getString("type");

                if (type.contentEquals("background"))
                {
                    if(this.sprites.size() == 0)
                    {
                        Background bg = new Background(j);
                        this.sprites.add(bg);
                    }
                }
                else if (type.contentEquals("mario"))
                {
                    if(this.sprites.size() == 1)
                    {
                        Mario ma = new Mario(j, this);
                        this.mario = ma;// re-assign redundant mario reference
                        this.sprites.add(ma);
                    }
                }
                else if (type.contentEquals("brick"))
                {
                    Brick br = new Brick(j);// pass Json into constructor, get coords for brick
                    this.sprites.add(br);// add extracted brick into brick array
                }
                else if (type.contentEquals("coin_block"))
                {
                    CoinBlock cb = new CoinBlock(j, this);
                    this.sprites.add(cb);
                }
                else
                {
                    throw new RuntimeException("Unknown sprite type encountered.");
                }
            }
        }
    }
}