//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
void settings() {
  size(800, 800);
}
final double FIELD_WIDTH = 12*12*0.0254; // field width in meters (12 ft).
MeccanumRobot g_robot;
long prevTimeMs  = 0;

void setup() {
  rectMode(CENTER);
  g_robot = new MeccanumRobot(FIELD_WIDTH/2, FIELD_WIDTH/2, 0);
  setStartingPower(g_robot);
}

void draw() {
  if (prevTimeMs == 0) {
    prevTimeMs = millis();
  }
  long now = millis();
  double dT = (now - prevTimeMs)/1000.0;
  prevTimeMs = now;
  g_robot.simloop(dT); // 0.1 simulated seconds have elapsed
  
  background(128);
  g_robot.draw();
}

void setStartingPower(MeccanumRobot r) {
  //r.stop();
  r.setPowerFR(-0.3);
  r.setPowerFL(0.3);
  r.setPowerBR(0.3);
  r.setPowerBL(-0.3);
}
