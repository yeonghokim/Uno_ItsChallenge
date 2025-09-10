#include "CheckVelocity.h"
#include "Ultrasonic.h"
#include "Brake.h"
#include "pinDefine.h"

Brake brake;

UltraSonic ultra;

CheckVelocity check_v;

long timestack[5]={0};
long nowstack=0;

void setup() {
    // put your setup code here, to run once:
  Serial.begin(9600);
  brake.Setup();
  ultra.Setup();
  timestack[0]=millis();
  timestack[1]=timestack[0];
  timestack[2]=timestack[0];
  timestack[3]=timestack[0];
  timestack[4]=timestack[0];
  
}

void loop() {
  // put your main code here, to run repeatedly:
   nowstack =millis();
   if(nowstack -timestack[0]  > 50){
      check_v.Update();//1ms 마다 실행
      timestack[0] = nowstack;
   }
   if(nowstack -timestack[1]  > 500){
      timestack[1]=nowstack+10;
      ultra.Update1();
   }
   if(nowstack -timestack[2]>510){
      timestack[2] = nowstack;
      ultra.Update2();
   }
   
   if(ultra.D20){
      brake.brake();
      digitalWrite(LED,HIGH);
   }
   else{ 
      brake.Release();
      digitalWrite(LED,LOW);
   }
   




   
}
