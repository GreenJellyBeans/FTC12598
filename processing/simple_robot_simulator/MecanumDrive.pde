//
// The MecanumDrive class implements a very simple physics-based robot simulator
// of a 4-wheel mecanum-based holonomic drive. The physics is stripped down to 4 diagonal
// forces on the 4 corners of a square robot producing linear and angular velocities.
// Composite static and dynamic linear and rotational friction, specified as constants, provide
// damping effects.
//
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
class MecanumDrive {
  final RobotProperties props;
  final Point[] boundaryPoints; // the 4 corners

  // These adjust and set the direction for each wheel
  // Typically they are set to +1 or -1, but they could be
  // individually tweaked to generate various unbalanced conditions.
  final double powerAdjustFL = 1.0;
  final double powerAdjustFR = 1.0;
  final double powerAdjustBL = 1.0;
  final double powerAdjustBR = 1.0;

  final Field field;
  // Current position and orientation
  double x;
  double y;
  double a; // in radians
  double cos_a; // cos(a)
  double sin_a; // sin(a)

  double vx = 0; // velocity in x-direction, m/s
  double vy = 0; // velocity in y-direction  m/s
  double va = 0; // angular velocity along z-axis, rad/s

  // Motor power
  // Forces act in a "diamond" pattern for mecanum:
  //    /\
  //    \/
  double pFL = 0;
  double pFR = 0;
  double pBL = 0;
  double pBR = 0;

  double markX = 0;
  double markY = 0;

  // Shows where we've been
  Trail trail;


  // Create a robot at the specified position
  public MecanumDrive(Field field, RobotProperties props, color trailColor) {
    this.field = field;
    this.props = props;
    this.trail = new Trail(field, trailColor);
    boundaryPoints = new Point[]{
      new Point(), 
      new Point(), 
      new Point(), 
      new Point()
    };

    // Initial position and orientation - can be changed
    // by using place().
    place(field.BREADTH/2, field.DEPTH/2, 0);
  }


  void setPowerFL(double p) {
    pFL = clipPower(p) * powerAdjustFL;
  }


  void setPowerFR(double p) {
    pFR = clipPower(p)  * powerAdjustFR;
  }


  void setPowerBL(double p) {
    pBL = clipPower(p) * powerAdjustBL;
  }


  void setPowerBR(double p) {
    pBR = clipPower(p) * powerAdjustBR;
  }

  // Places the robot at the specified location and orientation.
  // Units are meters and radians.
  // This is typically used once - to initially position the robot
  // somewhere on the field.
  public void place(double x, double y, double a) {
    this.x = x;
    this.y = y;
    this.a = a;
    this.cos_a = Math.cos(a);
    this.sin_a = Math.sin(a);
    updateBoundaryPoints();
  }

  void stop() {
    setPowerFL(0);
    setPowerFR(0);
    setPowerBL(0);
    setPowerBR(0);
  }


  // Updates the simulation,
  // assuming the absoute time is {t} seconds, and {dT} seconds have elapsed
  // since previous call
  void simloop(double t, double dT) {

    // Calculate motive linear forces. These are do not include friction - just motor power,
    // and are in the robot's frame of reference, not the field's frame of reference.
    double rightForce = rightMotiveForce();
    double frontForce = frontMotiveForce();

    // Convert forces to the field's frame of reference...
    // Note: Robot is pointing in the direction of {a}. 
    double motiveFx = frontForce*cos_a - rightForce*sin_a;
    double motiveFy = frontForce*sin_a + rightForce*cos_a;

    // Apply dampening effects -  acts in a direction opposite to the current direction of travel of the robot
    // This includes the extra load produced by non-powered motors. This is a big simplification, because non-powered
    // motors and friction in general will be mostly along the the same direction as the raw forces because of the 
    // free-wheeling mecanum wheel segments.
    double dampForce  = maxDampingForce();
    double hyp = Math.sqrt(vx*vx + vy*vy);
    double dampFx, dampFy;
    if (!props.noSpeed(hyp)) {
      // Nonzero current velocity
      dampFx = Math.abs((vx/hyp)*dampForce);
      dampFy = Math.abs((vy/hyp)*dampForce);
    } else {
      // Current velocity is zero
      double hyp2 = Math.sqrt(motiveFx*motiveFx + motiveFy*motiveFy);
      if (props.noForce(hyp2)) {
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

    // Calculate collision force and torque;
    double collisionFx = 0;
    double collisionFy = 0;
    double collisionTorque = 0;
    CollisionResult col = calculateCollisionImpact(this);
    if (col != null) {
      field.addExtendedStatus(String.format("COLLISION fx=%5.2f fy=%5.2f torque=%5.2f", col.fx, col.fy, col.torque));
      collisionFx = col.fx;
      collisionFy = col.fy;
      collisionTorque = col.torque;
    }

    // Calculated updated velocities - we assume, for simplicity,
    // constant force and torque for the whole previous period of duration dT.
    // We could assume a ramped force, but that would change the equations by adding
    // nonlinear elements that would tend to 0 as dT goes to 0, so we assume dT is sufficiently
    // small to make the simulation valid enough.
    double vxNew = newLinearSpeed(vx, motiveFx+collisionFx, dampFx, dT);
    double vyNew = newLinearSpeed(vy, motiveFy+collisionFy, dampFy, dT);
    double vaNew = newAngularSpeed(va, motiveTorque+collisionTorque, dampTorque, dT);

    // Compute displacements, asumming linear change in between simulation steps (which 
    // follows from the assumption of constant forces and torques during this period).
    double oldX = x;
    double oldY = y;
    x += dT * (vx + vxNew)/2;
    y += dT * (vy + vyNew)/2;
    a = normalizeAngle(a + dT * (va + vaNew)/2);
    this.cos_a = Math.cos(a);
    this.sin_a = Math.sin(a);
    updateBoundaryPoints();

    // Add to the trail - though this may not
    // be added if it is too close to the previously
    // added point.
    trail.addPoint(x, y);

    // Update velocities
    vx = vxNew;
    vy = vyNew;
    va = vaNew;

    // Update status
    field.addExtendedStatus(String.format("POS     x:%1.2f  y:%1.2f  a:%1.2f", x, y, balancedAngle(a)));
    field.addExtendedStatus(String.format("POWER   PFL:%5.2f  PFR:%5.2f  PBL:%5.2f   PBR:%5.2f", pFL, pFR, pBL, pBR));
    field.addExtendedStatus(String.format("MOTIVE  MFx:%5.2f  MFy:%5.2f  MT:%5.2f", motiveFx, motiveFy, motiveTorque));
    field.addExtendedStatus(String.format("DAMPEN  DFx:%5.2f  DFy:%5.2f  DT:%5.2f", dampFx, dampFy, dampTorque));
    field.addExtendedStatus(String.format("SPEED   Vx:%5.2f   Vy:%5.2f   w:%5.2f", vxNew, vyNew, vaNew));
  }

  // Convert robot coordinate to field coordinate - x component
  double fieldX(double rx, double ry) {
    return x + rx*cos_a - ry*sin_a;
  }

  // Convert robot coordinate to field coordinate - x component
  double fieldY(double rx, double ry) {
    return y + rx*sin_a + ry*cos_a;
  }




  // Clips to lie within [-1,1]
  private double clipPower(double in) {
    return Math.min(Math.max(in, -1), 1);
  }


  // Calculates the motive force in N of a single motor, given input unitless power, that ranges
  // within [-1, 1]. This model is based on the description in http://lancet.mit.edu/motors/motors3.html#tscurve.
  // For any particular input power, the relationship between torque and RPM is a line with -ve slope. The y-intercept
  // is the stall torque and the x-intercept is the max RPM, aka no-load RPM. The way this method uses power is in
  // defining the line itself - so it defines a family of lines, or rather a continum of lines, one line for each
  // value of power. The line furthest from the original is the torque-RPM line described above. The remaining are
  // simply the line multiplied by {power}:
  // |\
  // |\\
  // |\\\
  // |\\\\
  // |---------- > RPM
  //
  // We can at best be very approximate here, because
  // to be more accurate one would have to estimate the RPM of each motor, which would depend on
  // the speed of turning of each individual wheel, which we do not track. So we're taking a ham-handed approach
  // of both estimating the velocity of a wheel (all wheels the same) and of torque-RPM.
  double motiveForce(double power) {
    double dist = props.side * props.FORCE_FRAC; // dist from center to wheel in m
    double vLin = Math.sqrt(vx*vx + vy*vy); // magnitude of linear velocity
    double vTot = vLin + Math.abs(va * dist); // adding contriutions of angular velocity about center (see "ham handed" comment above)
    double force = 0;
    double absPower = Math.abs(power);
    double maxForce = absPower * props.MAX_MOTIVE_FORCE_PER_WHEEL;
    double maxSpeed = absPower * props.MAX_VIRTUAL_SPEED;
    if (props.noRotation(vTot)) {
      force = maxForce; // Equivalent of stall torque - this force is acting diagonally because of mecanum wheels.
    } else if (vTot > maxSpeed) {
      force = 0; // "No load speed" achieved - torque goes to zero
    } else {
      // The relationship is a line with -ve slope - see, for example,
      // http://lancet.mit.edu/motors/motors3.html#tscurve
      force = maxForce * (1 - vTot / maxSpeed);
    }
    return force * Math.signum(power);
  }


  // Remember the direction of forces on the wheels:
  //    /\
  //    \/


  // Motive force along the *robot's* x-axis (side-to-side), NOT including friction effects
  private  double rightMotiveForce() { 
    return props.FORCE_FRAC*(motiveForce(pFL) - motiveForce(pFR) - motiveForce(pBL) + motiveForce(pBR));
  }


  // Motive force along the *robot's* y-axis (front-to-back), NOT including friction effects
  private  double frontMotiveForce() {
    return props.FORCE_FRAC*(motiveForce(pFL) + motiveForce(pFR) + motiveForce(pBL) + motiveForce(pBR));
  }


  private  double motiveTorque() {
    // We assume points of contact are on the 4 corners of the square of size {this.side},
    // AND that the center of rotation is the geometric center, by symmetry.
    // So the distance of each point of contact from the center is {this.side} * (1/2) * 1/cos(45 deg) = (1/2)*sqrt(2) =cos(45 deg) 
    // which conveniently happens to be {this.side} * FORCE_FRAC
    // We assume +ve torque will make the robot rotate counterclockwise. 
    double dist = props.side * props.FORCE_FRAC;
    return dist * (-motiveForce(pFL) + motiveForce(pFR) - motiveForce(pBL) + motiveForce(pBR));
  }


  // Max resistive force - a combination of friction and resistive effects of any powered-down motors
  // Return value is positive.
  double maxDampingForce() {
    return props.weight*(props.isMoving(vx, vy) ? props.dynamicLinFriction : props.staticLinFriction) + motorDragForce();
  }


  double maxDampingTorque() {
    double dist = props.side * props.FORCE_FRAC; // distance from center to each wheel (see motiveTorque comments)
    return props.noRotation(va) ? 0 : dist*maxDampingForce()*props.dampingTorqueAdjustment;
  }


  // Resistive force in N produced by any motors that are powered off
  private double motorDragForce() {
    int numPoweredOff = 0;
    if (props.noPower(pFL)) numPoweredOff++;
    if (props.noPower(pFR)) numPoweredOff++;
    if (props.noPower(pBL)) numPoweredOff++;
    if (props.noPower(pBR)) numPoweredOff++;
    return numPoweredOff*props.MOTOR_DRAG_FORCE;
  } 


  // Returns the updated linear OR angular speed, subject to both motive force/torque and dampening force/torque,
  // assuming these forces act constantly over a period of {dT}.
  double newLinearSpeed(double curSpeed, double motiveForce, double maxDampForce, double dT) {
    assert(maxDampForce) >= 0;
    double newSpeed;

    if (props.noSpeed(curSpeed)) {
      if (Math.abs(motiveForce) < maxDampForce) {
        // Not enough motive force to produce motion
        newSpeed = 0;
      } else {
        double netForce = motiveForce - Math.signum(motiveForce)*maxDampForce;
        // We expect the net force to be in the direction of the motive force
        assert(Math.signum(netForce) == Math.signum(motiveForce));
        newSpeed = curSpeed + dT * netForce / props.mass;
      }
    } else {
      // Nonzero current speed - dampening force is in the direction opposing the current direction
      assert(props.linearDirection(curSpeed) != 0); // Because speed is not zero
      double netForce = motiveForce - Math.signum(props.linearDirection(curSpeed))*maxDampForce;
      newSpeed = curSpeed + dT * netForce / props.mass;
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

    if (props.noRotation(curSpeed)) {
      if (Math.abs(motiveTorque) < maxDampTorque) {
        // Not enough motive force to produce motion
        newSpeed = 0;
      } else {
        double netTorque = motiveTorque - Math.signum(motiveTorque)*maxDampTorque;
        // We expect the net force to be in the direction of the motive force
        assert(Math.signum(netTorque) == Math.signum(motiveTorque));
        newSpeed = curSpeed + dT * netTorque / props.rotInertia;
      }
    } else {
      // Nonzero current speed - dampening force is in the direction opposing the current direction
      assert(props.angularDirection(curSpeed) != 0); // Because speed is not zero
      double netTorque = motiveTorque - Math.signum(props.linearDirection(curSpeed))*maxDampTorque;
      newSpeed = curSpeed + dT * netTorque / props.rotInertia;
      if (Math.signum(curSpeed) != Math.signum(newSpeed)) {
        // We don't allow zero-crossings, because it involves incorrect application of damping force - when the change in
        // direction is only because of the damping force.
        // So we set current speed to 0. The next simulation cycle will adjust the speed.
        newSpeed = 0;
      }
    }
    return newSpeed;
  }



  // The boundaryPoints array keeps track of the locations of
  // the corners of the robot, in field coordinates; these change as
  // the robot moves, so need to be updated constantly.
  void updateBoundaryPoints() {
    double d = props.side/2;
    // This generates four combinations of {-d, d} X {-d, d}, which 
    // are the corners in robot-coordinates; those then have to be tranformed
    // to field coordinates
    int i = 0;
    for (int ii = -1; ii <= 1; ii+= 2) {
      double x0  = d*ii;
      for (int jj = -1; jj <= 1; jj+= 2) {
        double y0  = d*jj;
        Point p  = boundaryPoints[i];
        p.set(fieldX(x0, y0), fieldY(x0, y0));
        // uncomment to verify we got the corners right...
        // fill(0); field.drawCircle(p.x, p.y, 0.05);
        i++;
      }
    }
    assert(i == 4);
  };
}
