//
// Very simple physics-based robot simulator.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
//



// These are the supported roles...
final String ROLE_A = "A";
final String ROLE_B = "B";

// Thease are the supported robot IDs:
// We are limited to 4 robots simply because
// we use the Gamepad Hat positions N, S, E, W
// to delect robots (see method checkGamepadMappings).
// These can be renamed anything, but one or two characters
// is preferable because it is rendered on the robot.
final String ROBOT_1 = "1";
final String ROBOT_2 = "2";  
final String ROBOT_3 = "3";
final String ROBOT_4 = "4";  

Field g_field; // Keeps the state of the field and field elements, not including robots
GamepadManager g_gamepadMgr; // Manages gamepads and mappings from real to proxy gamepads
Robot[] g_robots; // The robots on the field
long startTimeMs;
long prevTimeMs  = 0;
PApplet g_pa = this;

// Configuration file name - Should be located under ./data/
final String CONFIG_FILE = "config.txt"; 

// Configuration settings - can be overridden by settings in the config file.
int g_numGamepads = 0; // Config setting "numGamepads" overrides this value

static SimpleLogger g_logger = new SimpleLogger();

// The list of op modes. These are filled out
// in setup_robots.pde
IterativeOpMode[] g_iterativeOpModes;
LinearOpMode[] g_linearOpModes;


void settings() {
  size(1500, 1000);
}


void setup() {
  setup_simulator();

  //test_op_modes();
}


void draw() {
  simulator_loop();
  //test_op_modes_loop();
}


void setup_simulator() {
  rectMode(CENTER);
  setFont();
  loadConfig();
  g_field = new Field();
  g_field.init();
  g_gamepadMgr = new GamepadManager("Gamepad-F310", g_numGamepads);
  g_gamepadMgr.init();
  setup_robots();

  startTimeMs = millis();

  // Initialize all robots
  for (Robot r : g_robots) {
    r.init();
  }

  // Register and start all op modes
  for (IterativeOpMode op : g_iterativeOpModes) {
    OpModeManager.registerIterativeOpMode(op);
  }
  for (LinearOpMode op : g_linearOpModes) {
    OpModeManager.registerLinearOpMode(op);
  }
  OpModeManager.startAll();
}


// Must be called from Processing's draw method
void simulator_loop() {

  // Check if the user would like to re-map
  // which real gamepads are mapped to which roles on
  // which robots
  checkGamepadMappings();

  // Clears the window so the current view is redrawn from scratch each time.
  background(200); 

  // Initialze prevTimeMs
  if (prevTimeMs == 0) {
    prevTimeMs = millis();
  }

  // Calculate how much has elapsed since the
  // last time draw() was called (dT) and since
  // the animation was started (t)
  long now = millis();
  double t = (now - startTimeMs)/1000.0; // In seconds
  double dT = (now - prevTimeMs)/1000.0; // In seconds
  prevTimeMs = now;

  g_field.updateStatus(String.format("t:% 7.3f", t)); 

  // Draw the field
  g_field.draw();

  // Service the op modes.
  OpModeManager.loopAll();

  // Update various aspects of each of the robots
  for (int i = 0; i < g_robots.length; i++ ) {
    Robot r = g_robots[i];
    g_field.addExtendedStatus("\nROBOT " + r.id + " STATUS");
    r.simloop(t, dT); // Physics simulation
    r.draw(); // Draw the robot on the field
  }
}



// Construct and return a new robot with the specified ID and color
// with initial position ({x}, {y}) in meters and heading {a} 
// in radians
Robot newRobot(String robotId, color c, double x, double y, double a) {
  GamepadInterface gamepad1 = g_gamepadMgr.newProxyGamepad(robotId, ROLE_A);
  GamepadInterface gamepad2 = g_gamepadMgr.newProxyGamepad(robotId, ROLE_B); 
  Robot r = new Robot(robotId, c, g_field, gamepad1, gamepad2) ;
  r.place(x, y, a);
  return r;
}


void setFont() {
  PFont font = createFont("Consolas", 14);
  textFont(font);
}




// Load the config file, which is expected to be located under ./data/
void loadConfig() {
  String[] configTxt = loadStrings(CONFIG_FILE); 
  for (String s : configTxt) {

    // Trim beginning and ending blanks and everything including and after #
    // which is the comment char, and replace tabs with spaces
    s = s.replaceFirst("#.*", "").trim();
    s = s.replaceAll("\t", "");
    if (s.length() == 0) {
      continue;
    }
    println("CONFIG LINE:[" + s + "]"); 
    if (s.startsWith("numGamepads")) {
      g_numGamepads = parseIntConfig(s, 0, 4, g_numGamepads);
    }
  }
}


// Must be called periodically to check if there is a change in mapping
// of real gamepads to robots and roles
void checkGamepadMappings() {

  for (GamepadManager.RealGamepad rg : g_gamepadMgr.realGamepads) {

    if (rg == null) continue; // ********* CONTINUE

    // Check if the hat specifies a valid robot ID
    // 2 == UP == ROBOT_1; 4 == RIGHT == ROBOT_2   
    int hatPos = rg.hatPos();
    boolean idSelected = hatPos == 2 || hatPos == 4;

    // Check if a role has been identified
    boolean roleSelected = rg.a() || rg.b();

    // Either START must be pressed or a Robot ID must be specified (valid hat press),
    // plus the role identifed.
    if (roleSelected && (idSelected || rg.start())) {
      String robotId = null;
      if (hatPos == 2) {
        robotId = ROBOT_1;
      } else if (hatPos == 4) {
        robotId = ROBOT_2;
      } else if (hatPos == 6) {
        robotId = ROBOT_3;
      } else if (hatPos == 8) {
        robotId = ROBOT_4;
      }
      // Switch roles...
      if (rg.a()) {
        g_gamepadMgr.switchRoles(rg, robotId, ROLE_A);
      } else if (rg.b()) {
        g_gamepadMgr.switchRoles(rg, robotId, ROLE_B);
      }
    }
  }
}

// Parse a config string, ignoring the first token (name)
// If there is a parse error or the value is outside of
// [{min}, {max}] return {defaultValue} instead
int parseIntConfig(String s, int min, int max, int defaultValue) {
  int ret = defaultValue;
  try {
    int i = s.indexOf(" ");
    ret = Integer.parseInt(s.substring(i+1));
    ret = inBounds(ret, min, max) ? ret : defaultValue;
  }
  catch (NumberFormatException e) {
    System.err.println("Invalid integer in configuration string [" + s + "]");
    ret = defaultValue;
  }
  return ret;
}
