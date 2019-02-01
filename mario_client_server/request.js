/*
Making a chat window

static ArrayList<String> chat = new ArrayList<String>();// string array list hold all messages
int c1pos;
int c2pos;

- server gets a message from client, server adds the string to its list
- clients ask for updates
- send client newer message
- remember clients ping server
 */

// Client ID unknown (-1), will be assigned by server
let myID = -1;

//---------------------------------------------------------------------------------------------------------------------
// Send a message to the server
//---------------------------------------------------------------------------------------------------------------------
function httpPost(url, data, callback)
{
    let request = new XMLHttpRequest();

    request.onreadystatechange = function()
    {
        if(request.readyState == 4)
        {
            if(request.status == 200)
            {
                // Calls cb, give it the server response in text form
                callback(request.responseText);
            }
            else
            {
                if(request.status == 0 && request.statusText.length == 0)
                {
                    alert("Connection failed");
                }
                else
                {
                    alert("Server returned status " + request.status + ", " + request.statusText);
                }
            }
        }
    };
    request.open('post', url, true);
    request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Send your necessary data to the server
    request.send(data);
}

//---------------------------------------------------------------------------------------------------------------------
// Do something with data received from the server (the JSON object)
//---------------------------------------------------------------------------------------------------------------------
function cb(response)
{
    // Display in browser console what was sent back
    console.log("The back-end server replied: " + response);

    // Parse the JSON string
    let ob = JSON.parse(response);

    // Take JSON fields and convert them into numbers
    let tempX = Number(ob.xPos);
    let tempY = Number(ob.yPos);
    let tempMSP = Number(ob.masterScrollPos);

    // If FIRST TIME getting response from server, initialize some client attributes
    if(myID === -1)
    {
        // Assign a proper ID...
        myID = Number(ob.ID);

        // ...Also set up: image resources, master scrolling position
        if(myID === 0)
        {
            // Mario: even # ID's
            mario.src1 = "mario1.png";// still
            mario.src2 = "mario2.png";// right frame 1
            mario.src3 = "mario3.png";// right frame 2
            mario.src4 = "mario4.png";// left frame 1
            mario.src5 = "mario5.png";// left frame 2

            masterScrollPos = -120;

            mario.playerID = myID;
        }
        else if(myID === 1)
        {
            // Koopa: odd # ID's
            mario.src1 = "green_koopa1.png";// still
            mario.src2 = "green_koopa1.png";// right frame 1
            mario.src3 = "green_koopa2.png";// right frame 2
            mario.src4 = "green_koopa3.png";// left frame 1
            mario.src5 = "green_koopa4.png";// left frame 2

            // NOTE: keep masterScrollPos as is for the current hard-coded map

            mario.playerID = myID;
        }
    }

    // Initialize other player's sprite now that both clients are connected
    else if(myID > -1)
    {
        if (otherPlayer == null)
        {
            otherPlayer = new Mario(tempX + tempMSP, tempY, game.model);
            game.model.sprites.push(otherPlayer);

            console.log("INITIAL other client's coords: xPos: " + otherPlayer.xPos + ", yPos: " + otherPlayer.yPos);

            if(myID === 0)
            {
                // Other player is Koopa: odd # ID's
                otherPlayer.src1 = "green_koopa1.png";// still
                otherPlayer.src2 = "green_koopa1.png";// right frame 1
                otherPlayer.src3 = "green_koopa2.png";// right frame 2
                otherPlayer.src4 = "green_koopa3.png";// left frame 1
                otherPlayer.src5 = "green_koopa4.png";// left frame 2

                otherPlayer.playerID = 1;
            }
            else if(myID === 1)
            {
                // Other player is Mario: event # ID's
                otherPlayer.src1 = "mario1.png";// still
                otherPlayer.src2 = "mario2.png";// right frame 1
                otherPlayer.src3 = "mario3.png";// right frame 2
                otherPlayer.src4 = "mario4.png";// left frame 1
                otherPlayer.src5 = "mario5.png";// left frame 2

                otherPlayer.playerID = 0;
            }
        }

        // Keep updating other client's sprite data
        if(otherPlayer.xPos < (tempX + tempMSP))
        {
            otherPlayer.goingRight = true;
        }
        else if(otherPlayer.xPos > (tempX + tempMSP))
        {
            otherPlayer.goingLeft = true;
        }
        else if(otherPlayer.xPos === (tempX + tempMSP))
        {
            otherPlayer.goingRight = false;
            otherPlayer.goingLeft = false;
        }

        otherPlayer.xPos = tempX + tempMSP;

        otherPlayer.yPos = tempY;
    }

    console.log("Other client's coords: xPos: " + otherPlayer.xPos + ", yPos: " + otherPlayer.yPos + ", other player's ID: " + otherPlayer.playerID);
    console.log("Your coords: xPos: " + (mario.xPos + masterScrollPos) + ", yPos: " + mario.yPos + ", my player ID: " + mario.playerID);

    console.log("Sprites in this client (should be 14): " + game.model.sprites.length);
}