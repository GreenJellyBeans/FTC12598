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
long startTimeMs;
long prevTimeMs  = 0;

void setup() {
  rectMode(CENTER);
  g_field = new Field();
  g_robot = new MeccanumRobot(g_field, g_field.WIDTH/2, g_field.WIDTH/2, 0);
  setStartingPower(g_robot);
  startTimeMs = millis();
}

void draw() {
  background(200);
  if (prevTimeMs == 0) {
    prevTimeMs = millis();
  }
  long now = millis();
  double t = (startTimeMs - now)/1000.0; // In seconds
  double dT = (now - prevTimeMs)/1000.0; // In seconds
  prevTimeMs = now;
  g_robot.simloop(t, dT);
  
  g_field.draw();
  g_robot.draw();
  
  if (frameCount == 2000) {
    g_robot.markSpot();
    g_robot.stop();
  }
}

void setStartingPower(MeccanumRobot r) {
  //r.stop();
  double pFwd = 0.5;
  double pStrafe = 0.5;
  double pTurn = 0.2;
  
  r.setPowerFL(pFwd + pStrafe + pTurn);
  r.setPowerFR(pFwd - pStrafe - pTurn);
  r.setPowerBL(pFwd - pStrafe + pTurn);
  r.setPowerBR(pFwd + pStrafe - pTurn);
}
