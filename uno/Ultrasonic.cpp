#include <Arduino.h>
#include "UltraSonic.h"
#include "pinDefine.h"

void UltraSonic::Setup(){
  lcd = new LiquidCrystal_I2C(0x3F,16,2); 
  lcd->init(); // initialize the lcd
  lcd->backlight(); 
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO,INPUT);
  pinMode(LED,OUTPUT);
}
float UltraSonic::CheckDistance(){


  
}
void UltraSonic::Update1(){
  digitalWrite(TRIG,HIGH);   
}
void UltraSonic::Update2(){
  int distanceCm;
  digitalWrite(TRIG,LOW);
  long duration = pulseIn(ECHO,HIGH);
  int imsi = duration*0.017;
  if(imsi<3000){
    distanceCm=imsi;
    lcd->clear();
    lcd->setCursor(0,0);
    lcd->print("distance : ");
    lcd->print(distanceCm,DEC);
  }
  D5 = distanceCm<=5;
  D20 = distanceCm<=20; 
}

