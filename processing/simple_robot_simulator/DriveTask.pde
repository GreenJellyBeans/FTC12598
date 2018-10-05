// The DriveTask collects together methods that control the robot movement
class DriveTask {

  final Robot robot;
  boolean gamepadEnabled = false; // Stays disabled until "A" button is pressed

  public DriveTask(Robot r) {
    robot = r;
  }


  void init() {
  }


  void start() {
    setStartingPower();
  }


  void stop() {
    robot.drive.stop();
  }


  void loop() {
    driveTaskLoop1();
  }


  void deinit() {
  }


  void setStartingPower() {
    double pFwd = 0;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = 0.1;
    MeccanumDrive d = robot.drive;

    d.setPowerFL(pFwd + pStrafe + pTurn);
    d.setPowerFR(pFwd - pStrafe - pTurn);
    d.setPowerBL(pFwd - pStrafe + pTurn);
    d.setPowerBR(pFwd + pStrafe - pTurn);
  }


  void driveTaskLoop1() {
    GamepadInterface gp = robot.gamepad1;
    if (!gamepadEnabled && gp.a()) {
      gamepadEnabled = true;
    }

    if (!gamepadEnabled) {
      return; // ***** EARLY RETURN ******
    }

    if (gp.right_bumper()) {         //right bumper makes the robot spin clockwise
      setPowerAll(0.5, -0.5, 0.5, -0.5); // FL FR BL BR
    } else if (gp.left_bumper()) {    //left bumper makes the robot spin counterclockwise
      setPowerAll(-0.5, 0.5, -0.5, 0.5);
    } else {
      double fwd  = gp.right_stick_y();
      double turn  = gp.left_stick_x();
      double strafe = gp.right_stick_x();
      setHybridPower(fwd, turn, strafe);
    }
  }

  // Sets the power to each of the 4 motors of the meccanum drive given
  // the incoming request to go forward, turn and strafe by amounts
  // ranging within [-1, 1]
  void setHybridPower(double fwd, double turn, double strafe) {
    // Let's clip anyways, incase we get faulty input
    fwd = clipInput(fwd);
    turn = clipInput(turn);
    strafe = clipInput(strafe);
    
    Field f = robot.field;
    f.addExtendedStatus(String.format("HPOWER  fwd:%5.2f  turn:%5.2f  strafe:%5.2f", fwd, turn, strafe));

    double pFL = fwd + strafe + turn;
    double pFR = fwd - strafe - turn;
    double pBL = fwd - strafe + turn;
    double pBR =fwd + strafe - turn;

    // m is the max absolute value of the individual motor power amounts. If it is too small, we stop all motors.
    double m = Math.max(Math.max(Math.abs(pFL), Math.abs(pFR)), Math.max(Math.abs(pBL), Math.abs(pBR)));
    if (m<0.1) {
      setPowerAll(0, 0, 0, 0);
    } else {
      // Scale everything so no magnitude exeeds 1
      double scale = Math.max(1/m, 1);
      pFL *= scale;
      pFR *= scale;
      pBL *= scale;
      pBR *= scale;
      setPowerAll(pFL, pFR, pBL, pBR);
    }
  }

  // Clips input to be within [-1, 1]
  double clipInput(double in) {
    return Math.max(Math.min(in, 1), -1);
  }


  void setPowerAll(double pFL, double pFR, double pBL, double pBR) {
    MeccanumDrive d = robot.drive;
    d.setPowerFL(pFL);
    d.setPowerFR(pFR);
    d.setPowerBL(pBL);
    d.setPowerBR(pBR);
  }
}
