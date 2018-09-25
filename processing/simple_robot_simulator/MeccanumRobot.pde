class MeccanumRobot {
  final double mass = 1; // In kg
  final double rotInertia = 1; // Rotational inertia in kg-m^2
  final double staticFriction = 1; // Coef. of static friction - unitless
  final double dynamicFriction = 1; // Coef. of dynamic friction - unitless
  final int side = 20; // side of the square robot
  double x;
  double y;
  double a;
  double vx = 0; // velocity in x-direction, m/s
  double vy = 0; // velocity in y-direction  m/s
  double va = 0; // angular velocity along z-axis, rad/s

  // Motor power
  double pFL = 0;
  double pFR = 0;
  double pBL = 0;
  double pBR = 0;


  // Create a robot at the specified position
  public MeccanumRobot(double x, double y, double a) {
    this.x = x;
    this.y = y;
    this.a = a;
  }

  void setFLPower(double p) {
    pFL = clipPower(p);
  }

  void setFRPower(double p) {
    pFR = clipPower(p);
  }

  void setBLPower(double p) {
    pBL = clipPower(p);
  }

  void setBRPower(double p) {
    pBR = clipPower(p);
  }

  void stop() {
    setFLPower(0);
    setFRPower(0);
    setBLPower(0);
    setBRPower(0);
  }

  // Updates the simulation,
  // assumed to be {dT} seconds have elapsed
  // since previous call
  void simloop(double dT) {
    // Calculated updated velocities
    double vxNew = vx;
    double vyNew = vy;
    double vaNew = va;

    // Compute displacements, asumming linear change in between simulation steps
    x += (vx + vxNew)/2;
    y += (vy + vyNew)/2;
    a += (va + vaNew)/2;   

    // Update velocities
    vx = vxNew;
    vy = vyNew;
    va = vaNew;
  }

  void draw() {
    pushMatrix();
    translate((float)x, (float)y);
    rotate((float) a);
    rect(0, 0, (float)side, (float)side);  
    popMatrix();
  }

  // Clips to lie within [-1,1]
  private double clipPower(double in) {
    return Math.min(Math.max(in, -1), 1);
  }
}
