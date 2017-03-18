# PiRadioProject

This project makes it able to stream out music via the Raspberry Pi 3, gpio pin 4. It also includes a file transfer via android to the Raspberry Pi , by a TCP-Server
and a TCP-Client (Android application).

To be able to run the code you need to have the application downloaded to your phone. Also in our case the Raspberry Pi files should be located
on the desktop. All the files needed is in the "Pi-files" folder. 

To be able to run the server on the Raspberry Pi on startup, you need to configure autostartup on it, its done by simply making a script that runs on boot. 
For simplicity your device and the Rasperry should preferably be on the same network though you can use port forwarding in your router to make distance
communication work perfectly. 

The Raspberry also needs to have a antenna aka jumperwire attached to gpio pin 4. 10 cm pin makes about 50 meters range.

Guide :

Step 1:
Download the apk file via Android Studio. 

Step2:
Copy the folder "Pi files" onto yout desktop in the raspbian interface aka Raspberry pi desktop. 

Step3:
Install Ffmpeg to be able to convert to mp3, else just add wav files...

Step4:
Add some songs to the music folder, either by putting them there manually or by transfering them via the application

Step5:
Attach the jumper wire to gpio pin 4. 

Step6: 
Configure a script to run the commands "cd home/Desktop" + "java TCPServer" (TCPServer should be the compiled java class)
This script should be run at boot, by starting @lxterminal -e "scriptname"
You could also do this manually in the terminal to just start the server. 

Step7:
Now enter the correct port and ip to the raspberry to initialize connection and start to play and transfer songs!

// There is also a TCPClient for Java on PC , though it was used for testing so it should
not be used now, only if you want to test connection via PC->PC
// Frequency ranges is set in the Java Server code, now its on 89.9. Be aware of which frequencies your broadcasting on due to regulations.
// Credit to the C-code Oskar Weigl and Oliver Mattos icrobotix piradio


 