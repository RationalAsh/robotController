#robotController
This is an android app that I made to stream motion commands over a wireless network.
It uses accelerometer readings to decide which way the phone is tilting and sends 5
motion commands.
W - Forward (When the phone is pitching forward)
S - Back (When the phone is pitching back)
A - Left (When the phone is rolling left)
D - Right (When the phone is rolling right)
H - Halt (When the phone is relatively parallel to the ground)

The app has a text field to enter the IP address of a TCP server. To use the app,
enter the IP address of the TCP server and click "Connect". If the app connects
successfully to the server, it will start streaming these commands to the server.

Writing a TCP server is fairly simple in Python. It takes a few lines of code to 
listen for incoming connections and loop through and print the data being received.
How to use this data is up to you! :D

In my case, I got the data and passed it on to an arduino which then transmitted it
over a wireless link to a wheeled robot. 