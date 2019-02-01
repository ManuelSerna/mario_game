package manuel.mariogame.app;

import android.util.Log;
import android.view.MotionEvent;

class GameController implements Runnable
{
    volatile boolean playing;
    Thread gameThread = null;
    Model model;
    GameView view;

    //==============================================================================================
    // Game controller constructor
    //==============================================================================================
    GameController(Model m, GameView v)
    {
        model = m;
        view = v;
        view.setController(this);
        playing = true;
    }

    //==============================================================================================
    // Update the model's scrolling position
    //==============================================================================================
    void update()
    {
        //if(goLeft)
        if(model.mario.isGoingLeft)
        {
            System.out.println("LEFT");
            model.setMasterScrollPos(model.getMasterScrollPos() + model.getMasterScrollSpeed());// +
        }

        //if(goRight)
        if(model.mario.isGoingRight)
        {
            System.out.println("RIGHT");
            model.setMasterScrollPos(model.getMasterScrollPos() - model.getMasterScrollSpeed());// -
        }
    }

    //==============================================================================================
    // Run the game
    //==============================================================================================
    @Override
    public void run()
    {
        while(playing)
        {
            //long time = System.currentTimeMillis();
            this.update();
            model.update();
            view.update();

            try
            {
                Thread.sleep(20);
            }
            catch(Exception e)
            {
                Log.e("Error:", "sleeping");
                System.exit(1);
            }
        }
    }

    //==============================================================================================
    // Touch event: user presses the screen, perform action
    //==============================================================================================
    void onTouchEvent(MotionEvent motionEvent)
    {
        int touchX = (int)motionEvent.getX();
        int touchY = (int)motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: // Player touched the screen
                // Q I
                if( (touchX >= Model.getScreenWidth()/2) && (touchY <= Model.getScreenHeight()/2) )
                {
                    // Do nothing in Q I
                }
                // Q II: Jump
                else if( (touchX < Model.getScreenWidth()/2) && (touchY <= Model.getScreenHeight()/2) )
                {
                    model.mario.jump = true;
                }
                // Q III
                else if( (touchX < Model.getScreenWidth()/2) && (touchY > Model.getScreenHeight()/2) )
                {
                    model.mario.isGoingLeft = true;
                }
                // Q IV
                else if( (touchX >= Model.getScreenWidth()/2) && (touchY > Model.getScreenHeight()/2) )
                {
                    model.mario.isGoingRight = true;
                }

                break;

            case MotionEvent.ACTION_UP: // Player withdrew finger
                // Reset either direction flag
                //model.mario.jump = false;
                model.mario.isGoingLeft = false;
                model.mario.isGoingRight = false;

                break;
        }
    }

    //==============================================================================================
    // Shut down the game thread
    //==============================================================================================
    public void pause()
    {
        playing = false;
        try
        {
            gameThread.join();
        }
        catch (InterruptedException e)
        {
            Log.e("Error:", "joining thread");
            System.exit(1);
        }
    }

    //==============================================================================================
    // Restart the game thread
    //==============================================================================================
    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}