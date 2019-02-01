package manuel.mariogame.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class GameView extends SurfaceView
{
    SurfaceHolder ourHolder;
    Canvas canvas;
    Paint paint;
    Model model;
    GameController controller;

    // Image Resources
    Bitmap world0;

    Bitmap mario1;
    Bitmap mario2;
    Bitmap mario3;
    Bitmap mario4;
    Bitmap mario5;
    Bitmap mario6;

    Bitmap brick;
    //==============================================================================================
    // Game View constructor
    //==============================================================================================
    public GameView(Context context)
    {
        super(context);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Load the images
        world0 = BitmapFactory.decodeResource(this.getResources(), R.drawable.world0);

        mario1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario1);
        mario2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario2);
        mario3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario3);
        mario4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario4);
        mario5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario5);
        mario6 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario6);

        brick = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick);
    }

    //==============================================================================================
    // Setters
    //==============================================================================================
    void setModel(Model m)
    {
        model = m;
    }

    void setController(GameController c)
    {
        controller = c;
    }

    //==============================================================================================
    // Update screen
    //==============================================================================================
    public void update()
    {
        if (!ourHolder.getSurface().isValid())
        {
            return;
        }

        canvas = ourHolder.lockCanvas();

        //------------------------------------------------------------------------------------------
        // Draw the background, make x the master scrolling position
        //------------------------------------------------------------------------------------------
        canvas.drawBitmap(world0, model.getMasterScrollPos()/3, 0, null);

        //------------------------------------------------------------------------------------------
        // Animate Mario
        //------------------------------------------------------------------------------------------
        // Animate going left
        if(model.mario.isGoingLeft)
        {
            if(model.mario.leftCounter % 9 < 4)
            {
                canvas.drawBitmap(mario4, model.mario.xPos, model.mario.yPos, paint);
            }
            else
            {
                canvas.drawBitmap(mario5, model.mario.xPos, model.mario.yPos, paint);
            }

            model.mario.leftCounter++;
        }
        // Animate going right
        else if(model.mario.isGoingRight)
        {
            if(model.mario.rightCounter % 9 < 4)
            {
                canvas.drawBitmap(mario2, model.mario.xPos, model.mario.yPos, paint);
            }
            else
            {
                canvas.drawBitmap(mario3, model.mario.xPos, model.mario.yPos, paint);
            }

            model.mario.rightCounter++;
        }
        // Remain still
        else
        {
            model.mario.rightCounter = 0;
            model.mario.leftCounter = 0;
            canvas.drawBitmap(mario1, model.mario.xPos, model.mario.yPos, paint);
        }

        //------------------------------------------------------------------------------------------
        // Draw non-Mario sprites with x position being master scroll position
        //------------------------------------------------------------------------------------------
        canvas.drawBitmap(brick, model.b1.xPos + model.getMasterScrollPos(), model.b1.yPos, paint);
        canvas.drawBitmap(brick, model.b2.xPos + model.getMasterScrollPos(), model.b2.yPos, paint);
        canvas.drawBitmap(brick, model.b3.xPos + model.getMasterScrollPos(), model.b3.yPos, paint);

        //------------------------------------------------------------------------------------------
        // Draw lines to separate quadrants so user can better distinguish controls
        //------------------------------------------------------------------------------------------
        canvas.drawLine(Model.getScreenWidth()/2, 0, Model.getScreenWidth()/2, Model.getScreenHeight(), paint);
        canvas.drawLine(0, Model.getScreenHeight()/2, Model.getScreenWidth(), Model.getScreenHeight()/2, paint);

        // Label quadrants with controls
        paint.setTextSize(35);
        canvas.drawText("No Control", Model.getScreenWidth()/2 + 30, 35, paint);
        canvas.drawText("Jump", 30, 40, paint);
        canvas.drawText("Run Left", 30, Model.getScreenHeight()/2 + 35, paint);
        canvas.drawText("Run Right", Model.getScreenWidth()/2 + 30, Model.getScreenHeight()/2 + 35, paint);

        ourHolder.unlockCanvasAndPost(canvas);
    }

    //==============================================================================================
    // The SurfaceView class (which GameView extends) already implements onTouchListener,
    // so we override this method and pass the event to the controller
    //==============================================================================================
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        controller.onTouchEvent(motionEvent);
        return true;
    }
}