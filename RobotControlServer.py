import socket
import select
import sys
import serial
import time

#serial data stuff
ser = serial.Serial("/dev/ttyUSB0", 9600)

host = ''
port = int(raw_input("Enter the port to listen on: "))
backlog = 5
size = 1

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind((host, port))

#Listen for incoming connection on the port given by user
s.listen(backlog)

conn, addr = s.accept()
print "Connection from ", addr

while 1:
    data = conn.recv(size)
    if not data:
        break
    print "Receieved: ", data
    if((data=='W')or(data=='A')or(data=='S')or(data=='D')or(data=='H')):
        com = data+data
        ser.write(com)
    #conn.send(data)

conn.close()
