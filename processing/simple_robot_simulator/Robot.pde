// Class Robot implements a simulation of a ROBOT, for developing autonomous algorithms
// and potentially for drive practice.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
class Robot {
  final String id; // Used to identify the robot and select which robot gets which gamepads
  final Field field; // Passed in during constructor - the field the robot operates within
  final GamepadInterface gamepad1;
  final GamepadInterface gamepad2;
  final DriveBase base;
  final MecanumDrive drive;
  final RawSensorModule sensors;
  final RobotProperties props;

  public Robot(String id, color c, Field f, GamepadInterface gamepad1, GamepadInterface gamepad2) {
    this.id = id;
    this.field = f;
    this.props = new RobotProperties();
    this.gamepad1  = gamepad1;
    this.gamepad2  = gamepad2;
    base = new DriveBase(field, props, c);
    drive = new MecanumDrive(base);
    sensors = new RawSensorModule(f, this);
  }

  // Places the robot at the specified location and orientation.
  // Units are meters and radians.
  // This is typically used once - to initially position the robot
  // somewhere on the field.
  public void place(double x, double y, double a) {
    base.place(x, y, a);
  }

  public void init() {
    sensors.init();
  }


  public void deinit() {
  }


  // Updates the simulation,
  // assuming the absoute time is {t} seconds, and {dT} seconds have elapsed
  // since previous call
  public void simloop(double t, double dT) {
    drive.simloop(t, dT);
    sensors.simloop(t, dT);
  }


  public void draw() {
    displayGamepadStatus("GP1", gamepad1);
    displayGamepadStatus("GP2", gamepad2);
    double x = base.cx;
    double y = base.cy;
    double side = props.side;
    double a = base.a;

    if (x < 0 || x > field.BREADTH || y < 0 || y > field.DEPTH) {
      fill(255, 0, 0);
      noLoop();
      field.addExtendedStatus("done");
    }

    noStroke();
    fill(base.trail.c);
    field.drawCircle(x, y, props.side/4);
    base.trail.draw();

    float pixSide = field.pixLen(side);
    float sx = field.screenX(x);
    float sy = field.screenY(y);

    pushMatrix();
    
    translate(sx, sy);
    rotate((float) -a); // In Processing, rotate(t) rotates the axes by -t
    fill(255, 200);
    stroke(0);
    strokeWeight(1);
    rect(0, 0, pixSide, pixSide);  
    fill(0);

    // Render Robot ID in the center
    float adj0 = 5;
    textAlign(CENTER);
    text(id, 0, adj0);
    // Render L and R labels on the front left and right corners
    // of the robot. Note that these are in screen coordinates, where
    // y grows downwards! Also note that after rotating to be aligned
    // with the robot's x-axis, the robot is facing the x-axis, which is
    // towards the right!
    final int adj = -10; // adjustment for text size
    text("L", pixSide/2 + adj, -1.5*adj - pixSide/2);
    text("R", pixSide/2 + adj, pixSide/2 + 0.9*adj);
    popMatrix();
    
    visualizeSensorData();
  }


  // Shows state of gamepad controls in the field's extended status area
  void displayGamepadStatus(String prefix, GamepadInterface gp) {
    if (g_numGamepads == 0) {
      field.addExtendedStatus(prefix + " GAMEPAD OFF");
      return; // ******* EARLY RETURN
    }
    field.addExtendedStatus(String.format(prefix + " STICKS LSx: %5.2f  LSy: %5.2f  RSx: %5.2f  RSy: %5.2f", 
      gp.left_stick_x(), gp.left_stick_y(), gp.right_stick_x(), gp.right_stick_y()
      ));

    String buttons =  
      (gp.start()? " S" : "") + 
      (gp.left_bumper()? " LB" : "") + 
      (gp.right_bumper()? " RB" : "") + 
      (gp.a()? " A" : "") + 
      (gp.y()? " Y" : "");
    String buttonStatus = prefix + ((buttons.length() == 0) ? " BUTTONS: none" : " BUTTONS: " + buttons);
    String dpad = "  DPAD " + gp.hatPos();
    field.addExtendedStatus(buttonStatus + dpad);
  }


  // Display/print the raw state of the sensors
  void visualizeSensorData() {
    if (sensors.numColorSensors()>0) {
      fill(sensors.color_red(0), sensors.color_green(0), sensors.color_blue(0));
      if (id.equals(ROBOT_1)) {
        rect(width-120, height-50, 50, 50);
      } else if (id.equals(ROBOT_2)) {
        rect(width-60, height-50, 50, 50);
      }
    }
  }
}
