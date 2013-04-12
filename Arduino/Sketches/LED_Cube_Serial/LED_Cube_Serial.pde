/*
 * Dez 2011
 */

#include <MsTimer2.h>

int inComm = 0;	// for incoming serial data
int inValue = 0;	// for incoming serial data


byte col = 0;
byte leds[8][8];
boolean lock=false;

// col[xx] of leds = pin yy on led matrix
int rows[8] = {
  9,3,2,12,15,11,7,6};

// row[xx] of leds = pin yy on led matrix
int cols[8] = {
  13,8,17,10,5,16,4,14};


void setup() {
  Serial.begin(115200);
  // set up cols and rows
  for (int i = 0; i < 8; i++) {
    pinMode(cols[i], OUTPUT);
    digitalWrite(cols[i], LOW);
  }

  for (int i = 0; i < 8; i++) {
    pinMode(rows[i], OUTPUT);
    digitalWrite(rows[i], LOW);
  }
  
  clearLeds();
  MsTimer2::set(1, display);
  MsTimer2::start();
}


void clearLeds() {
  cli();
  // Clear display array
  for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 8; j++) {
      leds[i][j] = 0;
    }
  }
  sei();
}

void fillLeds() {
  cli();
  // Clear display array
  for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 8; j++) {
      leds[i][j] = 1;
    }
  }
  sei();
}


// Interrupt routine
void display() {
  if (!lock)
  {
  digitalWrite(cols[col], LOW);  // Turn whole previous column off
  col++;
  if (col == 8) {
    col = 0;
  }
  for (int row = 0; row < 8; row++) {
    if (leds[row][col] == 1) {
      digitalWrite(rows[row], LOW);  // Turn on this led
    }
    else {
      digitalWrite(rows[row], HIGH); // Turn off this led
    }
  }
  digitalWrite(cols[col], HIGH); // Turn whole column on at once (for equal lighting times)
 //   delayMicroseconds(800);  // Delay so that on times are longer than off time = brighter leds
 }
}

// Interrupt routine
void displayA() { 
  digitalWrite(cols[col], LOW);  // Turn whole previous column off
  col = (col >= 7) ? 0 : col+1;
  for (int row = 0; row < 8; row++)
    digitalWrite(rows[row], (leds[row][col] != 1));
  digitalWrite(cols[col], HIGH); // Turn whole column on at once (for equal lighting times)
}


void loop() {
if (Serial.available() > 1) {
    inComm = Serial.read();
    Serial.println((char)inComm); 
    inValue = Serial.read();
    Serial.println(inValue); 
    
// 1 == ON
if (inComm==49)
  {
    leds[inValue % 8][inValue / 8] = 1;
  }
// 0 == OFF
else if (inComm==48)
{
    leds[inValue % 8][inValue / 8] = 0;  
}

// c == Clear
else if (inComm==99)
{
    clearLeds();  
}
    
}
}
