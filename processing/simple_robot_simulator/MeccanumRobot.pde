//
// The MeccanumRobot class implements a very simple physics-based robot simulator
// of a 4-wheel meccanum-based holonomic drive. The physics is stripped down to 4 diagonal
// forces on the 4 corners of a square robot producing linear and angular velocities.
// Composite static and dynamic linear and rotational friction, specified as constants, provide
// damping effects.
//
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
class MeccanumRobot {
  // Only metric units allowed.
  //
  final double mass = 42/2.2; // In kg
  final double weight = mass * 9.8; // In N.
  final double rotInertia = 3; // Rotational inertia in kg-m^2
  final double staticLinFriction = 0.001; // Coef. of static friction - unitless
  final double dynamicLinFriction = 0.001; // Coef. of dynamic friction - unitless
  final double staticRotFriction = 0.001;
  final double dynamicRotFriction = 0.001;
  final double side = 0.5; // side of the square robot, m.
  final double P_TO_F_SCALE = 0.5; // Unitless motor power (which is within [-1, 1]) to force (N) conversion
  final double FORCE_FRAC = 1/Math.sqrt(2); // Fraction of wheel force in the x (or y) direction - assuming square positioning.
  // Cos(Pi/4) == Sin(Pi/4) (or Cos(45 degrees))
  final double LIN_SPEED_ZERO = 0.001; // In m/s. Less than 1mm per second is considered NOT moving
  final double ANGULAR_SPEED_ZERO = Math.PI/180; // In rad/sec. Less than 1 degree of rotation per second is considered NOT rotating.
 
  // These adjust and set the direction for each wheel
  // Typically they are set to +1 or -1, but they could be
  // individually tweaked to generate various unbalanced conditions.
  final double powerAdjustFL = 1.0;
  final double powerAdjustFR = 1.0;
  final double powerAdjustBL = 1.0;
  final double powerAdjustBR = 1.0;

  // Current position and orientation
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

  // Force magnitudes, acting in a "diamond" pattern for meccanum:
  //    /\
  //    \/
  // These are re-calculated each time a motor's power is changed via setPowerXX.
  private double fMagFL = 0;
  private double fMagFR = 0;
  private double fMagBL = 0;
  private double fMagBR = 0;

  // Create a robot at the specified position
  public MeccanumRobot(double x, double y, double a) {
    this.x = x;
    this.y = y;
    this.a = a;
  }

  void setPowerFL(double p) {
    pFL = clipPower(p);
    fMagFL = pFL * P_TO_F_SCALE * powerAdjustFL;
  }

  void setPowerFR(double p) {
    pFR = clipPower(p);
    fMagFR = pFR * P_TO_F_SCALE * powerAdjustFR;
  }

  void setPowerBL(double p) {
    pBL = clipPower(p);
    fMagBL = pBL * P_TO_F_SCALE * powerAdjustBL;
  }

  void setPowerBR(double p) {
    pBR = clipPower(p);
    fMagBR = pBR * P_TO_F_SCALE * powerAdjustFR;
  }

  void stop() {
    setPowerFL(0);
    setPowerFR(0);
    setPowerBL(0);
    setPowerBR(0);
  }

  // Updates the simulation,
  // assumed to be {dT} seconds have elapsed
  // since previous call
  void simloop(double dT) {
    // Calculate linear force and torque;
    // The "r" and "R" prefixes indicate that these forces are with respect
    // to the robot's own frame of reference!
    double rfx = calcRFx();
    double rfy = calcRFy();
    double torque = calcTorque();
    
    // Convert forces to the field's frame of reference...
    double fx = rfx*Math.cos(a) - rfy*Math.sin(a);
    double fy = rfx*Math.sin(a) + rfy*Math.cos(a);
    
    // Calculated updated velocities - we assume, for simplicity,
    // constant force and torque for the whole previous period of duration dT.
    // We could assume a ramped force, but that would change the equations by adding
    // nonlinear elements that would tend to 0 as dT goes to 0, so we assume dT is sufficiently
    // small to make the simulation valid enough.
    double vxNew = vx + (fx/mass)*dT;
    double vyNew = vy + (fy/mass)*dT;
    double vaNew = va + (torque/rotInertia)*dT;

    // Compute displacements, asumming linear change in between simulation steps (which 
    // follows from the assumption of constant forces and torques during this period).
    x += (vx + vxNew)/2;
    y += (vy + vyNew)/2;
    a += (va + vaNew)/2;   

    // Update velocities
    vx = vxNew;
    vy = vyNew;
    va = vaNew;
  }

  void draw() {
    
    double x_to_px = (float) (width / FIELD_WIDTH);
    float pixSide = (float) (side * x_to_px);
    float px = (float) (x * x_to_px);
    float py = (float) (height - y * x_to_px); // y grows upwards, py grows downwards
    pushMatrix();
    translate(px, py);
    rotate((float) a);
    rect(0, 0, pixSide, pixSide);  
    popMatrix();
  }

  // Clips to lie within [-1,1]
  private double clipPower(double in) {
    return Math.min(Math.max(in, -1), 1);
  }
  

  // Remember the direction of forces on the wheels:
  //    /\
  //    \/

  // Net force along the *robot's* x-axis (side-to-side), including friction effects
  private  double calcRFx() { 
    double rawForce =  FORCE_FRAC*(fMagFL - fMagFR - fMagBL + fMagBR);
    return calcNetForce(rawForce, weight*staticLinFriction, weight*dynamicLinFriction, linearDirection(vx));
  }

  // Net force along the *robot's* y-axis (front-to-back), including friction effects
  private  double calcRFy() {
    double rawForce =  FORCE_FRAC*(fMagFL + fMagFR + fMagBL + fMagBR);
    return calcNetForce(rawForce, weight*staticLinFriction, weight*dynamicLinFriction, linearDirection(vy));
  }

  private  double calcTorque() {
    // We assume points of contact are on the 4 corners of the square of size {this.side},
    // AND that the center of rotation is the geometric center, by symmetry.
    // So the distance of each point of contact from the center is {this.side} * (1/2) * 1/cos(45 deg) = (1/2)*sqrt(2) =cos(45 deg) 
    // which conveniently happens to be {this.side} * FORCE_FRAC
    // We assume +ve torque will make the robot rotate counterclockwise. 
    double dist = this.side * FORCE_FRAC;
    double rawTorque = dist * (-fMagFL + fMagFR - fMagBL + fMagBR);
    
    // TODO: for now we generate 0 torque - while we debug
    return calcNetForce(rawTorque, dist*staticRotFriction, dist*dynamicRotFriction, angularDirection(va));

  }

  private boolean isMoving() {
    return Math.max(Math.abs(vx), Math.abs(vy)) > LIN_SPEED_ZERO;
  }

  // {direction} is present direction of motion - +ve = forwards; -ve == backwards; 0 == no motion.
  // This could also apply to torque - so units of force are unspecified
  private double calcNetForce(double rawForce, double staticFrictionForce, double dynamicFrictionForce, int direction) {
    // Friction always acts against the direction of motion. If direction happens to be 0, then friction
    // acts against raw force.
    double fricDir = (direction == 0) ? -1 * Math.signum(rawForce) : -1 * Math.signum(direction);
    double frictionForce = fricDir * (isMoving() ? dynamicFrictionForce : staticFrictionForce);

    double netForce = frictionForce + rawForce;

    // Friction force can never exceed raw force if they in opposite directions - else
    // it would produce movement in the opposite direction to the net force!
    if (Math.abs(frictionForce) > Math.abs(rawForce)) {
      netForce = 0;
    } 
    return netForce;
  }

  // Returns -1 | 0 | 1 depending on whether speed (in m/sec) is -ve, 0 or +ve
  //
  private int linearDirection(double speed) {
    return (speed < -LIN_SPEED_ZERO) ? -1 : ((speed > LIN_SPEED_ZERO) ? 1 : 0);
  }

  // Returns -1 | 0 | 1 depending on whether speed (in m/sec) is -ve, 0 or +ve
  //
  private int angularDirection(double omega) {
    return (omega < -ANGULAR_SPEED_ZERO) ? -1 : ((omega > ANGULAR_SPEED_ZERO) ? 1 : 0);
  }
}
