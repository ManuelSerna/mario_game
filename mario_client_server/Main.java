/*
Dr. Gashler's Simple Java Server
Goal: introduction to client-server architecture, work with a web browser and a simple server

I have commented on it more extensively
*/

import java.net.*;
import java.io.*;
import java.util.Date;
import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

class Main
{
	// Keep track of clients
	private static int clientCount = 0;

	// Keep track of client 0 and client 1's player coordinates and master scroll positions
	private static int player0X = 0;
	private static double player0Y = 0;
	private static int mSP0 = 0;

	private static int player1X = 0;
	private static double player1Y = 0;
	private static int mSP1 = 0;

	//=================================================================================================================
	//=================================================================================================================
	static String getServerTime()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return dateFormat.format(calendar.getTime());
	}

	//=================================================================================================================
	//=================================================================================================================
	static void sendLine(PrintWriter out, String line)
	{
		out.print(line); // Send over the socket
		out.print("\r\n");
		
		System.out.println(line); // Print it to the console too, just to make debugging easier
	}

	//=================================================================================================================
	//=================================================================================================================
	static void onGet(OutputStream os, String url) throws Exception
	{
		PrintWriter out = new PrintWriter(os, true);
		String filename = url.substring(1); // cut off the initial "/"
		File f = new File(filename);
		Path path = Paths.get(filename);
		String dateString = getServerTime();
		
		System.out.println("* The server replied: ===================================================================");
		
		if(f.exists() && !f.isDirectory())
		{
			// Read the file from disk
			byte[] fileContents = Files.readAllBytes(path);

			// Send the headers
			sendLine(out, "HTTP/1.1 200 OK");
			sendLine(out, "Content-Type: " + Files.probeContentType(path));
			sendLine(out, "Content-Length: " + Integer.toString(fileContents.length));
			sendLine(out, "Date: " + dateString);
			sendLine(out, "Last-Modified: " + dateString);
			sendLine(out, "Connection: close");
			sendLine(out, "");
			out.flush();

			// Send the payload
			os.write(fileContents);
			String blobHead = fileContents.length < 60 ? new String(fileContents) : new String(fileContents, 0, 60) + "...";
			System.out.println(blobHead);
		}
		else
		{
			// Make an error message
			String payload = "404 - File not found: " + filename;

			// Send HTTP headers
			sendLine(out, "HTTP/1.1 200 OK");
			sendLine(out, "Content-Type: text/html");
			sendLine(out, "Content-Length: " + Integer.toString(payload.length()));
			sendLine(out, "Date: " + dateString);
			sendLine(out, "Last-Modified: " + dateString);
			sendLine(out, "Connection: close");
			sendLine(out, "");

			// Send the payload
			sendLine(out, payload);
		}
	}

	//=================================================================================================================
	// Receive messages from client
	//=================================================================================================================
	static void onPost(OutputStream os, String url, char[] incomingPayload)
	{
		//-------------------------------------------------------------------------------------------------------------
		// Parse the incoming payload from client
		//-------------------------------------------------------------------------------------------------------------
 		System.out.println("\n=========================================================================================");

		String payload = String.valueOf(incomingPayload);
		System.out.println("Received the following payload: " + payload);

		Json incoming = Json.parse(payload);

		// Gather info to see if current client's information has changed (compare later below)
		int clientID = Integer.parseInt(incoming.get("ID").toString());// get "ID" element from a client
		int clientXPos = Integer.parseInt(incoming.get("xPos").toString());// NOTE: xPos is technically double
		double clientYPos = Double.parseDouble(incoming.get("yPos").toString());
		int clientMSP = Integer.parseInt(incoming.get("masterScrollPos").toString());

		//-------------------------------------------------------------------------------------------------------------
		// Make a response
		//-------------------------------------------------------------------------------------------------------------

		// Extract information from JSON object, then convert it into String, something you can work with
		Json outgoing = Json.newObject();// outgoing data goes here, it will go back to the client

		// Assign a proper client ID to the current client
		if(clientID < 0)
		{
			// Assign count as the client ID (outgoing), then increment
			outgoing.add("ID", clientCount);

			// "Initialize" server's coordinates with client's
			if(clientCount == 0)
			{
				player0X = clientXPos;
				player0Y = clientYPos;
				mSP0 = clientMSP;
			}
			else if(clientCount == 1)
			{
				player1X = clientXPos;
				player1Y = clientYPos;
				mSP1 = clientMSP;
			}

			clientCount++;
		}
		// Else just keep updating a pre-existing client
		else
		{
			/*
			Now prepare information to send back to the client

			- identify client:
				- Mario: ID 0
				- Koopa: ID 1
			- update client's data with server's (BUT check to see if numbers have changed)
			- now send other client's data to client so it can update its own data on the other client (player)
			 */
			if(clientID == 0)
			{
				System.out.println("I am player" + clientID);

				// Update server's coordinates of current client's
				player0X = clientXPos;
				player0Y = clientYPos;
				mSP0 = clientMSP;

				// Update client's data on OTHER client (add to JSON object)
				outgoing.add("xPos", player1X);
				outgoing.add("yPos", player1Y);
				outgoing.add("masterScrollPos", mSP1);
			}
			// Is Koopa
			else if(clientID == 1)
			{
				System.out.println("I am player" + clientID);

				// Update server's coordinates of current client's
				player1X = clientXPos;
				player1Y = clientYPos;
				mSP1 = clientMSP;

				// Update client's data on OTHER client (add to JSON object)
				outgoing.add("xPos", player0X);
				outgoing.add("yPos", player0Y);
				outgoing.add("masterScrollPos", mSP0);
			}
		}

		// Finalize the outgoing response to the client to making it a string
		String response = outgoing.toString();

		System.out.println("\nOutgoing" + response + "\n");

		//-------------------------------------------------------------------------------------------------------------
		// Send HTTP headers
		//-------------------------------------------------------------------------------------------------------------
		System.out.println("* The server replied: ===================================================================");
		String dateString = getServerTime();
		PrintWriter out = new PrintWriter(os, true);
		sendLine(out, "HTTP/1.1 200 OK");
		sendLine(out, "Content-Type: application/json");
		sendLine(out, "Content-Length: " + Integer.toString(response.length()));
		sendLine(out, "Date: " + dateString);
		sendLine(out, "Last-Modified: " + dateString);
		sendLine(out, "Connection: close");
		sendLine(out, "");
		
		// Send the response
		sendLine(out, response);
		out.flush();
	}
	
	//=================================================================================================================
	// Main
	//=================================================================================================================
	public static void main(String[] args) throws Exception
	{
		// Make a socket to listen for clients
		int port = 1234;// port is a number that facilitates communication, 1234 is arbitrary
		
		// localhost = this computer
		String ServerURL = "http://localhost:" + Integer.toString(port) + "/page.html";
		
		//Socket: works like telephones connected by land lines
		ServerSocket serverSocket = new ServerSocket(port);

		// Start the web browser
		if(Desktop.isDesktopSupported())
		{
            // Open up two web browsers to open up the Mario game
			Desktop.getDesktop().browse(new URI(ServerURL));// browser window 1
			Desktop.getDesktop().browse(new URI(ServerURL));// browser window 2
        }
		else
		{
			System.out.println("Please direct your browser to " + ServerURL);
        }

		// Handle requests from clients until the program is terminated manually
		while(true)
		{
            /*
            Server: this Java program
            Client: a web browser
            */
		
            // .accept = "wait for the phone to ring"
			Socket clientSocket = serverSocket.accept(); // This call blocks (waits) until a client connects
			
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			OutputStream os = clientSocket.getOutputStream();

			/*
			HTML: language of web pages
			HTTP: hypertext transport protocol
			- describes how server and browser talk communicate in order to transport hypertext
			
			hypertext: text with links
			*/
			
			// Read the HTTP headers
			String headerLine;
			int requestType = 0;
			int contentLength = 0;
			String url = "";
			
			// Listen for a few important words from the browser, respond accordingly
			System.out.println("* A client said: ========================================================================");
			while ((headerLine = in.readLine()) != null)
			{
				System.out.println(headerLine);
				
				if(headerLine.length() > 3 && headerLine.substring(0, 4).equals("GET "))
				{
					requestType = 1;
					url = headerLine.substring(4, headerLine.indexOf(" ", 4));
				}
				else if(headerLine.length() > 4 && headerLine.substring(0, 5).equals("POST "))
				{
					requestType = 2;
					url = headerLine.substring(5, headerLine.indexOf(" ", 5));
				}
				else if(headerLine.length() > 15 && headerLine.substring(0, 16).equals("Content-Length: "))
				{
					contentLength = Integer.parseInt(headerLine.substring(16));
                }
				if(headerLine.length() < 2) // Headers are terminated by a "\r\n" line
				{
					break;
                }
			}

			// Send a response
			if(requestType == 1)
			{
				onGet(os, url);
			}
			else if(requestType == 2)
			{
				// Read the incoming payload
				char[] incomingPayload = new char[contentLength];
				in.read(incomingPayload, 0, contentLength);

				String blobHead = incomingPayload.length < 60 ? new String(incomingPayload) : new String(incomingPayload, 0, 60) + "...";

				System.out.println(blobHead);

				onPost(os, url, incomingPayload);
			}
			else
			{
				System.out.println("Received bad headers. Ignoring.");
			}

			// Hang up
			os.flush();// make sure all messages have gone through the other end
			clientSocket.close();
			
			// Thus communication ends, now loop back again. Create a new socket, and accept the next call when the browser needs something
		}
	}
}
