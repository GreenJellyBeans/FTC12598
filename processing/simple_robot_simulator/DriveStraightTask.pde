class DriveStraightTask implements DriveTask {

  double targetBearing = radians(180);
  Robot ra;
  final double Kp = 0.5;

  public DriveStraightTask(Robot r) {
    ra=r;
  }
  void init() {
    setStartingPower();
  }

  void deinit() {
  }

  void loop() {
    //get current bearing   
    Field f = ra.field;
    f.addExtendedStatus(String.format("ANGLE: %5.2f", balancedAngle(ra.base.a)));

    //find difference from target bearing (error)
    double error = balancedAngle(targetBearing - ra.base.a) ;
    f.addExtendedStatus(String.format("ERROR: %5.2f ", error));

    //fix it
    double pFwd = 0;//0.05;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = -error*Kp;

    /**/
    double pFL = (pFwd + pStrafe + pTurn);
    double pFR = (pFwd - pStrafe - pTurn);
    double pBL = (pFwd - pStrafe + pTurn);
    double pBR = (pFwd + pStrafe - pTurn);
    ra.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
    /**/
  }



  void start() {
  }

  void stop() {
  }

  void setStartingPower() {
    double pFwd = 0.1;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = 0;//0.01;
    double pFL = (pFwd + pStrafe + pTurn);
    double pFR = (pFwd - pStrafe - pTurn);
    double pBL = (pFwd - pStrafe + pTurn);
    double pBR = (pFwd + pStrafe - pTurn);
    ra.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
  }
}
