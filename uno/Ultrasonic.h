#include <Wire.h> // I2C control library
#include <LiquidCrystal_I2C.h>

class UltraSonic {
private:
  

   //LCD
  LiquidCrystal_I2C* lcd;

  float NowDistance;

  float MinDistance;

public:

  boolean D20 = false;
  boolean D5 = false;
  
  void Setup();

  float CheckDistance();

  void Update1();
  void Update2();
  
};

