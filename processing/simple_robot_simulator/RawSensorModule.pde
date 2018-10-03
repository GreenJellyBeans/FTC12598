// Class RawSensorModule collects simulated sensor information made available to robot control software.
// Sensor errors are included in the simulation.
class RawSensorModule {

  Robot r;
  Field f;
  final double colorSensorDiameter = 0.2; // 2cm diameter

  // These are the locations of the centers of the color sensors, relative to the robot. In meters.
  // Color sensors all look straight down.
  Point[] colorSensorLocations = {
    new Point(0, 0) // Center of robot
  };
  color[] currentColors = {0};

  // The following dictate the behavior of Perlin noise applied
  // to the color sensor. See Processing help for noise() for more information.
  final float redNoiseBase = 0;
  final float greenNoiseBase = 1;
  final float blueNoiseBase = 2;
  final float noiseScale = 1; 

  RawSensorModule(Field f, Robot r) {
    this.f = f;
    this.r = r;
  }

  int numColorSensors() {
    return currentColors.length;
  }
  
  // Color
  // Returns the r/g/b values of the {i}th color sensor
  // See Processing documentation for red(), green() & blue() for explanation
  // of these faster conversion operators.
  int red(int i) {
    return currentColors[0] >> 16 & 0xFF;
  }
  int green(int i) {
    return currentColors[0] >> 8 & 0xFF;
  }
  int blue(int i) {
    return currentColors[0] & 0xFF;
  }

  // Updates the simulation,
  // assuming the absoute time is {t} seconds, and {dT} seconds have elapsed
  // since previous call
  void simloop(double t, double dT) {

    // Update color values
    for (int i = 0; i < currentColors.length; i++) {
      Point p = colorSensorLocations[i];
      double fx = r.drive.fieldX(p.x, p.y);
      double fy = r.drive.fieldY(p.x, p.y);
      color c = f.senseFloorColor(fx, fy, colorSensorDiameter);
      currentColors[i] = c; //color(redNoise(), greenNoise(), blueNoise());
      f.addExtendedStatus(String.format("Color[%d].red(): %d", i, red(c)));
    }
  }

  //
  // redNoise, blueNoise and greenNoise return Perlin noise that is correlated with the robot's location on field. 
  //

  float redNoise() {
    return (float) (255*noise((float) (redNoiseBase + r.drive.x * noiseScale), (float) (redNoiseBase + r.drive.y * noiseScale)));
  }

  float greenNoise() {
    return (float) (255*noise((float) (greenNoiseBase + r.drive.x * noiseScale), (float) (greenNoiseBase + r.drive.y * noiseScale)));
  }

  float blueNoise() {
    return (float) (255*noise((float) (blueNoiseBase + r.drive.x * noiseScale), (float) (blueNoiseBase + r.drive.y * noiseScale)));
  }
}
