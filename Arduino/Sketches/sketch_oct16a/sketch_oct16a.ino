#include <Rainbowduino.h>

void setup()
{
  Rb.init();
}

void loop()
{
  Rb.setPixelXY(0,0,0xFF0000);
  Rb.setPixelXY(0,1,0x00FF00);
  Rb.setPixelXY(1,0,0x0000FF);
  Rb.setPixelXY(1,1,0xFFFFFF);
}
