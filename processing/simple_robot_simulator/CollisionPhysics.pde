// Code to calculate forces during collisions of the
// robot with "walls" - which are vertical rectangles
// on the field. They could also potentially be the walls
// of other robots, however walls are considered immovable
// (or to move neglagably) as far as how they impact
// the instantenous calculation of impact force.
// Author: Joseph M. Joy, FTC12598 mentor.

final double EPSILON_LENGTH = 0.001; // Length amount (meters) considered to be close enough to zero
final double EPSILON_ANGLE = 0.001;  // Angle amount (radians) considered to be close enough to  zero


// Data for a single wall
// All coordinates are field coordinates
class Wall {
  final boolean isBoundary; // True iff the wall is a field boundary
  double cx; // of midpoint - in m
  double cy; // of midpoint - in m
  double len; // length - in m
  double aN; // angle of normal - in radians
  double nx; // unit vector of normal - x component
  double ny; // unit vector of normal - y component


  Wall(double x, double y, double len, double aN) {
    this.len = len;
    reposition(x, y, aN);
    isBoundary = boundaryWall(x, y, len, aN);
  }


  // Place wall at new location and orientation
  void reposition(double x, double y, double aN) {
    this.cx = x;
    this.cy = y;
    this.aN = aN;
    this.nx = Math.cos(aN);
    this.ny = Math.sin(aN);
  }


  // Calculates the magnetude of the collision
  // force - it will be normal to the wall (walls are frictionless), and simply
  // a function of how much "behind" the point is to the wall.
  // This amount reflects how much the wall has deflected - the more
  // it has deflected, the greater the collision force, and this
  // relationship is nonlinear.
  // To get the component along x or y axes, simply
  // multiply by nx or ny.
  double collisionMagnitude(double px, double py) {
    if (isBoundary) {
      // Quick check for boundary walls
      if (insideField(px, py)) {
        return 0; // ****** EARLY RETURN
      }
    } else {
      // Quick check if the non-boundary wall is too far away from point
      double distToCenter = distance(px, py, cx, cy);
      if (distToCenter > len) {
        return 0; // ****** EARLY RETURN
      }
    }

    // We now have to transform (px, py) into the wall's coordinate system, and determine how
    // much "behind" the wall the point is, and calculate the collision force magnitude appropriately.
    // If it is not behind the wall, the magnetude is zero, of course.
    double x = px-cx;
    double y = py-cy;
    // Note that nx is cos(aN) and ny is sin(aN), where aN is
    // the angle of the normal to the x-axis.
    double xx = x * nx + y *ny;
    double yy = -x * ny + y * nx;
    if (xx < 0 && Math.abs(yy) < len /2) {
      // Collision!
      double FORCE_FACTOR = 10;
      return  -xx * FORCE_FACTOR; // We return a positive value always
    }
    return 0;
  }


  // Determines if the wall is one of the boundary walls
  // For it to be a boundary wall it has to be right on the boundary of
  // the field.
  private boolean boundaryWall(double x, double y, double len, double aN) {
    boolean bX =  sameLength(x, 0) || sameLength(x, g_field.BREADTH);
    boolean bY =  sameLength(y, 0) || sameLength(y, g_field.DEPTH);
    boolean bLen = sameLength(len, g_field.BREADTH) || sameLength(len, g_field.DEPTH); // slightly lax but ok
    boolean bA = sameAngle(aN, 0) || sameAngle(aN, Math.PI/2) 
      || sameAngle(aN, Math.PI) || sameAngle(aN, Math.PI*3/2);
    return bX && bY && bLen && bA;
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


// Calculates the result of a potential impact of a set of corner points {corners} with
// a set of walls. Net torque is computed about the point ({cx}, {cy}), which is typically
// the center of the robot.
CollisionResult calculateCollisionImpact(Wall[] walls, Point[]corners, double cx, double  cy) {
  return null;
}


double distance(double x1, double y1, double x2, double y2) {
  double dx = x2-x1;
  double dy = y2-y1;
  return Math.sqrt(dx*dx + dy*dy);
}


boolean sameLength(double a, double b) {
  return Math.abs(a-b) < EPSILON_LENGTH;
}


boolean sameAngle(double a, double b) {
  return Math.abs(normalizeAngle(a) - normalizeAngle(b)) < EPSILON_ANGLE;
}


// Returns an equivalent angle that is within [0, 2*Pi]
// a can be negative.
double normalizeAngle(double a) {
  final double twoPi = 2*Math.PI;
  return  a < 0 ?  a = twoPi - ((-a) % twoPi) : a % twoPi;
}

void testCollisionPhysics() {
  final double SIZE = 10;
  double cx = SIZE/2;
  double cy = SIZE;
  Wall w = new Wall(cx, cy, SIZE, -Math.PI/2);
  println("MAG: " + w.collisionMagnitude(cx, cy+0.001));
}
