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
    //r.stop();
    double pFwd = 0.5;
    double pStrafe = 0.5;
    double pTurn = 0.2;
    MeccanumDrive d = robot.drive;

    d.setPowerFL(pFwd + pStrafe + pTurn);
    d.setPowerFR(pFwd - pStrafe - pTurn);
    d.setPowerBL(pFwd - pStrafe + pTurn);
    d.setPowerBR(pFwd + pStrafe - pTurn);
  }


  void driveTaskLoop1() {
    GamepadInterface gp = robot.gamepad;
    if (!gamepadEnabled && gp.a()) {
      gamepadEnabled = true;
    }

    if (!gamepadEnabled) {
      return; // ***** EARLY RETURN ******
    }
    
    if (robot.gamepad.right_bumper()) {         //right bumper makes the robot spin clockwise
      setPowerAll(0.5, -0.5, 0.5, -0.5); // FL FR BL BR
    } else if (robot.gamepad.left_bumper()) {    //left bumper makes the robot spin counterclockwise
      setPowerAll(-0.5, 0.5, -0.5, 0.5);
    } else {
      setPowerAll(0, 0, 0, 0);
    }
  }


  void setPowerAll(double pFL, double pFR, double pBL, double pBR) {
    MeccanumDrive d = robot.drive;
    d.setPowerFL(pFL);
    d.setPowerFR(pFR);
    d.setPowerBL(pBL);
    d.setPowerBR(pBR);
  }
}
