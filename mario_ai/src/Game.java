import javax.swing.JFrame;
import java.awt.Toolkit;

public class Game extends JFrame
{
    /*
    Components of the Game

    Model:
        - in charge of the mechanics, not the graphics
        - registers what will appear on screen
        - contains an independent collection of sprites within it
        - updates all sprites in the sprite list
    View:
        - in charge of graphics, what is seen
            - draws what the model updates
    Controller:
        - registers input from user
            - gives instructions to model accordingly
    */
    private Model model;
    private Controller controller;
    private View view;

    private Game()
    {
        final int SCREEN_WIDTH  = 900;
        final int SCREEN_HEIGHT = 650;

        model = new Model(SCREEN_WIDTH, SCREEN_HEIGHT);

        controller = new Controller(model);
        view = new View(model);

        // Consider mouse and keyboard actions
        view.addMouseListener(controller);
        this.addKeyListener(controller);

        this.setTitle("Paradigms Project 6: Artificial Intelligence, Serna-Aguilera");
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);// set the size of the window
        this.setFocusable(true);
        this.getContentPane().add(view);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void run()
    {
        // Game loop updates models, graphics, and keeps up with user input
        while(true)
        {
            controller.update();
            model.update();
            view.repaint();// Indirectly calls View.paintComponent
            Toolkit.getDefaultToolkit().sync();// Updates screen

            // Go to sleep for 25 milliseconds so user can see movement smoothly
            try
            {
                Thread.sleep(25);
            } 
            catch(Exception e) 
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
}
