// RobotProperties collects  non-changing properties of the robot. They may vary 
// from robot instance to robot instance.
// Author: Joseph M. Joy, FTC12598 mentor.
class RobotProperties {
  // Only metric units allowed.
  //
  final double mass = 42/2.2; // In kg
  final double weight = mass * 9.8; // In N.
  
    // In reality this depends on orientation of the wheels, but we just assume it is
  // isotropic against the prevailing direction of motion of the robot.
  final double staticLinFriction = 0.1;//0.04; // Coef. of static friction - unitless
  final double dynamicLinFriction = 0.1  ;//0.04; // Coef. of dynamic friction - unitless
  final double dampingTorqueAdjustment = 1;//0.45; // Factor applied when converting max damping force to torque
  final double side = 17*0.0254; // side of the square robot, m. (17 inches).
  final double rotInertia = mass * side * side / 6; // Rotational inertia in kg-m^2 - rect prism of uniform density
  final double P_TO_F_SCALE = 0.1; // Unitless motor power (which is within [-1, 1]) to force (N) conversion
  final double FORCE_FRAC = 1/Math.sqrt(2); // Fraction of wheel force in the x (or y) direction - assuming square positioning.
  // Cos(Pi/4) == Sin(Pi/4) (or Cos(45 degrees))
  final double MAX_VIRTUAL_SPEED = 2.0; // M/sec. Corner (wheel) speed at which motor torques go to zero.
  final double MAX_MOTIVE_FORCE_PER_WHEEL = weight*0.1; // N. Equivalent of stall torque, expressed as force acting on the diagonal.


  final double DISTANCE_ZERO = 0.001; // 1mm is considered close enough to be the same length/distance/position
  final double POWER_ZERO = 0.05; // Unit-less motor power. Values less than this are considered to be no power.
  final double LIN_SPEED_ZERO = 0.001; // In m/s. Less than 1mm per second is considered NOT moving
  final double ANGULAR_SPEED_ZERO = 0.1*Math.PI/180; // In rad/sec. Less than 1 degree of rotation per second is considered NOT rotating.
  final double FORCE_MAG_ZERO = weight/10000; // Forces < 0.01% of the weight of the robot are considered effectively no force 
  final double TORQUE_MAG_ZERO = FORCE_MAG_ZERO*side; // Base it off the zero force and side of the robot
  final double MOTOR_DRAG_FORCE = P_TO_F_SCALE*100;//4; // N. A simple approximation of the impact of the drag of an unpowered motor.

  boolean isMoving(double vx, double vy) {
    return Math.max(Math.abs(vx), Math.abs(vy)) > LIN_SPEED_ZERO;
  }


  // Returns -1 | 0 | 1 depending on whether speed (in m/sec) is -ve, 0 or +ve
  //
  int linearDirection(double speed) {
    return (speed < -LIN_SPEED_ZERO) ? -1 : ((speed > LIN_SPEED_ZERO) ? 1 : 0);
  }


  // Returns -1 | 0 | 1 depending on whether speed (in m/sec) is -ve, 0 or +ve
  //
  int angularDirection(double omega) {
    return (omega < -ANGULAR_SPEED_ZERO) ? -1 : ((omega > ANGULAR_SPEED_ZERO) ? 1 : 0);
  }


  // Effectively no power  - this is the unitless power used to control the motor (setPower)
  boolean noPower(double p) {
    return Math.abs(p) < POWER_ZERO;
  }


  // Effectively no speed  - {s} in m/sec
  boolean noSpeed(double s) {
    return Math.abs(s) < LIN_SPEED_ZERO; // 0.1% of the weight of the robot - somewhat arbitrary
  }


  // Effectively no angular velocity  - {a} in rad / sec
  boolean noRotation(double a) {
    return Math.abs(a) < ANGULAR_SPEED_ZERO;
  }


  // Effectively no force  - {f} in N
  boolean noForce(double f) {
    return Math.abs(f) < FORCE_MAG_ZERO;
  }


  // Effectively no torque  - {t} in Nm
  boolean noTorque(double t) {
    return Math.abs(t) < TORQUE_MAG_ZERO;
  }
  
  // A length or distance close enough to zero
  boolean noLength(double len) {
    return Math.abs(len) < DISTANCE_ZERO;
  }


  // Points are close enough to be considered the same
  boolean samePoint(double x1, double y1, double x2, double y2) {
    return (Math.abs(x1-x2) + Math.abs(y1-y2)) < DISTANCE_ZERO;
  }
}
