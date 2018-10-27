// Future replacement of simple_robot_simulator,
// using op modes instead of tasks.
IterativeOpMode[] g_iterativeOpModes;
LinearOpMode[] g_linearOpModes;

void new_setup_simulator() {
  rectMode(CENTER);
  setFont();
  loadConfig();
  g_field = new Field();
  g_field.init();
  g_gamepadMgr = new GamepadManager("Gamepad-F310", g_numGamepads);
  g_gamepadMgr.init();

  // Create two robots, with their own unique names, colors and initial position and orientation
  g_robots = new Robot[]{
    newRobot(ROBOT_1, color(0, 255, 0), g_field.BREADTH/2-0.5, g_field.DEPTH/2-0.5, radians(180)), 
    newRobot(ROBOT_2, color(255, 255, 0), g_field.BREADTH/2+.5, g_field.DEPTH/2+.5, radians(180)) 
  }; 

  // Setup each robot's op modes
  g_iterativeOpModes = new IterativeOpMode[]{
    new SampleIterativeOpMode(g_robots[0]),
    new SampleIterativeOpMode(g_robots[1])
  };
  g_linearOpModes = new LinearOpMode[]{
  };

  assert g_robots.length == g_iterativeOpModes.length + g_linearOpModes.length;

  startTimeMs = millis();

  // Initialize all robots
  for (Robot r : g_robots) {
    r.init();
  }

  // Register and start all op modes
  for (IterativeOpMode op : g_iterativeOpModes) {
    OpModeManager.registerIterativeOpMode(op);
  }
  OpModeManager.startAll();
}


// Must be called from Processing's draw method
void new_simulator_loop() {

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
