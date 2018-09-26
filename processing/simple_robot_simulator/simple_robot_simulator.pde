//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
void settings() {
  size(1000, 1000);
}

Field g_field;
MeccanumRobot g_robot;
long prevTimeMs  = 0;

void setup() {
  rectMode(CENTER);
  g_field = new Field();
  g_robot = new MeccanumRobot(g_field, g_field.WIDTH/2, g_field.WIDTH/2, 0);
  setStartingPower(g_robot);
}

void draw() {
  if (prevTimeMs == 0) {
    prevTimeMs = millis();
  }
  long now = millis();
  double dT = (now - prevTimeMs)/1000.0; // In seconds
  prevTimeMs = now;
  g_robot.simloop(dT);
  
  g_field.draw();
  g_robot.draw();
  
  if (frameCount == 200) {
    g_robot.markSpot();
    g_robot.stop();
  }
}

void setStartingPower(MeccanumRobot r) {
  //r.stop();
  double pFwd = 0.5;
  double pStrafe = 0;
  double pTurn = 0.1;
  
  r.setPowerFL(pFwd + pStrafe + pTurn);
  r.setPowerFR(pFwd + pStrafe - pTurn);
  r.setPowerBL(pFwd - pStrafe + pTurn);
  r.setPowerBR(pFwd - pStrafe - pTurn);
}
