/*
  AUTHOR:     Orlando S. Hoilett
  DISCLAIMER
  
  This code is in the public domain. Please feel free to modify,
  use, etc however you see fit. But, please give reference to
  original authors as a courtesy to Open Source developers.
*/


const int analogPin = A0; //Arduino Serial Input pin
int analogIn = 0;
unsigned long pulseCounter = 0;
unsigned long firstPulseStartTime = 0;
unsigned long secondPulseStartTime = 0;
unsigned long time_between_pulses = 0;
const unsigned long refractoryPeriod = 300;
const double minutes_in_milliseconds = 60000;


const double threshold = 0.40;


void setup()
{
  Serial.begin(115200);
  
  delay(2000); //a slight delay for system stabilization
}


void loop()
{
  //creates a timer variable to keep track of time
  unsigned long timer = millis();
  
  analogIn = analogRead(analogPin); //assign analog_in to sensorval
  double voltage = analogToDigital(analogIn); //assign input to turn into voltage
  double absorbance = calculateAbsorbance(voltage); //turn voltage into absorbance
  long time_between_pulses = detectThreshold(absorbance);
  int pulseRate = calculatePulseRate(time_between_pulses);
  displayPulseInBluetooth(absorbance, pulseRate);
  //small delay to change our sampling rate
  //and stabilize our signal
  delay(25);  
}

//convertToVoltage()
//Does the calculation to convert the Arduino's analog-to-digital
//converter number to a voltage. This function then returns the
//value to the rest of the program.
double analogToDigital(double ADC_Val)
{
  double volt = 0;
  volt = 5*(ADC_Val/1023);
  return volt;
}

//calculateAbsorbance()
//Does the caluclation to convert the voltage to an absorbance.
//The value of the absorbance is then returned to the rest of
//the program
double calculateAbsorbance(double volt)
{
  double absorbance = 0;
  absorbance = log10(5/volt);
  return absorbance;  
}

//detectThreshold()
//This method detects whether the signal has passed our
//threshold and determines the time between subsequent peaks
long detectThreshold(double absorbance)
{
  if (millis() - firstPulseStartTime >= refractoryPeriod
    && absorbance >= threshold)
  {
    if (pulseCounter == 0)
    {
      pulseCounter++;
      firstPulseStartTime = millis();
    }
    else if (pulseCounter == 1)
    {
      secondPulseStartTime = millis();
      time_between_pulses = secondPulseStartTime - firstPulseStartTime;
      firstPulseStartTime = secondPulseStartTime;
    }
  }

  return time_between_pulses;
}

//calculatePulseRate()
//This method calculates pulse rate by dividing 60 seconds by the
//time between subsequent pulses
double calculatePulseRate(long time_between_pulses)
{
  return minutes_in_milliseconds/time_between_pulses;
}
//displayPulseInBluetooth()
//send the output to bluetooth
void displayPulseInBluetooth(double absorbance, int pulseRate)
{
  Serial.print(absorbance,5);
  Serial.print("\t");
  Serial.print(pulseRate);
  Serial.println();
}
