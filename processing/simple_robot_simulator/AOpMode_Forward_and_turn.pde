static class AOpMode_Forward_and_turn extends LinearOpMode {
  final Robot robot;
  

  AOpMode_Forward_and_turn(Robot r) {
    this.robot = r;
    
  }


  @Override
    public void runOpMode() {
      setStartingPower(0.5,0,0);
      long startMs = System.currentTimeMillis();
      robot.base.resetEncoders();
      double ticksPerMeter = 2;
      robot.base.setEncoderScale(ticksPerMeter);
      while (opModeIsActive() && !encoderReached(1.0)) { // (System.currentTimeMillis() - startMs) < 10000) {
        // Do nothing
      }
    /*  setStartingPower(0,0,-0.5);
      while(opModeIsActive() && !mangleReached(0)){
      }
      */
      double bob = -PI/2;
      setStartingPower(0,0, 0.5);
      while(opModeIsActive() && !angleReached(bob)){
        System.out.println("bob: " + bob*57.2957795 + "a: " + robot.base.a*57.2957795 + "AR: ");
        double error = balancedAngle(robot.base.a-bob);
        double pTurn = -error*0.5;
        setHybridPower(0, pTurn, 0);
      }
      robot.base.setMotorPowerAll(0, 0, 0, 0);
    }
    
    // Returns true if the current encoder value reaches or
    // exceeds {targetValue}
    boolean encoderReached(double targetValue) {
      // Just look up the average of the encoder values
      double average =  (robot.base.readEncoder(robot.base.FL)+robot.base.readEncoder(robot.base.FR)+robot.base.readEncoder(robot.base.BL)+ robot.base.readEncoder(robot.base.BR))/4;
      return  average >= targetValue;
    }
    boolean angleReached(double targetAngle) {
      return Math.abs(balancedAngle(balancedAngle(robot.base.a) + targetAngle)) < 0.0001 ;
    }
    boolean mangleReached(double targetAngle) {
      return balancedAngle(robot.base.a) <= targetAngle;
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
  void setHybridPower(double fwd, double turn, double strafe) {
    // Let's clip anyways, incase we get faulty input
    fwd = clipInput(fwd);
    turn = 0.5*clipInput(turn);
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
    double pFL = fwd - strafe + turn;
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
}
