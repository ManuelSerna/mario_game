import java.util.ArrayList;

class Model
{
    // Hold every sprite in a collection
    ArrayList<Sprite> sprites;

    // Add redundant Mario reference, easier to access
    Mario mario;

	Model(int inputWidth, int inputHeight)
	{
	    // Screen size
        int screenWidth = inputWidth;
        int screenHeight = inputHeight;

        // Regular sprite dimensions
        int spriteWidth = 60;
        int spriteHeight = 95;

        // Initialize an empty list of sprites
        sprites = new ArrayList<Sprite>();

        // Create a background and mario, add immediately to the list
        /*
        Background background = new Background();
        sprites.add(background);

        mario  = new Mario(screenWidth/2, 500, spriteWidth, spriteHeight, sprites);
        sprites.add(mario);

        // Add a koopa
        GreenKoopa greenKoopa = new GreenKoopa(screenWidth - 100, 500, sprites);
        sprites.add(greenKoopa);
        */

        // Load a preset layout
        load("map.json");
	}

    void update()
    {
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite s = sprites.get(i);
            s.update();// polymorphism happens here, s may be a mario or turtle
        }
    }

    void addBrick(int x, int y, int w, int h)
	{
	    Brick b = new Brick(x, y, w, h);
        sprites.add(b);
	}

	void addBlock(int x, int y)
    {
        CoinBlock c = new CoinBlock(x, y, sprites);
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

        // turn all sprites into Json objects
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite s = sprites.get(i);
            Json j = s.spriteMarshall();// marshall from brick class
            json_sprites.add(j);
        }

        return ob;
    }

    //=================================================================================================================
    // Unmarshall - take Json models and turn them back into models, read from a Json file
    //=================================================================================================================
    private void unmarshall(Json ob)
    {
        sprites.clear();// throw out all the sprites
        Json json_sprites = ob.get("sprites");
        Json j = null;

        for(int i = 0; i < json_sprites.size(); i++)
        {
            j = json_sprites.get(i); // go through every element in json object
            String type = j.getString("type");

            if(type.contentEquals("background"))
            {
                Background bg = new Background(j);
                sprites.add(bg);
            }
            else if(type.contentEquals("mario"))
            {
                Mario ma = new Mario(j, sprites);

                // Assign Mario element to redundant mario reference
                mario = ma;
                sprites.add(ma);
            }
            else if(type.contentEquals("brick"))
            {
                Brick br = new Brick(j);// pass Json into constructor, get coords for brick
                sprites.add(br);// add extracted brick into brick array
            }
            else if(type.contentEquals("coin_block"))
            {
                CoinBlock cb = new CoinBlock(j, sprites);
                sprites.add(cb);
            }
            else if(type.contentEquals("green_koopa"))
            {
                GreenKoopa gk = new GreenKoopa(j, sprites);
                sprites.add(gk);
            }
            else
            {
                throw new RuntimeException("Unknown sprite type encountered.");
            }
        }
    }
}