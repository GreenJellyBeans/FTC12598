// A sample iterative op mode that uses gamepad1 to control the robot. It uses the
// left joystick's x to calculate turn amount, and the right joystick's 
// x and y to control strafe and forward motion, respectively.
// Also maps left and right bumpers to a fixed amount of pure turn motion.
static class SampleIterativeOpMode extends IterativeOpMode {
  final Robot robot;
  boolean gamepadEnabled = false; // Stays disabled until "A" button is pressed


  SampleIterativeOpMode(Robot r) {
    this.robot = r;
  }


  @Override
    public void init() {
  }


  @Override
    public void deinit() {
  }


  @Override
    public void start() {
    setStartingPower();
  }


  @Override
    public void stop() {
    robot.drive.stop();
  }


  @Override
    public void loop() {
    GamepadInterface gp = robot.gamepad1;
    if (!gamepadEnabled && gp.a()) {
      gamepadEnabled = true;
    }

    if (!gamepadEnabled) {
      return; // ***** EARLY RETURN ******
    }
    // If "Y" button is pressed, we clear the encoder values on all motors.
    if (gp.y()) {
      robot.base.resetEncoders();
    }

    if (gp.right_bumper()) {         //right bumper makes the robot spin clockwise
      robot.base.setMotorPowerAll(0.5, -0.5, 0.5, -0.5); // FL FR BL BR
    } else if (gp.left_bumper()) {    //left bumper makes the robot spin counterclockwise
      robot.base.setMotorPowerAll(-0.5, 0.5, -0.5, 0.5);
    } else {
      double fwd  = gp.right_stick_y();
      double turn  = gp.left_stick_x();
      double strafe = gp.right_stick_x();
      setHybridPower(fwd, turn, strafe);
    }
  }


  void setStartingPower() {
    double pFwd = 0;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = 0.3;
    double pFL = (pFwd + pStrafe + pTurn);
    double pFR = (pFwd - pStrafe - pTurn);
    double pBL = (pFwd - pStrafe + pTurn);
    double pBR = (pFwd + pStrafe - pTurn);
    robot.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
  }


  // Sets the power to each of the 4 motors of the mecanum drive given
  // the incoming request to go forward, turn and strafe by amounts
  // ranging within [-1, 1]
  void setHybridPower(double fwd, double turn, double strafe) {
    // Let's clip anyways, incase we get faulty input
    fwd = clipInput(fwd);
    turn = 0.5*clipInput(turn);
    strafe = clipInput(strafe);

    Field f = robot.field;
    f.addExtendedStatus(String.format("HPOWER  fwd:%5.2f  turn:%5.2f  strafe:%5.2f", fwd, turn, strafe));

    // Note: +ve strafe makes the robot go right
    // Robot forces:    /\
    //                  \/
    // So, for example, to strafe right, the FL wheel must rotate forwards, i.e.,
    // positive power sent to the FL wheel.
    double pFL = fwd + strafe + turn;
    double pFR = fwd - strafe - turn;
    double pBL = fwd - strafe + turn;
    double pBR = fwd + strafe - turn;

    // m is the max absolute value of the individual motor power amounts. If it is too small, we stop all motors.
    double m = Math.max(Math.max(Math.abs(pFL), Math.abs(pFR)), Math.max(Math.abs(pBL), Math.abs(pBR)));
    if (m<0.1) {
      robot.base.setMotorPowerAll(0, 0, 0, 0);
    } else {
      // Scale everything so no magnitude exeeds 1
      double scale = Math.min(1/m, 1);
      pFL *= scale;
      pFR *= scale;
      pBL *= scale;
      pBR *= scale;
      robot.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
    }
  }


  // Clips input to be within [-1, 1]
  double clipInput(double in) {
    return Math.max(Math.min(in, 1), -1);
  }
}
