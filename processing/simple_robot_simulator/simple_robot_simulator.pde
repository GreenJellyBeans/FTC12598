//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
//
void settings() {
  size(1000, 1000);
}


Field g_field;
Robot g_robot;
long startTimeMs;
long prevTimeMs  = 0;
PApplet g_pa = this;


void setup() {
  rectMode(CENTER);
  g_field = new Field();
  g_robot = new Robot(g_field);  
  startTimeMs = millis();
  g_robot.init();
  g_robot.start();
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
  g_robot.loop(t, dT);
  g_robot.simloop(t, dT);
  g_field.draw();
  g_robot.draw();

  if (frameCount == 2000) {
    g_robot.stop();
  }
}
