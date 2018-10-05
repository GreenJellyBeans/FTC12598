//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
//
void settings() {
  size(1500, 1000);
}


Field g_field;
GamepadManager g_gamepadMgr;
Robot g_robot;
long startTimeMs;
long prevTimeMs  = 0;
PApplet g_pa = this;

// Configuration settings
// These can be overridden by settings in the .\data\config.txt file.
boolean g_noGamepad = false; // Config setting "noGamepad" turns it on

void setup() {
  rectMode(CENTER);
  setFont();
  loadConfig();
  g_field = new Field();
  g_field.init();
  g_gamepadMgr = new GamepadManager("Gamepad-F310", g_noGamepad? 0 : 2);
  g_gamepadMgr.init();
  GamepadInterface gamepad1 = g_gamepadMgr.newProxyGamepad(g_gamepadMgr.ROBOT_1, g_gamepadMgr.ROLE_A);
  GamepadInterface gamepad2 = g_gamepadMgr.newProxyGamepad(g_gamepadMgr.ROBOT_1, g_gamepadMgr.ROLE_B);
  g_robot = new Robot(g_field, gamepad1, gamepad2); 
  startTimeMs = millis();
  g_robot.init();
  g_robot.start();
}


void setFont() {
  // The font "andalemo.ttf" must be located in the 
  // current sketch's "data" directory to load successfully
  PFont font = createFont("Consolas", 14);
  textFont(font);
}

void draw() {
  
  g_gamepadMgr.checkMappings();
  
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

// Load the config file config.txt
void loadConfig() {
  String[] configTxt = loadStrings("config.txt");
  for (String s : configTxt) {
    // Trim beginning and ending blanks and everything including and after #
    s = s.replaceFirst("#.*", "").trim();
    if (s.length() == 0) {
      continue;
    }
    println("CONFIG:[" + s + "]");
    if (s.equals("noGamepad")) {
      println("NO GAMEPAD!");
      g_noGamepad = true;
    }
  }
}
