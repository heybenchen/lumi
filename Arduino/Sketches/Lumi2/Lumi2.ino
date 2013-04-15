#include "Colorduino.h"

char val;         // variable to receive data from the serial port
int ledpin = 2;  // LED connected to pin 2 (on-board LED)
char bytes[5];

void setup()
{
  Colorduino.Init();
  // compensate for relative intensity differences in R/G/B brightness
  // array of 6-bit base values for RGB (0~63)
  // whiteBalVal[0]=red
  // whiteBalVal[1]=green
  // whiteBalVal[2]=blue
  unsigned char whiteBalVal[3] = {36,63,63};
  Colorduino.SetWhiteBal(whiteBalVal);
  pinMode(ledpin = 13, OUTPUT);  // pin 13 (on-board LED) as OUTPUT
  Serial.begin(38400);       // start serial communication at specified baud rate
}

void loop() {
  if (Serial.available() > 5)
  {
    val = Serial.read();
    
    if (val == 'd'){ // Draw single LED
      
      if (Serial.readBytes(bytes, 5) == 5){ // If successfully reads next 5 bytes:
        Serial.flush();
       
        Colorduino.SetPixel((unsigned char)bytes[0], (unsigned char)bytes[1], (unsigned char)bytes[2], 
                            (unsigned char)bytes[3], (unsigned char)bytes[4]);
        Colorduino.SetDrawPixel((unsigned char)bytes[0], (unsigned char)bytes[1], (unsigned char)bytes[2], 
                                (unsigned char)bytes[3], (unsigned char)bytes[4]);
        Colorduino.FlipPage();       
      }
    }      
  }
}

