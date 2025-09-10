import processing.serial.*;

Serial port1;

int[] value = {0,0,0};

boolean[] Yin = {false,false,false};

void setup() {
  //port1 = new Serial(this, Serial.list()[2], 9600);  
  size(1000, 640);
}
//----------------------------------------------------------//
void draw() {
  
  background(255);
  stroke(255,0,0);
  fill(255,0,0);
  rect(0,value[0]-105,333,120);
  
  stroke(0,255,0);
  fill(0,255,0);
  rect(333,value[1]-105,333,120);
  
  stroke(0,0,255);
  fill(0,0,255);
  rect(666,value[2]-105,333,120);
   
  for(int i=0;i<3;i++){
    if(Yin[i]){
      if(value[i]<100)value[i]+=5;
    }
    else {
      if(value[i]>0)value[i]-=5;
    }
  }
   
   
  
 /* while (port1.available() > 0) {
    String myString = port1.readStringUntil(10);
    if (myString != null) 
      println(myString);
  }*/
  
}
//----------------------------------------------------------//
void keyPressed(){
}
//----------------------------------------------------------//
void mouseClicked(){
}
//----------------------------------------------------------//
void mouseMoved() {
  Yin[0]=mouseY<100&&mouseX<333;
  Yin[1]=mouseY<100&&(mouseX>=333&&mouseX<=666);
  Yin[2]=mouseY<100&&(mouseX>666);
}
void close(){
 
}