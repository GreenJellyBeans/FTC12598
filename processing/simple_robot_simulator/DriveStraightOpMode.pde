// This is work in progress, experimenting with PID algorithms
// to drive straight and at a particular bearing
class DriveStraightOpMode extends IterativeOpMode {

  double targetBearing = radians(0);
  double targetX = meters(1);
  double targetY = meters(6);
  Robot ra;
  final double AKp = 2;
  final double DKp = 1;
  public DriveStraightOpMode(Robot r) {
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
    stroke(255, 0, 0);
    strokeWeight(4);
    f.drawPoint(targetX, targetY);

    //find difference from target bearing (error)
    double aError = balancedAngle(targetBearing - ra.base.a) ;
    f.addExtendedStatus(String.format("AERROR: %5.2f ", aError));
    double dError = Math.sqrt((ra.base.cx - targetX)*(ra.base.cx-targetX) + (ra.base.cy - targetY)*(ra.base.cy-targetY));

    //fix it
    double pFwd = 0;//dError*DKp;
    double pStrafe = 0;//0.5;
    double pTurn = -aError*AKp;

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
    MecanumDrive d = ra.drive;

    double pFL = (pFwd + pStrafe + pTurn);
    double pFR = (pFwd - pStrafe - pTurn);
    double pBL = (pFwd - pStrafe + pTurn);
    double pBR = (pFwd + pStrafe - pTurn);
    ra.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
  }
}
