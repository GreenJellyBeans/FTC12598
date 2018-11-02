static class AOpMode_Forward_and_turn extends LinearOpMode {
  final Robot robot;


  AOpMode_Forward_and_turn(Robot r) {
    this.robot = r;
  }


  @Override
    public void runOpMode() {
    
    encoderDriveMec(0.5,1.3);
    
    imuBearingMec(0.7, -135);
    
    encoderDriveMec(0.5, 2.25);
   
  
    robot.base.setMotorPowerAll(0, 0, 0, 0);
    System.out.println("im completely done");
  }
  
  
  // Drive forward with speed {speed} and distance {forward}
  // Distance is in meters.
  void encoderDriveMec (double speed, double forward){
     robot.sensors.encoder_resetAll();
     setStartingPower(speed, 0, 0);
   // long startMs = System.currentTimeMillis();
    double ticksPerMeter = 1;
    robot.sensors.encoder_setScale(ticksPerMeter);
    while (opModeIsActive() && !encoderReached(forward)) { // (System.currentTimeMillis() - startMs) < 10000) {
      // Do nothing
    
    }
    robot.base.setMotorPowerAll(0, 0, 0, 0);
 }
 
 
 // Turn with max speed {speed} (which must be positive)
 // and angle {angle} in degrees.
  void imuBearingMec (double speed, double angle){
     setStartingPower(0, 0, speed);
    robot.sensors.imu_reset(); // Sets current bearing to 0
    double bob = radians(angle); // Target
   while (opModeIsActive() && !angleReached(bob)) {
      double bearing = robot.sensors.imu_bearing();
      System.out.println("bob: " + balancedAngle(bob)*57.2957795 + "bearing: " + balancedAngle(bearing)*57.2957795);
      double error = balancedAngle(bob - bearing);
      final double kP = 5;
      double pTurn = -error*kP;
      pTurn = clipInput(pTurn, speed);
      setHybridPower(0, 0, pTurn);
    }
  }


  // Returns true if the current encoder value reaches or
  // exceeds {targetValue}
  boolean encoderReached(double targetValue) {
    // Just look up the average of the encoder values
    double average =  (robot.sensors.encoder_FL()+robot.sensors.encoder_FR()
      +robot.sensors.encoder_BL()+robot.sensors.encoder_BR())/4;
    return  0.01 >= Math.abs(targetValue-average);
  }
  boolean angleReached(double targetAngle) {
    return Math.abs(balancedAngle(balancedAngle(robot.sensors.imu_bearing()) - targetAngle)) < radians(2) ;
  }
//  boolean mangleReached(double targetAngle) {
//    return balancedAngle(robot.base.a) <= targetAngle;
//  }

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
