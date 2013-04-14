#include "Colorduino.h"

char val;         // variable to receive data from the serial port
int ledpin = 2;  // LED connected to pin 2 (on-board LED)
byte bytes[6];

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
  unsigned int i = 500; 
  if (Serial.available() > 5)
  {
    val = Serial.read();
    Serial.print("val: ");
    Serial.println(val);
    
    // Set single LED
    if (val == 'd'){
      bytes[0] = 'd';
      int j;
      for (j = 1; j < 6; j++){
        bytes[j] = Serial.read();
      }
      Serial.println((int)bytes[1]);
      Serial.println((int)bytes[2]);
      Serial.println((int)bytes[3]);
      Serial.println((int)bytes[4]);
      Serial.println((int)bytes[5]);
      Colorduino.SetPixel((int)bytes[1], (int)bytes[2], (int)bytes[3], (int)bytes[4], (int)bytes[5]);
      Colorduino.SetDrawPixel((int)bytes[1], (int)bytes[2], (int)bytes[3], (int)bytes[4], (int)bytes[5]);
      Colorduino.FlipPage();
    }      
  }
}

