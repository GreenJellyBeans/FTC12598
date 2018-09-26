// //<>//
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
  final double dampingTorqueAdjustment = 1; // Factor applied when converting max damping force to torque
  final double side = 17*0.0254; // side of the square robot, m. (17 inches).
  final double P_TO_F_SCALE = 0.5; // Unitless motor power (which is within [-1, 1]) to force (N) conversion
  final double FORCE_FRAC = 1/Math.sqrt(2); // Fraction of wheel force in the x (or y) direction - assuming square positioning.
  // Cos(Pi/4) == Sin(Pi/4) (or Cos(45 degrees))
  final double LIN_SPEED_ZERO = 0.001; // In m/s. Less than 1mm per second is considered NOT moving
  final double ANGULAR_SPEED_ZERO = Math.PI/180; // In rad/sec. Less than 1 degree of rotation per second is considered NOT rotating.
  final double FORCE_MAG_ZERO = weight/1000; // Forces < 0.1% of the weight of the robot are considered effectively no force 
  final double MOTOR_DRAG_FORCE = 12; // N. A simple approximation of the impact of the drag of an unpowered motor.

  // In reality this depends on orientation of the wheels, but we just assume it is
  // isotropic against the prevailing direction of motion of the robot.
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

    // Calculate motive linear forces. These are do not include friction - just motor power,
    // and are in the robot's frame of reference, not the field's frame of reference.
    double rightForce = rightMotiveForce();
    double frontForce = frontMotiveForce();

    // Convert forces to the field's frame of reference...
    double motiveFx = rightForce*Math.cos(a) - frontForce*Math.sin(a);
    double motiveFy = rightForce*Math.sin(a) + frontForce*Math.cos(a);

    // Apply dampening effects -  acts in a direction opposite to the current direction of travel of the robot
    // This includes the extra load produced by non-powered motors. This is a big simplification, because non-powered
    // motors and friction in general will be mostly along the the same direction as the raw forces because of the 
    // free-wheeling meccanum wheel segments.
    double dampForce  = maxDampingForce();
    double hyp = Math.sqrt(vx*vx + vy*vy);
    double dampFx, dampFy;
    if (!noSpeed(hyp)) {
      // Nonzero current velocity
      dampFx = Math.abs((vx/hyp)*dampForce);
      dampFy = Math.abs((vy/hyp)*dampForce);
    } else {
      // Current velocity is zero
      double hyp2 = Math.sqrt(motiveFx*motiveFx + motiveFy*motiveFy);
      if (noForce(hyp2)) {
        // Current velocity is zero, and no motive force. Let's
        // set everything to 0 - the velocity will thus remain 0.
        dampFx = motiveFx = dampFy = motiveFy = 0;
      } else {
        // Current velocity is zero, and non-zero motive force. We
        // set the dampening force direction to be opposite to the motive
        // force direction.
        dampFx = Math.abs((motiveFx/hyp2)*dampForce);
        dampFy = Math.abs((motiveFx/hyp2)*dampForce);
      }
    }

    double motiveTorque = motiveTorque();
    double dampTorque = maxDampingTorque();


    // Calculated updated velocities - we assume, for simplicity,
    // constant force and torque for the whole previous period of duration dT.
    // We could assume a ramped force, but that would change the equations by adding
    // nonlinear elements that would tend to 0 as dT goes to 0, so we assume dT is sufficiently
    // small to make the simulation valid enough.
    double vxNew = newLinearSpeed(vx, motiveFx, dampFx, dT*mass);
    double vyNew = newLinearSpeed(vy, motiveFy, dampFy, dT*mass);
    double vaNew = newAngularSpeed(va, motiveTorque, dampTorque, dT*rotInertia);

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

    if (x < 0 || x > FIELD_WIDTH || y < 0 || y > FIELD_WIDTH) {
      fill(255, 0, 0);
      noLoop();
    }
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

  // Motive force along the *robot's* x-axis (side-to-side), NOT including friction effects
  private  double rightMotiveForce() { 
    return FORCE_FRAC*(fMagFL - fMagFR - fMagBL + fMagBR);
  }

  // Motive force along the *robot's* y-axis (front-to-back), NOT including friction effects
  private  double frontMotiveForce() {
    return FORCE_FRAC*(fMagFL + fMagFR + fMagBL + fMagBR);
  }

  private  double motiveTorque() {
    // We assume points of contact are on the 4 corners of the square of size {this.side},
    // AND that the center of rotation is the geometric center, by symmetry.
    // So the distance of each point of contact from the center is {this.side} * (1/2) * 1/cos(45 deg) = (1/2)*sqrt(2) =cos(45 deg) 
    // which conveniently happens to be {this.side} * FORCE_FRAC
    // We assume +ve torque will make the robot rotate counterclockwise. 
    double dist = this.side * FORCE_FRAC;
    return dist * (-fMagFL + fMagFR - fMagBL + fMagBR);
  }

  // Max resistive force - a combination of friction and resistive effects of any powered-down motors
  // Return value is positive.
  double maxDampingForce() {
    return weight*(isMoving() ? dynamicLinFriction : staticLinFriction) + motorDragForce();
  }

  double maxDampingTorque() {
    double dist = this.side * FORCE_FRAC; // distance from center to each wheel (see motiveTorque comments)
    return dist*maxDampingForce()*dampingTorqueAdjustment;
  }

  // Resistive force in N produced by any motors that are powered off
  private double motorDragForce() {
    int numPoweredOff = 0;
    if (noPower(pFL)) numPoweredOff++;
    if (noPower(pFR)) numPoweredOff++;
    if (noPower(pBL)) numPoweredOff++;
    if (noPower(pBR)) numPoweredOff++;
    return numPoweredOff*MOTOR_DRAG_FORCE;
  } 

  // Returns the updated linear OR angular speed, subject to both motive force/torque and dampening force/torque,
  // assuming these forces act constantly over a period of {dT}.
  double newLinearSpeed(double curSpeed, double motiveForce, double maxDampForce, double dT) {
    assert(maxDampForce) >= 0;
    double newSpeed;

    if (noSpeed(curSpeed)) {
      if (Math.abs(motiveForce) < maxDampForce) {
        // Not enough motive force to produce motion
        newSpeed = 0;
      } else {
        double netForce = motiveForce - Math.signum(motiveForce)*maxDampForce;
        // We expect the net force to be in the direction of the motive force
        assert(Math.signum(netForce) == Math.signum(motiveForce));
        newSpeed = curSpeed + netForce * mass * dT;
      }
    } else {
      // Nonzero current speed - dampening force is in the direction opposing the current direction
      assert(linearDirection(curSpeed) != 0); // Because speed is not zero
      double netForce = motiveForce - Math.signum(linearDirection(curSpeed))*maxDampForce;
      newSpeed = curSpeed + netForce * mass * dT;
      if (Math.signum(curSpeed) != Math.signum(newSpeed)) {
        // We don't allow zero-crossings, because it involves incorrect application of damping force - when the change in
        // direction is only because of the damping force.
        // So we set current speed to 0. The next simulation cycle will adjust the speed.
        newSpeed = 0;
      }
    }
    return newSpeed;
  }
  
  // Returns the updated angular speed, subject to both motive torque and dampening torque,
  // assuming these  act constantly over a period of dT.
  // IMPNOTE: This method is SUBSTANTIALLY the same as newSpeed(). The only differences are
  // in converting torque/force to speed, and in checking for zero values of linear vs. angular speeds
  double newAngularSpeed(double curSpeed, double motiveTorque, double maxDampTorque, double dT) {
    assert(maxDampTorque) >= 0;
    double newSpeed;

    if (noRotation(curSpeed)) {
      if (Math.abs(motiveTorque) < maxDampTorque) {
        // Not enough motive force to produce motion
        newSpeed = 0;
      } else {
        double netTorque = motiveTorque - Math.signum(motiveTorque)*maxDampTorque;
        // We expect the net force to be in the direction of the motive force
        assert(Math.signum(netTorque) == Math.signum(motiveTorque));
        newSpeed = curSpeed + netTorque * rotInertia * dT;
      }
    } else {
      // Nonzero current speed - dampening force is in the direction opposing the current direction
      assert(angularDirection(curSpeed) != 0); // Because speed is not zero
      double netTorque = motiveTorque - Math.signum(linearDirection(curSpeed))*maxDampTorque;
      newSpeed = curSpeed + netTorque * rotInertia * dT;
      if (Math.signum(curSpeed) != Math.signum(newSpeed)) {
        // We don't allow zero-crossings, because it involves incorrect application of damping force - when the change in
        // direction is only because of the damping force.
        // So we set current speed to 0. The next simulation cycle will adjust the speed.
        newSpeed = 0;
      }
    }
    return newSpeed;
  }


  private boolean isMoving() {
    return Math.max(Math.abs(vx), Math.abs(vy)) > LIN_SPEED_ZERO;
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

  // Effectively no power  - this is the unitless power used to control the motor (setPower)
  private boolean noPower(double p) {
    return Math.abs(p) < 0.1;
  }

  // Effectively no speed  - {s} in m/sec
  private boolean noSpeed(double s) {
    return Math.abs(s) < LIN_SPEED_ZERO; // 0.1% of the weight of the robot - somewhat arbitrary
  }

  // Effectively no angular velocity  - {a} in rad / sec
  private boolean noRotation(double a) {
    return Math.abs(a) < ANGULAR_SPEED_ZERO; // 0.1% of the weight of the robot - somewhat arbitrary
  }

  // Effectively no force  - {f} in N
  private boolean noForce(double f) {
    return Math.abs(f) < FORCE_MAG_ZERO; // 0.1% of the weight of the robot - somewhat arbitrary
  }
}
