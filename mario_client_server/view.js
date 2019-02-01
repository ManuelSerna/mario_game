//=====================================================================================================================
// View "class"
//=====================================================================================================================
class View
{
    constructor(model)
    {
        this.model = model;
        this.canvas = document.getElementById("marioCanvas");// canvas defined in HTML doc
    }

    //=================================================================================================================
    // Draw all the sprites in the collection by passing the context
    //=================================================================================================================
    update()
    {
        for(let i = 0; i < this.model.sprites.length; i++)
        {
            let ctx = this.canvas.getContext("2d");
            let sprite = this.model.sprites[i];
            sprite.draw(ctx);
        }
    }
}