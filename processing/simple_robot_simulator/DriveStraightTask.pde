class DriveStraightTask implements DriveTask {

  double targetBearing = radians(180);
  double targetX = meters(1);
  double targetY = meters(6);
  Robot ra;
  final double AKp = 2;
  final double DKp = 1;
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
    f.addExtendedStatus(String.format("ANGLE: %5.2f", balancedAngle(ra.drive.a)));
    stroke(255, 0, 0);
    strokeWeight(4);
    f.drawPoint(targetX, targetY);

    //find difference from target bearing (error)
    double aError = balancedAngle(targetBearing - ra.drive.a) ;
    f.addExtendedStatus(String.format("AERROR: %5.2f ", aError));
    double dError = Math.sqrt((ra.drive.x - targetX)*(ra.drive.x-targetX) + (ra.drive.y - targetY)*(ra.drive.y-targetY));

    //fix it
    double pFwd = 0;//dError*DKp;
    double pStrafe = 0;//0.5;
    double pTurn = -aError*AKp;

    /**/
    MecanumDrive d = ra.drive;
    d.setPowerFL(pFwd + pStrafe + pTurn);
    d.setPowerFR(pFwd - pStrafe - pTurn);
    d.setPowerBL(pFwd - pStrafe + pTurn);
    d.setPowerBR(pFwd + pStrafe - pTurn);
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
    MecanumDrive d = ra.drive;

    d.setPowerFL(pFwd + pStrafe + pTurn);
    d.setPowerFR(pFwd - pStrafe - pTurn);
    d.setPowerBL(pFwd - pStrafe + pTurn);
    d.setPowerBR(pFwd + pStrafe - pTurn);
  }
}
