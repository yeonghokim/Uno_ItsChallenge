#include "Brake.h"
#include "pinDefine.h"
void Brake::Setup(){
  servo.attach(SERVO);
  Release();
}

void Brake::brake(){
  servo.write(110);
} 

void Brake::Release(){
  servo.write(0);
}

