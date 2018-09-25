//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
void settings() {
  size(800, 800);
}
MeccanumRobot g_robot;

void setup() {
  rectMode(CENTER);
  g_robot = new MeccanumRobot(height/2, width/2, 0);
  setStartingPower(g_robot);
}

void draw() {
  g_robot.simloop(0.1); // 0.1 simulated seconds have elapsed
  
  background(128);
  g_robot.draw();
}

void setStartingPower(MeccanumRobot r) {
  r.setPowerFL(0.4);
  r.setPowerBL(0.5);
}
