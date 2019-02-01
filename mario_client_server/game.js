//=================================================================================================================
// Game class
//=================================================================================================================
class Game
{
    constructor()
    {
        this.model = new Model();
        this.view = new View(this.model);
        this.controller = new Controller(this.model, this.view);
    }

    onTimer()
    {
        this.controller.update();
        this.model.update();
        this.view.update();
    }
}

//=================================================================================================================
// Run a new game
//=================================================================================================================
let game = new Game();
let timer = setInterval(function() { game.onTimer(); }, 40);
