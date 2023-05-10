COMMAND EXAMPLES TO RUN
--------------------------------------------------------------------------
java -jar CreateWhiteBoard.jar localhost 3005 server
java -jar JoinWhiteBoard.jar localhost 3005 client


PROJECT GOAL
--------------------------------------------------------------------------

This project aims to implement a shared whiteboards server application that allows multiple users to perform basic drawings and inputting text messages on the shared paint area and communicate through the chat area from the client application. The first user acts as a manager and can kick other users out and perform file management. The project is developed using a client-server model, enabling the server to broadcast the newest paint on the painting area to all clients. Socket and thread technologies are the two fundamental technologies used in the project implementation for network communication and concurrency. Java programming language is used in project implementation.

ARCHITECTURES
--------------------------------------------------------------------------

CLIENT-SERVER ARCHITECTURE

The project was implemented using a client-server architecture. It allows the server to act as a broadcast station. So that no matter which client has performed any actions on the shared painting area, the action will be broadcast to every joined client to achieve the same action on the painting area, including the server. Whenever a new client has joined the server, the server will share the latest state of the shared painting area. By doing so, the concurrency state of the shared painting area can be achieved.

THREAD-PER-CONNECTION MULTI-THREAD SERVER ARCHITECTURE

This project multi-threaded server was implemented using thread-per-connection architecture. The decision of choosing this architecture is based on the successful implementation from the previous project. Stable communications between the server and clients can be guaranteed via establishing communication channels using TCP protocol. However, there is one major implementation difference between this project to the previous one.

The previous project only requires establishing one thread per connected client for communication, which is insufficient to handle the necessary message exchanges. It cannot support broadcast functionality from the server to all other connected clients. For this reason, the server application itself is implemented as one thread. As a result, every thread which handles per connected client can access the client thread list stored in the server thread to broadcast the message to all connected clients.

COMMUNICATION PROTOCOLS AND MESSAGE FORMATS
--------------------------------------------------------------------------

All communications between server and clients are implemented using TCP sockets in this project. It provides a reliable communication mechanism to ensure every message transmission being delivered to another party successfully.

Message exchange protocol is mainly based on an external library named org.json, which allows message exchange between client and server to be done using JSON format. Furthermore, org.json library provides the functionality for converting, reading and writing from Strings format to JSON Object, which is also used in the dictionary library file manipulation.

As for the image transfer between the server and the newly joined user, the ImageIO class converts image format to a ByteArray format to transfer the image data from the server to the client. When the client has received the image in ByteArray format, the ImageIO class is able to convert the ByteArray formatted image back to the actual image format to display in the client application.

FAILURE MODEL
--------------------------------------------------------------------------
Potential errors for both client and server-side have been appropriately handled in the project. A pop-up message window will appear and display the error message corresponding to the actual error the program encountered. The errors included but not limited to:
      * Console input error, such as wrong parameters passed in command line
      * Network communication error, such as socket closed and connection error
      * Any other errors may occur runtime for both client and server applications
      
FUNCTIONALITIES
--------------------------------------------------------------------------
  * Drawing
  * Colour selection
  * Chat room
  * Online user list
  * Quit

Server exclusive functions:

  * Accept or decline a user from joining the whiteboard.
  * Kick a specific user from the online user list.
  * File managements.


