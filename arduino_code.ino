bool light = false;
int num = 0;

void setup() {
  Serial.begin(9600); // initialization
  pinMode(LED_BUILTIN, OUTPUT);
  //Serial.println("Press 1 to LED ON or 0 to LED OFF...");
}

void loop() {
  if(num == 0)
    num = 1;
  else
    num = 0;
  if(light == false){
    Serial.println(num);
    digitalWrite(LED_BUILTIN, LOW);
    light = true;
  }else{
    Serial.println(num);
    digitalWrite(LED_BUILTIN, HIGH);
    light = false;
  }
  delay(1000);
  /*if (Serial.available() > 0) {  // if the data came
    incomingByte = Serial.read(); // read byte
    if(incomingByte == '0') {
       digitalWrite(LED_BUILTIN, LOW);  // if 1, switch LED Off
       Serial.println("LED OFF. Press 1 to LED ON!");  // print message
    }
    if(incomingByte == '1') {
       digitalWrite(LED_BUILTIN, HIGH); // if 0, switch LED on
       Serial.println("LED ON. Press 0 to LED OFF!");
    }
  }*/
}
