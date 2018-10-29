// Code to calculate forces during collisions of the
// robot with "walls" - which are vertical rectangles
// on the field. They could also potentially be the walls
// of other robots, however walls are considered immovable
// (or to move neglagably) as far as how they impact
// the instantenous calculation of impact force.
// Author: Joseph M. Joy, FTC12598 mentor.

final static double EPSILON_LENGTH = 0.001; // Length amount (meters) considered to be close enough to zero
final static double EPSILON_ANGLE = 0.001;  // Angle amount (radians) considered to be close enough to  zero
final static double TWO_PI =  2*Math.PI;


// Data for a single wall
// All coordinates are field coordinates
class Wall {
  final boolean isBoundary; // True iff the wall is a field boundary
  double cx; // of midpoint - in m
  double cy; // of midpoint - in m
  double len; // length - in m
  double thickness;
  double aN; // angle of normal - in radians
  double nx; // unit vector of normal - x component
  double ny; // unit vector of normal - y component

  // A wall whose position and orientation will be defined later or constantly updated.
  Wall(double len, double thickness) {
    this.len = len;
    this.thickness = thickness;
    isBoundary = false;
    reposition(0, 0, 0);
  }

  Wall(double x, double y, double len, double thickness, double aN, boolean boundary) {
    this.len = len;
    this.thickness = thickness;
    reposition(x, y, aN);
    isBoundary = boundary;
  }


  // Place wall at new location and orientation
  void reposition(double x, double y, double aN) {
    this.cx = x;
    this.cy = y;
    this.aN = aN;
    this.nx = Math.cos(aN);
    this.ny = Math.sin(aN);
  }

  // Rotate entire Wall by {a} about point (px, py)
  void rotate(double a, double px, double py) {
    double c = Math.cos(a);
    double s = Math.sin(a);

    // Translate origin (temporarily) to (px, py)
    double x1 = cx - px;
    double y1 = cy - py;
    cx = px + c*x1 - s*y1;
    cy = py + c*y1 + s*x1;

    // Update wall angle and normals
    aN = aN + a;
    nx = Math.cos(aN);
    ny = Math.sin(aN);
  }

  // Calculates the magnetude of the collision
  // force - it will be normal to the wall (walls are frictionless), and simply
  // a function of how much "behind" the point is to the wall.
  // This amount reflects how much the wall has deflected - the more
  // it has deflected, the greater the collision force, and this
  // relationship is nonlinear.
  // To get the component along x or y axes, simply
  // multiply by nx or ny.
  double collisionMagnitude(double px, double py, double vx, double vy) {
    if (isBoundary) {
      // Quick check for boundary walls
      if (insideField(px, py)) {
        return 0; // ****** EARLY RETURN
      }
    } else {
      // Quick check if the non-boundary wall is too far away from point
      double distToCenter = distance(px, py, cx, cy);
      if (distToCenter > len + thickness) {
        return 0; // ****** EARLY RETURN
      }
    }

    // We now have to transform (px, py) into the wall's coordinate system, and determine how
    // much "behind" the wall the point is, and calculate the collision force magnitude appropriately.
    // If it is not behind the wall, the magnetude is zero, of course.
    double x = px-cx;
    double y = py-cy;
    // Note that nx is cos(aN) and ny is sin(aN), where aN is
    // the angle of the normal to the x-axis. We want to rotate
    // by (-aN).
    double xx = x * nx + y *ny;
    double yy = -x * ny + y * nx;

    // For non-boundary walls, we taper off thickness at corners. This helps to
    // reduce the cases of mistaken collisions when you have neighboring walls of
    // a convex structure. The tapering is at a 45-degree angle, that works best for
    // right-angle corners. Results for non-right angle corners will vary...
    double thick = isBoundary? thickness : Math.min(thickness, len/2 - Math.abs(yy));
    if (xx > -thick && xx < 0 && Math.abs(yy) < len /2) {
      // Collision!
      // This is a damped collision - energy is not preserved. The force
      // resists motion in a direction against the wall much more than
      // the force pushing the robot back out once it has started to move outwards.
      double vxx = vx * nx + vy * ny;
      double BREAKING_FORCE_FACTOR = 10000;
      double RESTORING_FORCE_FACTOR = 10;
      // Sometimes the velocity oscilates positive
      // when it is in the breaking zone, so we have to
      // ignore small positive velosities, otherwise the
      // robot can creep through walls.
      boolean breaking = vxx < 0.01;
      double forceFactor = breaking ? BREAKING_FORCE_FACTOR : RESTORING_FORCE_FACTOR;
      return  - xx * forceFactor; // We return a positive value always
    }
    return 0;
  }


  // Returns true iff the point ({px}, {py}) is within
  // the field boundary
  private boolean insideField(double px, double py) {
    return px > 0 && px < g_field.BREADTH && py > 0 && py < g_field.DEPTH;
  }
};


// The aggregate force and torque
// as a result of a collision
class CollisionResult {
  final double fx; // in N
  final double fy; // in N
  final double torque; // in Nm


  CollisionResult(double fx, double fy, double torque) {
    this.fx = fx;
    this.fy = fy;
    this.torque = torque;
  }
}


// Calculates the result of a potential impact of a set of corner points  with
// a set of walls. Net torque is computed about the point ({drive.x}, {drive.y}), which is
// the center of the robot. Special case: null is returned if there is negigable net force or torque. 
// If {robotCorners} then the collisions are between the corners of the robot and the walls. If !{robotCorners}
// then the collision is computed between the corners of external walls and the sides of the robot.
CollisionResult calculateCollisionImpact(DriveBase drive, boolean robotCorners) {
  RobotProperties props = drive.props;
  Point[]corners = (robotCorners) ? drive.boundaryPoints : drive.field.convexCorners;
  Wall[] walls = (robotCorners) ? drive.field.walls : drive.walls;
  double cx = drive.cx;
  double cy = drive.cy;
  double fx = 0;
  double fy = 0;
  double torque = 0;

  if (walls == null || corners == null || walls.length == 0 || corners.length == 0) {
    return null; // ************ EARLY RETURN **************
  }

  // Robot walls face outwards - in the opposite direction to normal external walls when they
  // collide with the robot. So we have to invert directions when aggregating forces and torques.
  // This is captured in the following variable.
  double direction = robotCorners ? 1 : -1;


  for (Point p : corners) {
    // To calculate torque about (cx, cy), we need to first translate 
    // the origin to (p.x, p.y), the point of collision.
    double cxx = cx - p.x;
    double cyy = cy - p.y;

    for (Wall w : walls) {
      // Note that velocity direction also has to be reversed - this is used in computing
      // assymetric collision reaction force.
      double mag = w.collisionMagnitude(p.x, p.y, direction*drive.vx, direction*drive.vy);

      // No need to further process Wall w if it does not
      // collide with p
      if (props.noForce(mag)) {
        continue;
      }

      if (g_field.visualizeCollisions) {
        // Draw a nice red disk at the collision point.
        fill(255, 0, 0);
        noStroke();
        g_field.drawCircle(p.x, p.y, 0.1);
      }

      // Collision force magnetude is normal to the wall, so we
      // calculate forces in the x and y directions
      // by multiplying by the appropriate wall normal vector
      // components, w.nx and w.ny
      // HOWEVER, if the walls are robot walls, we have to reverse the normals so that they
      // point inwards.

      fx += mag * w.nx * direction;
      fy += mag * w.ny * direction;

      // Then we rotate the x-axis to be aligned with the wall normal.
      // But we only need the transformed y-coordinate becasue that is
      // the distance between (p.x, p.y) that is perpendicular to the normal
      // force. The rotation is (-nA), where nA is the angle of the normal.
      // Note that nx is cos(aN) and ny is sin(aN), where aN is
      // the angle of the normal to the x-axis. We want to rotate
      // by (-aN).
      double pyy = -cxx * w.ny + cyy * w.nx;
      torque += mag * pyy * direction;
    }
  }

  if (props.noForce(fx) && props.noForce(fy) && props.noTorque(torque)) {
    return null;
  }
  return new CollisionResult(fx, fy, torque);
}


static double distance(double x1, double y1, double x2, double y2) {
  double dx = x2-x1;
  double dy = y2-y1;
  return Math.sqrt(dx*dx + dy*dy);
}


static boolean sameLength(double a, double b) {
  return Math.abs(a-b) < EPSILON_LENGTH;
}


static boolean sameAngle(double a, double b) {
  return Math.abs(normalizeAngle(a) - normalizeAngle(b)) < EPSILON_ANGLE;
}


// Returns an equivalent angle that is within [0, 2*Pi]
// a can be negative.
static double normalizeAngle(double a) {
  return  a < 0 ?  TWO_PI - ((-a) % TWO_PI) : a % TWO_PI;
}


// Return a value between -Pi and Pi - suitable for
// PID algorithms and such
static double balancedAngle(double a) {
  double na = normalizeAngle(a); // always positive
  return na < Math.PI ? na : na - TWO_PI;
}
