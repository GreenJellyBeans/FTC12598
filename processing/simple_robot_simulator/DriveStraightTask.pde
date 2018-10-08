class DriveStraightTask implements DriveTask {

  double targetBearing = radians(180);
  Robot ra;
  final double Kp = 0.0001;

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

    //find difference from target bearing (error)
    double error = balancedAngle(targetBearing - ra.drive.a) ;
    f.addExtendedStatus(String.format("ERROR: %5.2f ", error));

    //fix it
    double pFwd = 0;//0.05;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = -error*Kp;

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
