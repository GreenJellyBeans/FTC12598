class DriveStraightTask {

  double targetBearing = radians(90);
  Robot ra;
  final double Kp = 0.1;

  public DriveStraightTask(Robot r) {
    ra=r;
  }
 void init() {
    setStartingPower();
  }
  
  void loop() {
    //get current bearing   
    Field f = ra.field;
    f.addExtendedStatus("ANGLE: " + ra.drive.a);
        
    //find difference from target bearing (error)
    double error = normalizeAngle(targetBearing - ra.drive.a)  - 2*Math.PI ;
    //fix it
    double pFwd = 0.0;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = error*Kp;

    /**/
    MeccanumDrive d = ra.drive;
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
    double pFwd = 0.0;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = 0.01;
    MeccanumDrive d = ra.drive;

    d.setPowerFL(pFwd + pStrafe + pTurn);
    d.setPowerFR(pFwd - pStrafe - pTurn);
    d.setPowerBL(pFwd - pStrafe + pTurn);
    d.setPowerBR(pFwd + pStrafe - pTurn);
  }
}
