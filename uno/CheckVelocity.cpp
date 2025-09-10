#include <Arduino.h>
#include "CheckVelocity.h"
#include "pinDefine.h"

float CheckVelocity::GetNowVelocity(){
  return Velocity;
}
void CheckVelocity::Update(){
  TimeGap++;
  if(IsMagnet()){
    Velocity = WheelDistance / TimeGap;
    TimeGap++;
  }
}
bool CheckVelocity::IsMagnet(){

   //13번 핀
  int m = analogRead(MAGNETIC);

  return m<VELOCITY_DELTA;
}
