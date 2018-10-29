// Robots and associated op modes are
// set up here.

void setup_robots() {
  
  // Create two robots, with their own names, colors, and initial position and orientation
  // Name choices are: ROBOT_[1-4].
  g_robots = new Robot[]{
    newRobot(ROBOT_1, color(0, 255, 0), g_field.BREADTH/2-0.5, g_field.DEPTH/2-0.5, radians(180)), 
    newRobot(ROBOT_2, color(255, 255, 0), g_field.BREADTH/2+.5, g_field.DEPTH/2+1.5, radians(180)) 
  }; 

  //
  // Setup each robot's op modes
  //
  
  // Iterative op modes
  g_iterativeOpModes = new IterativeOpMode[]{
    //new SampleIterativeOpMode(g_robots[0]), 
    //new SampleIterativeOpMode(g_robots[1])
    new DriveStraightOpMode(g_robots[0])
  };
  
  // Linear op modes
  g_linearOpModes = new LinearOpMode[]{
    new AOpMode_Forward_and_turn(g_robots[1])
  };

  assert g_robots.length == g_iterativeOpModes.length + g_linearOpModes.length;

}
