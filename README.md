# Chat application
## Introduction
This application consists of a server and a GUI client and allows users to communicate with each other. I created this project to apply my knowledge of sockets in practice and explore JavaFX.
## How to use
If you wish to launch both server and clients on the same computer you do not have to change anything. But if you wish to run the server and clients on different computers, you need to change the line 36 in ClientFX.java and set the ip of the server (type it instead of localHost).
First run and the server and then as many clients as you wish. Once you have entered your username you can start typing your messages in the field at the bottom of the screen. Hit enter to confirm and send a message. It will be sent to all users. Please note that if you are trying to enter a username which is already in use, you will be asked to choose another one.
In order to close the server, type "exit" in your command line. All users will be notified that the server has been closed and asked to leave.
## Technologies
Java SE, JavaFX
## How to launch
This project contains .java source files which need to be compiled. Since it uses JavaFX, please include JavaFX library before building the game on your computer. Instruction on how to include JavaFX: https://openjfx.io/openjfx-docs/#introduction
