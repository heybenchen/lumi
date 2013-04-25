Lumi - Illuminated Notification System  
=================
Version 1.0

![Image](/Android/Lumi/res/drawable/ic_launcher.png)

####Created by: 

Ben Shuyi Chen  
Aman Ali
	
####Advised by:  

Prof Michael Spear  
Senior Design 2012-2013
	
Usage Guide:
------------
Lumi communicates via Bluetooth using a series of 6-character bytestreams for commands  
(Characters and strings are in single quotes, everything else is a variable):
	
		'd' + x + y + r + g + b 
			- Turns on a single LED at position (x,y) using colors r, g, b in HEX for RGB
			- Affects both pages
			
		'clrscr' 	
			- Clears the screen
			- Affects both pages
