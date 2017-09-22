TimeChecker tc = new TimeChecker();

void setup()
{

  MorseGenerator mg  = new MorseGenerator();
  size(800, 800);

  int[] delays ;
  delays = mg.generateDelays("JIVANA", 10);
  tc.setTimesByDurations(delays, 75);
  println(delays);
}

void draw() {
  processStages(tc);
}

void processStages(TimeChecker tc) {
  int stage = tc.getCurrentStage(); // Returns i where time is < ith value
  boolean lightsOn = false;
  if (stage % 2 == 1 && stage < tc.getNumStages()) {
    lightsOn = true; // turn lights on
  }
  //Sets the channel output voltage. If mode == 0: takes input from -1023-1023, output in the range -4 to +4 volts. If mode == 1, 2, or 3: takes input from 0-1023, output in the range 0 to 8 volts.
  int voltage = (int) ((3.5/4.0) * 1023); // 3.5 v
  if (lightsOn) {
    fill(0, 255, 0);
    voltage = 0; // 0 maps to 0V
  } else {
    fill(0);
  }
  //periodicLog("Setting ao voltage " + voltage);
  //ao.setAnalogOutputVoltage(voltage);

  ellipse(width/2, height/2, 100, 100);
}