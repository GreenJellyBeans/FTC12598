//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
//
void settings() {
  size(1500, 1000);
}


// These are the supported roles...
final String ROLE_A = "A";
final String ROLE_B = "B";

// Thease are the supported robot IDs:
final String ROBOT_1 = "1";
final String ROBOT_2 = "2";  

Field g_field;
GamepadManager g_gamepadMgr;
Robot[] g_robots;
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
  g_robots = new Robot[]{
    newRobot(ROBOT_1, g_field.WIDTH/2, g_field.WIDTH/2, radians(180)), 
    newRobot(ROBOT_2, g_field.WIDTH/2+.5, g_field.WIDTH/2+.5, radians(180)) 
  }; 
  startTimeMs = millis();
  for (Robot r : g_robots) {
    r.init();
    r.start();
  }
}


// Construct and return a new robot with the specified ID and
// with initial position ({x}, {y}) in meters and heading {a} 
// in radians
Robot newRobot(String robotId, double x, double y, double a) {
  GamepadInterface gamepad1 = g_gamepadMgr.newProxyGamepad(ROBOT_1, ROLE_A);
  GamepadInterface gamepad2 = g_gamepadMgr.newProxyGamepad(ROBOT_1, ROLE_B); 
  Robot r =  new Robot(robotId, g_field, gamepad1, gamepad2) ;
  r.place(x, y, a);
  return r;
}

void setFont() {
  // The font "andalemo.ttf" must be located in the 
  // current sketch's "data" directory to load successfully
  PFont font = createFont("Consolas", 14);
  textFont(font);
}

void draw() {

  checkMappings();

  background(200);
  if (prevTimeMs == 0) {
    prevTimeMs = millis();
  }
  long now = millis();
  double t = (startTimeMs - now)/1000.0; // In seconds
  double dT = (now - prevTimeMs)/1000.0; // In seconds
  prevTimeMs = now;

  g_field.draw();
  for (Robot r : g_robots) {
    g_field.addExtendedStatus("\nROBOT " + r.id + " STATUS");    
    r.loop(t, dT); 
    r.simloop(t, dT); 
    r.draw();
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

// Must be called periodically
// to check if there is a change in mapping
// of real gamepads to robots and roles
void checkMappings() {
  for (GamepadManager.RealGamepad rg : g_gamepadMgr.realGamepads) {
    if (rg == null) continue; // ********* CONTINUE

    if (rg.start()) {
      if (rg.a()) {
        g_gamepadMgr.switchRoles(rg, ROLE_A);
      } else if (rg.b()) {
        g_gamepadMgr.switchRoles(rg, ROLE_B);
      }
    }
  }
}
