// Class Robot implements a simulation of a ROBOT, for developing autonomous algorithms
// and potentially for drive practice.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
class Robot {

  final String id; // Used to identify the robot and select which robot gets which gamepads
  final Field field; // Passed in during constructor - the field the robot operates within
  final GamepadInterface gamepad1;
  final GamepadInterface gamepad2;
  final MecanumDrive drive;
  final DriveTask driveTask;
  final RawSensorModule sensors;
  final RobotProperties props;

  public Robot(String id, color c, Field f, GamepadInterface gamepad1, GamepadInterface gamepad2) {
    this.id = id;
    this.field = f;
    this.props = new RobotProperties();
    this.gamepad1  = gamepad1;
    this.gamepad2  = gamepad2;
    driveTask = new DriveTask(this);
    drive = new MecanumDrive(field, props, c);
    sensors = new RawSensorModule(f, this);
  }

  // Places the robot at the specified location and orientation.
  // Units are meters and radians.
  // This is typically used once - to initially position the robot
  // somewhere on the field.
  public void place(double x, double y, double a) {
    drive.place(x, y, a);
  }

  public void init() {
    sensors.init();
    driveTask.init();
  }


  public void deinit() {
  }


  public void start() {
    driveTask.start();
  }


  public void stop() {
    driveTask.stop();
  }


  public void loop(double t, double dT) {
    driveTask.loop();
  }

  // Updates the simulation,
  // assuming the absoute time is {t} seconds, and {dT} seconds have elapsed
  // since previous call
  public void simloop(double t, double dT) {
    drive.simloop(t, dT);
    sensors.simloop(t, dT);
    field.updateStatus(String.format("t:% 7.3f  x:%1.2f  y:%1.2f  a:%1.2f", t, drive.x, drive.y, balancedAngle(drive.a)));
  }


  public void draw() {
    displayGamepadStatus("GP1", gamepad1);
    displayGamepadStatus("GP2", gamepad2);
    double x = drive.x;
    double y = drive.y;
    double side = props.side;
    double a = drive.a;

    if (x < 0 || x > field.BREADTH || y < 0 || y > field.DEPTH) {
      fill(255, 0, 0);
      noLoop();
      field.addExtendedStatus("done");
    }

    noStroke();
    fill(drive.trail.c);
    field.drawCircle(x, y, props.side/4);
    drive.trail.draw();

    float pixSide = field.pixLen(side);
    float sx = field.screenX(x);
    float sy = field.screenY(y);
    pushMatrix();
    translate(sx, sy);
    rotate((float) -a);
    fill(255, 200);
    stroke(0);
    strokeWeight(1);
    rect(0, 0, pixSide, pixSide);  
    fill(0);

    // Render Robot ID in the center
    float adj0 = 5;
    text(id, -adj0, adj0);
    // Render L and R labels on the front left and right corners
    // of the robot. Note that these are in screen coordinates, where
    // y grows downwards! Also note that after rotating to be aligned
    // with the robot's x-axis, the robot is facing the x-axis, which is
    // towards the right!
    final int adj = -12; // adjustment for text size
    text("L", pixSide/2 + adj, -adj - pixSide/2);
    text("R", pixSide/2 + adj, pixSide/2 + 0.5 * adj);
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
      fill(sensors.sred(0), sensors.sgreen(0), sensors.sblue(0));
      if (id.equals(ROBOT_1)) {
        rect(width-120, height-50, 50, 50);
      } else if (id.equals(ROBOT_2)) {
        rect(width-60, height-50, 50, 50);
      }
    }
  }
};
