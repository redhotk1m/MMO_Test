# MMO_Test
Private project where i try to create the basis for an MMO. The concept is big, and it's only done for learning purposes. Will update project when i have time, and fixed packet loss handling (Another project).
# About this project
I wanted to create an MMO, so i could understand how they worked. I'm experienced with JavaFX, so i used this as my GUI, and added JavaFX11 to Java11 with Maven.
I read up on how big corporations setup their servers, and used Steam as an example. (Counter Strike Global Offensive, Left 4 Dead, and etc).
# What i learned
- Threads
- Implementing dependencies into modular Java 11 with Maven.
- Fat Jar (Injecting all dependencies into jar, so it can be run without installing JavaFX11 on clients who wants to run the Jar file)
- Integrating JavaFX 11 with Java11 using Maven
- Multiple socket-to-server communication. (Just like an MMO)
- SQLite (Lightweight SQL framework)
- UDP/TCP, as i had to learn alot about them to determine which to use.
  - I ended up using UDP as it's faster, but then i tried to make UDP Reliable, since some packets can't get lost. (E.g Login packets).
- Animation states over packets. (Players see other animations correctly)
- Login and user creation over internet
- Handling when server disconnects
- Handling when clients disconnects / logs off
- Handling multi client player movement
- Cheat prevention
  - This whole "MMO" uses the server as the "truth source". If you have too high speed, the server wont accept your packets and you can't move. This concept is applied on every packet.
 Since the server doublechecks all the packets, it induces some lag. Therefore i'm hoping to implement Client-Side Prediction and Server Reconciliation, Entity Interpolation and Lag Compensation.
 You can read more about that here: https://www.gabrielgambetta.com/client-server-game-architecture.html
# TODO
I wanted to learn, therefore i don't want to use libraries such as "RUDP" which fixes my Reliable UDP problem. I'm trying to create RUDP on my own, in a seperate project.
- Hash passwords in database
- Multiserver support (like a loadbalancer)
- Encrypt sensitive packets
- Implement selfmade RUDP
- Adding support for changing Port and IP in the client
# How to use
1. Run the application, and start up the server. This uses LocalHost with 1337 as the port number. If you want to change IP, change serverHost to your public IP. It's in openjfx\Controllers\FXMLController.java line 18.
2. Run N amount of applications and create your account (Do **not** run multiple servers)
3. Login with newly created accounts on your N amount of applications.
4. Play with the n amount of clients using your arrowkeys
