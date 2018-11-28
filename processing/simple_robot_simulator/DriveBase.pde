// Class DriveBase contains the details of the drive that are not tied to a specific drive mechanism such
// as mecanum. However this Drive assumes 4 wheels at the 1 corners of a square, and each wheel has a
// motor and encoder, and all wheels have their axels parallel to the front of the robot.
// A particular drive type (such as mecanum) may decide whether to use all 4 motors or not and
// what the force implications are of using (say) mecanum vs omni vs normal wheels.
// The class keeps track of position and orientation of the center of the drive base,
// as well as boundary point locations and robot wall (side) locations. It also keeps
// track of the forward travel by each individual wheel (or rather, corner) since the robot was initialized,
// and this latter information is used to return simulated encoder values for each wheel.
//
// This code is for basic experimentation and validation
// of robot drive control algorithms.
// Author: Joseph M. Joy, FTC12598 mentor.
//
class DriveBase {

  final int FL = 0;
  final int FR = 1;
  final int BL = 2;
  final int BR = 3;
  final int NUM_CORNERS = 4; // Really, can't be anything other than 4 without more substantial changes elsewhere
  final double DEFAULT_TICKS_PER_METER = 1000; // By default, 1 encoder "tick" is 1mm of forward travel
  final RobotProperties props;
  final Field field;
  final Point[] boundaryPoints; // the 4 corners
  final Wall[] walls; // the 4 walls of the robot

  // These adjust and set the direction for each wheel
  // Typically they are set to +1 or -1, but they could be
  // individually tweaked to generate various unbalanced conditions.
  final double[] powerAdjust = {
    1.0, // FL
    1.0, // FR
    1.0, // BL
    1.0  // BR
  };

  // Keeps track of where we've been - for visualization only
  final Trail trail;

  // Position and orientation of the center of the drive base in
  // field coordinates. In meters and radians
  double cx;
  double cy;
  double a;
  double cos_a; // cos(a)
  double sin_a; // sin(a)

  double vx = 0; // velocity in x-direction, m/s
  double vy = 0; // velocity in y-direction  m/s
  double va = 0; // angular velocity along z-axis, rad/s

  // Motor power
  // NOTE: Forces act in a "diamond" pattern for mecanum:
  //    /\
  //    \/
  double[] power = new double[NUM_CORNERS]; 

  // Distance covered by a virtual point stuck to each wheel. Units are in meters.
  // This is not the same as the distance traveled by each corner because (a)
  // The wheel is a meccanum wheel, so strafing also results in rotation and (b)
  // the wheel may slip.
  // This information is used by the simulated encoders.
  private double[] wheelTravel = new double[NUM_CORNERS];
  
  private double ticksPerMeter = DEFAULT_TICKS_PER_METER;


  public DriveBase(Field field, RobotProperties props, color trailColor) {
    this.field = field;
    this.props = props;
    this.trail = new Trail(field, trailColor);

    // Boundary points are are the position of the for corners, in field-coordinates
    boundaryPoints = new Point[]{
      new Point(), 
      new Point(), 
      new Point(), 
      new Point()
    };

    // Robot walls  - they are as wide as the robot's side, and as thick
    // as the quarter the robot's side. Thickness comes into play when the
    // robot collides with a corner that intrudes into the robot because
    // of the robot's momentum.
    walls = new Wall[] {
      new Wall(props.side, props.side/4), 
      new Wall(props.side, props.side/4), 
      new Wall(props.side, props.side/4), 
      new Wall(props.side, props.side/4)
    };

    // Initial position and orientation - can be changed
    // by a subsequent call to place().
    place(field.BREADTH/2, field.DEPTH/2, 0);
  }


  // Places the robot at the specified location and orientation.
  // Units are meters and radians.
  // This should only be called once - to initially position the robot
  // somewhere on the field. Subsequently, the position and orientation
  // should be left to the physics simulation.
  public void place(double x, double y, double a) {
    this.cx = x;
    this.cy = y;
    this.a = a;
    updatePositionIncrements(0, 0, 0); // This updates various other internal variables
  }

  // Set the current encoder tick value for all encoders to 0.
  void resetEncoders() {
    // Clear accumulated wheel travel on all motors.
    for (int i = 0; i < wheelTravel.length; i++) {
      wheelTravel[i] = 0;
    }
  }


  // Read current encoder value for a particular
  // motor / corner, scaled by the value earlier
  // set in a call to setEncoderScale (or a default).
  // Note that it could return fractional "tick" values.
  double readEncoder(int index) {
    return wheelTravel[index] * ticksPerMeter;
  }


  // Sets the unit of an encoder "tick".
  void setEncoderScale(double ticksPerMeter) {
    this.ticksPerMeter = ticksPerMeter;
  }


  // This is called by the drive-specific physics engine (like MecanumDrive)
  // to make an incremental update to the position and orientation of the robot.
  void updatePositionIncrements(double dCx, double dCy, double dA) {
    cx += dCx;
    cy += dCy;
    a = normalizeAngle(a + dA);
    this.cos_a = Math.cos(a);
    this.sin_a = Math.sin(a);
    updateBoundaryPoints();
    updateWalls();

    // Add to the trail - though this may not
    // be added if it is too close to the previously
    // added point.
    trail.addPoint(cx, cy);
  }


  // This is called by the drive-specific physics engine (like MecanumDrive)
  // to update the linear and angular velocities of the robot.
  void updateVelocities(double newVx, double newVy, double newVa) {
    vx = newVx;
    vy = newVy;
    va = newVa;
  }


  void setMotorPower(int index, double p) {
    power[index] = clipPower(p) * powerAdjust[index];
  }


  void setMotorPowerAll(double pFL, double pFR, double pBL, double pBR) {
    power[FL] = clipPower(pFL) * powerAdjust[FL];
    power[FR] = clipPower(pFR) * powerAdjust[FR];
    power[BL] = clipPower(pBL) * powerAdjust[BL];
    power[BR] = clipPower(pBR) * powerAdjust[BR];
  }


  // Convert robot coordinate to field coordinate - x component,
  // which increases towards the front of the robot.
  double fieldX(double rx, double ry) {
    return cx + rx*cos_a - ry*sin_a;
  }


  // Convert robot coordinate to field coordinate - y component,
  // which increases towards the left of the robot.
  double fieldY(double rx, double ry) {
    return cy + rx*sin_a + ry*cos_a;
  }


  // Convert field coordinate to robot coordinate - x component
  double robotX(double fx, double fy) {
    // First translate to robot's center, then rotate by (-a)
    // to align x-axis with robot's x-axis
    return (fx - cx)*cos_a + (fy - cy)*sin_a;
  }

  // Convert field coordinate to robot coordinate - y component
  double robotY(double fx, double fy) {
    // First translate to robot's center, then rotate by (-a)
    // to align x-axis with robot's x-axis
    return -(fx - cx)*sin_a + (fy - cy)*cos_a;
  }


  void addExtendedStatus() {
    field.addExtendedStatus(String.format("POS     cx:%1.2f  cy:%1.2f  a:%1.2f", cx, cy, balancedAngle(a)));
    field.addExtendedStatus(String.format("POWER   PFL:%5.2f  PFR:%5.2f  PBL:%5.2f   PBR:%5.2f", 
      power[FL], power[FR], power[BL], power[BR]));
    field.addExtendedStatus(String.format("SPEED   Vx:%5.2f   Vy:%5.2f   w:%5.2f", vx, vy, va));
    field.addExtendedStatus(String.format("ENCODERS EFL:%5.2f  EFR:%5.2f  EBL:%5.2f   EBR:%5.2f", 
      readEncoder(FL), readEncoder(FR), readEncoder(BL), readEncoder(BR)
      ));
  }

  //
  // Private methods
  // 

  // Clips to lie within [-1,1]
  private double clipPower(double in) {
    return Math.min(Math.max(in, -1), 1);
  }


  // The boundaryPoints array keeps track of the locations of
  // the corners of the robot, in field coordinates; these change as
  // the robot moves, so need to be updated constantly.
  private void updateBoundaryPoints() {
    double d = props.side/2;
    // This generates four combinations of {-d, d} X {-d, d}, which 
    // are the corners in robot-coordinates; those then have to be tranformed
    // to field coordinates
    updateBoundaryPoint(FL, d, d);
    updateBoundaryPoint(FR, d, -d);
    updateBoundaryPoint(BL, -d, d);
    updateBoundaryPoint(BR, -d, -d);
    if (field.visualizeCollisions) {
      field.visualizeCorners(boundaryPoints);
    }
  };


  // Update a single boundary point with index {index} and robot (not field)
  // coordinates ({rx}, {ry})
  private void updateBoundaryPoint(int index, double rx, double ry) {
    Point p  = boundaryPoints[index];
    // (oldRx, oldRy) is the previous positon of the point, in robot-coordinates.
    double oldRx = robotX(p.x, p.y);
    double oldRy = robotY(p.x, p.y);
    p.set(fieldX(rx, ry), fieldY(rx, ry));
    updateWheelTravel(index, rx - oldRx, ry - oldRy);
  }
  
  
  // Update the amount traveled by a virtual point on the surface of the specified wheel.
  // {dRx} and {dRy} are the change in position of the point of contact with the
  // field, in robot coordinates.
  private void updateWheelTravel(int index, double dRx, double dRy) {
    int sign = index == FL || index == BR ? 1 : -1; // See NOTES.md note "November 27, 2018-A"
    double dRawWheelTravel = dRx + sign * dRy;
    wheelTravel[index] += dRawWheelTravel; 
  }


  private void updateWalls() {
    double d = props.side/2;
    double da = Math.PI/2;
    updateWall(walls[0], d, 0, a); // East wall - faces increasing X (in robot frame of reference, where origin is the center)
    updateWall(walls[1], 0, -d, a - da); // South wall
    updateWall(walls[2], -d, 0, a + 2*da); // West wall
    updateWall(walls[3], 0, d, a + da); // North wall - faces increasing Y
    if (field.visualizeCollisions) {
      field.visualizeWalls(walls);
    }
  }


  private void updateWall(Wall w, double x, double y, double a1) {
    w.reposition(fieldX(x, y), fieldY(x, y), a1);
  }
}
