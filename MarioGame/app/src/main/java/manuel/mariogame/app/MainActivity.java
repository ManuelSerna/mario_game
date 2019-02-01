//**************************************************************************************************
// Mario Android Game - Honors Programming Paradigms Project 5
// Starting code provided by Dr. Gashler, expanded by Manuel Serna-Aguilera
//**************************************************************************************************
package manuel.mariogame.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    Model model;
    GameView view;
    GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new Model();

        view = new GameView(this);
        view.setModel(model);// have game view reference model

        controller = new GameController(model, view);
        setContentView(view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        controller.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        controller.pause();
    }
}