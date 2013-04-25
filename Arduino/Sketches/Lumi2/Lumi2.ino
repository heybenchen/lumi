/* Lumi - Illuminated Notification System
 *  @version 1.0

 *  @author Ben Shuyi Chen
 *  @author Aman Ali
*/

#include "Colorduino.h"

// Externally stored imagesets
extern unsigned char pic[6][8][8][3];
extern unsigned char msg[2][8][8][3];
extern unsigned char gmail[1][8][8][3];
extern unsigned char ugmail[1][8][8][3];

char val;         // variable to receive data from the serial port
int ledpin = 2;  // LED connected to pin 2 (on-board LED)
char bytes[5];
int i;

void displayImage(unsigned char imgset[6][8][8][3], int index) {
  unsigned char i, j, r, g, b;
  for (i = 0;i<8;i++) {
    for(j = 0;j<8;j++) {
      r = pgm_read_byte(&(imgset[index][i][j][0]));
      g = pgm_read_byte(&(imgset[index][i][j][1]));
      b = pgm_read_byte(&(imgset[index][i][j][2]));
      Colorduino.SetPixel(i, j, r, g, b);
    }
  }
}

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
  if (Serial.available() > 5) {
    val = Serial.read();
    if (val == 'd'){ // Draw single LED
      if (Serial.readBytes(bytes, 5) == 5) { // If successfully reads next 5 bytes:
        Serial.flush();
       
        Colorduino.SetPixel((unsigned char)bytes[0], (unsigned char)bytes[1], (unsigned char)bytes[2], 
                            (unsigned char)bytes[3], (unsigned char)bytes[4]);
        Colorduino.SetDrawPixel((unsigned char)bytes[0], (unsigned char)bytes[1], (unsigned char)bytes[2], 
                                (unsigned char)bytes[3], (unsigned char)bytes[4]);
        Colorduino.FlipPage();       
      }
    }
    else if (val == 'c'){ // Clear Screen
      if (Serial.readBytes(bytes, 5) == 5) { // If successfully reads next 5 bytes:
        Serial.flush();
        Colorduino.ColorFill(0, 0, 0);
        Colorduino.ColorFill(0, 0, 0);
      }
    } 
    else if (val == 'u'){ // Urgent Gmail
      if (Serial.readBytes(bytes, 5) == 5) { // If successfully reads next 5 bytes:
        Serial.flush();
        for (i = 0; i < 10; i++) {
          Colorduino.ColorFill(0,0,0);
          delay(200);
          displayImage(ugmail, 0);
          Colorduino.FlipPage();
          delay(200);
        }
      }
    } 
    else if (val == 'g'){ // Gmail
      if (Serial.readBytes(bytes, 5) == 5) { // If successfully reads next 5 bytes:
        Serial.flush();
        for (i = 0; i < 5; i++) {
          Colorduino.ColorFill(0,0,0);
          delay(500);
          displayImage(ugmail, 0);
          Colorduino.FlipPage();
          delay(500);
        }
      }
    } 
    else if (val == 'm'){ // Message
      if (Serial.readBytes(bytes, 5) == 5) { // If successfully reads next 5 bytes:
        Serial.flush();
        for (i = 0; i < 5; i++) {
          displayImage(msg, 0);
          Colorduino.FlipPage();
          delay(500);
          displayImage(msg, 1);
          Colorduino.FlipPage();
          delay(500);
        }
      }
    }  
  }
}

