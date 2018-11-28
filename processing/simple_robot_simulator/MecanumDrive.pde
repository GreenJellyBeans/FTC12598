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
  final DriveBase base;
  final RobotProperties props;
  final Field field;
  
  // This is a random number that varies from robot instance to instance.
  // It is used as the base to compute perlin noise.
  final float perturbationNoiseStart = random(10000);



  // Create a robot at the specified position
  public MecanumDrive(DriveBase base) {
    this.base = base;
    this.field = base.field;
    this.props = base.props;
  }


  void stop() {
    base.setMotorPowerAll(0, 0, 0, 0);
  }


  // Updates the simulation,
  // assuming the absoute time is {t} seconds, and {dT} seconds have elapsed
  // since previous call
  void simloop(double t, double dT) {

    // Calculate motive linear forces. These are do not include friction - just motor power,
    // and are in the robot's frame of reference, not the field's frame of reference.
    double leftForce = leftMotiveForce();
    double frontForce = frontMotiveForce();

    // Convert forces to the field's frame of reference...
    // Note: Robot is pointing in the direction of {a}. 
    //
    //                 ^ Robot's y-axis
    //      robot      |
    //    ...... FL    |
    //    .    .       --> Robot's x-axis
    //    ...... FR

    double motiveFx = frontForce*base.cos_a - leftForce*base.sin_a;
    double motiveFy = frontForce*base.sin_a + leftForce*base.cos_a;
    motiveFx = perturbForceX(motiveFx, motiveFy, t);
    motiveFy = perturbForceY(motiveFx, motiveFy, t);


    // Apply dampening effects -  acts in a direction opposite to the current direction of travel of the robot
    // This includes the extra load produced by non-powered motors. This is a big simplification, because non-powered
    // motors and friction in general will be mostly along the the same direction as the raw forces because of the 
    // free-wheeling mecanum wheel segments.
    double dampForce  = maxDampingForce();
    double vx = base.vx;
    double vy = base.vy;
    double va = base.va;
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
    motiveTorque = perturbTorque(motiveFx, motiveFy, motiveTorque, t);
    double dampTorque = maxDampingTorque();

    // Calculate collision force and torque;
    double collisionFx = 0;
    double collisionFy = 0;
    double collisionTorque = 0;
    CollisionResult col = calculateCollisionImpact(base, true); // first: robot corners with outside walls
    if (col == null) {
      col = calculateCollisionImpact(base, false); // next to try: robot sides with external corners
    }
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
    double dx = dT * (vx + vxNew)/2;
    double dy = dT * (vy + vyNew)/2;
    double da = dT * (va + vaNew)/2;
    base.updatePositionIncrements(dx, dy, da);

    // Update velocities
    base.updateVelocities(vxNew, vyNew, vaNew);

    // Update status
    base.addExtendedStatus();
    field.addExtendedStatus(String.format("MOTIVE  MFx:%5.2f  MFy:%5.2f  MT:%5.2f", motiveFx, motiveFy, motiveTorque));
    field.addExtendedStatus(String.format("DAMPEN  DFx:%5.2f  DFy:%5.2f  DT:%5.2f", dampFx, dampFy, dampTorque));
  }


  private double pFL() {
    return base.power[base.FL];
  }


  private double pFR() {
    return base.power[base.FR];
  }


  private double pBL() {
    return base.power[base.BL];
  }


  private double pBR() {
    return base.power[base.BR];
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
    double vLin = Math.sqrt(base.vx*base.vx + base.vy*base.vy); // magnitude of linear velocity
    double vTot = vLin + Math.abs(base.va * dist); // adding contriutions of angular velocity about center (see "ham handed" comment above)
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


  // Motive force along the *robot's* y-axis (side-to-side), NOT including friction effects
  private  double leftMotiveForce() { 
    return props.FORCE_FRAC*(motiveForce(pFR()) - motiveForce(pFL()) - motiveForce(pBR()) + motiveForce(pBL()));
  }


  // Motive force along the *robot's* x-axis (front-to-back), NOT including friction effects
  private  double frontMotiveForce() {
    return props.FORCE_FRAC*(motiveForce(pFL()) + motiveForce(pFR()) + motiveForce(pBL()) + motiveForce(pBR()));
  }


  private  double motiveTorque() {
    // We assume points of contact are on the 4 corners of the square of size {this.side},
    // AND that the center of rotation is the geometric center, by symmetry.
    // So the distance of each point of contact from the center is {this.side} * (1/2) * 1/cos(45 deg) = (1/2)*sqrt(2) =cos(45 deg) 
    // which conveniently happens to be {this.side} * FORCE_FRAC
    // We assume +ve torque will make the robot rotate counterclockwise. 
    double dist = props.side * props.FORCE_FRAC;
    return dist * (-motiveForce(pFL()) + motiveForce(pFR()) - motiveForce(pBL()) + motiveForce(pBR()));
  }


  // Max resistive force - a combination of friction and resistive effects of any powered-down motors
  // Return value is positive.
  double maxDampingForce() {
    return props.weight*(props.isMoving(base.vx, base.vy) ? props.dynamicLinFriction : props.staticLinFriction) + motorDragForce();
  }


  double maxDampingTorque() {
    double dist = props.side * props.FORCE_FRAC; // distance from center to each wheel (see motiveTorque comments)
    return props.noRotation(base.va) ? 0 : dist*maxDampingForce()*props.dampingTorqueAdjustment;
  }


  // Resistive force in N produced by any motors that are powered off
  private double motorDragForce() {
    int numPoweredOff = 0;
    if (props.noPower(pFL())) numPoweredOff++;
    if (props.noPower(pFR())) numPoweredOff++;
    if (props.noPower(pBL())) numPoweredOff++;
    if (props.noPower(pBR())) numPoweredOff++;
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

  //
  // Force and torque perturbations use noise parameters
  // RobotProperties to randomly perturb the forces and toreques
  // used to calculate robot motion. These make use of "Perlin noise"
  // built into Processing. Perlin Noise is a kind of multi-spectral
  // noise as opposed to white noise that is not correlated from sample
  // to sample. The amplitude and scale and other parameters are controlled
  // by constants in RobotProperties.
  //


  // Net force perturbation on the robot. {fx} is the
  // "pure" force in the x-direction in N, {t} is the current time.
  double perturbForceX(double fx, double fy, double t)
  {
    double mag = Math.sqrt(2*fx*fx + fy*fy);
    float C1 = perturbationNoiseStart;
    float C2 = perturbationNoiseStart + 100;
    float t1 = (float) (C1 + t * props.PERTURBATION_FORCE_SCALE);
    float t2 = (float) (C2 + t * props.PERTURBATION_FORCE_SCALE);
    return props.PERTURBATION_FORCE_A*(noise(t1)-0.5) + fx + mag * props.PERTURBATION_FORCE_B*(noise(t2)-0.5);
  }


  // Net force perturbation on the robot. {fy} is the
  // "pure" force in the y-direction in N, {t} is the current time.
  double perturbForceY(double fx, double fy, double t)
  {
    double mag = Math.sqrt(fx*fx + 2*fy*fy);
    float C1 = perturbationNoiseStart + 200;
    float C2 = perturbationNoiseStart + 300;
    float t1 = (float) (C1 + t * props.PERTURBATION_FORCE_SCALE);
    float t2 = (float) (C2 + t * props.PERTURBATION_FORCE_SCALE);
    return props.PERTURBATION_FORCE_A*(noise(t1)-0.5) + fy + mag * props.PERTURBATION_FORCE_B*(noise(t2)-0.5);
  }

  // Net force perturbation on the robot. {torque} is the
  // "pure" torque in N-m, {t} is the current time.
  double perturbTorque(double fx, double fy, double torque, double t) {
    double fmag = props.side*Math.sqrt(fx*fx + fy*fy);
    double mag = Math.sqrt(fmag*fmag + torque*torque);
    float C1 = perturbationNoiseStart + 400;
    float C2 = perturbationNoiseStart + 500;
    float t1 = (float) (C1 + t * props.PERTURBATION_TORQUE_SCALE);
    float t2 = (float) (C2 + t * props.PERTURBATION_TORQUE_SCALE);
    // The "constant" part not really constant - it is proportional to the force magnitude.
    return fmag*props.PERTURBATION_TORQUE_A*(noise(t1)-0.5) + torque + mag * props.PERTURBATION_TORQUE_B*(noise(t2)-0.5);
  }
}
