// MecanumWizard contains some methods useful for autonomous control of mecanum drives.
static class MecanumWizard {
  
  private final LinearOpMode om;
  private final Robot robot;


  public MecanumWizard(LinearOpMode om, Robot r) {
    this.robot = r;
    this.om = om;
  }


  // Drive forward with speed {speed} and distance {forward}
  // Distance is in *inches*.
  void encoderDriveMecInches (double speed, double forward) {
    encoderDriveMec(speed, meters(forward/12.0));
  }


  // Drive forward with speed {speed} and distance {forward}
  // Distance is in meters.
  void encoderDriveMec (double speed, double forward) {
    robot.sensors.encoder_resetAll();
    double ticksPerMeter = 1;
    robot.sensors.encoder_setScale(ticksPerMeter);
    setStartingPower(speed, 0, 0);
    // long startMs = System.currentTimeMillis();

    robot.sensors.encoder_setScale(ticksPerMeter);
    while (om.opModeIsActive() && !encoderReachedFB(forward)) {
      // (System.currentTimeMillis() - startMs) < 10000) {
      // Do nothing
    }
    robot.base.setMotorPowerAll(0, 0, 0, 0);
  }


  // Strafe sideways with speed {speed} and distance {sideways}
  // Distance is in *inches*.
  void encoderStrafeMecInches (double speed, double sideways) {
    encoderStrafeMec(speed, meters(sideways/12.0));
  }


  // Strafe sideways with speed {speed} and distance {sideways}
  // Distance is in meters. Positive distance goes left (increasing
  // y direction in robot coordinates).
  void encoderStrafeMec(double speed, double sideways) {
    robot.sensors.encoder_resetAll();
    double ticksPerMeter = 1;
    robot.sensors.encoder_setScale(ticksPerMeter);
    setStartingPower(0, speed, 0); // strafe
    // long startMs = System.currentTimeMillis();
    while (om.opModeIsActive() && !encoderReachedLR(sideways)) { 
      // (System.currentTimeMillis() - startMs) < 10000) {
      // Do nothing
    }
    robot.base.setMotorPowerAll(0, 0, 0, 0);
  }


  // Turn with max speed {speed} (which must be positive)
  // and angle {angle} in degrees. {timeout} is in milliseconds
  void imuBearingMec (double speed, double angle, double timeoutMs) {
    long startTime = System.currentTimeMillis();
    robot.sensors.imu_reset(); // Sets current bearing to 0
    double bob = radians(angle); // Target
    while (om.opModeIsActive() && !angleReached(bob) && System.currentTimeMillis() - startTime < timeoutMs) {
      double bearing = robot.sensors.imu_bearing();
      System.out.println("bob: " + balancedAngle(bob)*57.2957795 + "bearing: " + balancedAngle(bearing)*57.2957795);
      double error = balancedAngle(bob - bearing);
      final double kP = 1;
      double pTurn = -error*kP;
      pTurn = clipInput(pTurn, speed);
      setHybridPower(0, 0, pTurn);
    }
  }


  // Returns true if the current composite encoder value reaches or
  // exceeds {targetValue} in the Forward/Backward direction (FB)
  boolean encoderReachedFB(double targetValue) {
    // Just calculate the average of the encoder values: (FL+FR+BL+BR)/4
    double average =  (robot.sensors.encoder_FL()+robot.sensors.encoder_FR()
      +robot.sensors.encoder_BL()+robot.sensors.encoder_BR())/4;
    return  0.01 >= Math.abs(targetValue-average);
  }


  // Returns true if the current composite encoder value reaches or
  // exceeds {targetValue} in the Left/Right direction (LR).
  // Left is positive.
  boolean encoderReachedLR(double targetValue) {
    // (FL+BR-FR-BL)/4;
    double average =  (robot.sensors.encoder_FL()+robot.sensors.encoder_BR()
      -robot.sensors.encoder_FR() - robot.sensors.encoder_BL())/4;
    return  0.01 >= Math.abs(targetValue-average);
  }


  boolean angleReached(double targetAngle) {
    return Math.abs(balancedAngle(balancedAngle(robot.sensors.imu_bearing()) - targetAngle)) < radians(3) ;
  }


  void setStartingPower(double pFwd, double pStrafe, double pTurn) {
    //  double pFwd = 0.5;
    //  double pStrafe = 0;//0.5;
    //  double pTurn = 0;
    double pFL = (pFwd + pStrafe + pTurn);
    double pFR = (pFwd - pStrafe - pTurn);
    double pBL = (pFwd - pStrafe + pTurn);
    double pBR = (pFwd + pStrafe - pTurn);
    robot.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
  }


  // Sets the power to each of the 4 motors of the mecanum drive given
  // the incoming request to go forward, turn and strafe by amounts
  // ranging within [-1, 1]
  void setHybridPower(double fwd, double strafe, double turn) {
    // Let's clip anyways, incase we get faulty input
    fwd = clipInput(fwd);
    turn =clipInput(turn);
    strafe = clipInput(strafe);

    Field f = robot.field;
    f.addExtendedStatus(String.format("HPOWER  fwd:%5.2f  turn:%5.2f  strafe:%5.2f", fwd, turn, strafe));

    // Note: +ve strafe makes the robot go right, and with
    // the robot's front facing increasing x, to go right
    // means to go in the direction of decreasing y:
    //
    //                 ^ y-axis
    //      robot      |
    //    ...... FL    |
    //    .    .       --> x-axis
    //    ...... FR
    //
    double pFL = fwd - strafe + turn ;
    double pFR = fwd + strafe - turn;
    double pBL = fwd + strafe + turn;
    double pBR = fwd - strafe - turn;

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


  double clipInput(double in, double mx) {
    return Math.max(Math.min(in, mx), -mx);
  }
}
