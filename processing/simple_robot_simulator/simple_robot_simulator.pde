//
/ Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
void settings() {
  size(800, 800);
}
MeccanumRobot robot;

void setup() {
  robot = new MeccanumRobot(height/2, width/2, 0);
}

void draw() {
  robot.simloop(0.1); // 0.1 simulated seconds have elapsed
  robot.draw();
}
