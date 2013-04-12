/*
simple LED test
*/

#include "Colorduino.h"
char val;         // variable to receive data from the serial port
int ledpin = 2;  // LED connected to pin 2 (on-board LED)

void animate()
{
  Colorduino.SetPixel(2,2,255,0,0);
  delay(200);
  Colorduino.SetPixel(2,2,255,0,0);
}
  

void setup()
{
   Colorduino.Init();
  // compensate for relative intensity differences in R/G/B brightness
  // array of 6-bit base values for RGB (0~63)
  // whiteBalVal[0]=red
  // whiteBalVal[1]=green
  // whiteBalVal[2]=blue
  unsigned char whiteBalVal[3] = {36,63,63}; // for LEDSEE 6x6cm round matrix
  Colorduino.SetWhiteBal(whiteBalVal);
  
  pinMode(ledpin = 13, OUTPUT);  // pin 13 (on-board LED) as OUTPUT
  Serial.begin(115200);       // start serial communication at 115200bps
 
}
 
void loop() {
  if( Serial.available() > 0)       // if data is available to read
  {
    val = Serial.read();            // read it and store it in 'val'
  }
 
  if( val == '0' )               // if '0' was received led 13 is switched off
  {
    digitalWrite(ledpin, LOW);    // turn Off pin 13 off
    Serial.println("13 off");
    
  }

  if( val == '1' )               // if '1' was received led 13 on
 {
    digitalWrite(ledpin = 13, HIGH);  // turn ON pin 13 on
    Serial.println("13 on");
    animate();
  }
  delay(1000);                  // waits for a second   
}

