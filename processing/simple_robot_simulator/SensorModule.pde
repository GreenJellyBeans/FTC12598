import java.util.Arrays;

// Class SensorModule collects simulated sensor information made available to robot control software.
// Sensor errors are included in the simulation.
class SensorModule {

  Robot r;
  Field f;
  final double colorSensorDiameter = 0.2; // 2cm diameter
  PixelHelper floorPixels = null; // Initialized in init()

  // These are the locations of the centers of the color sensors, relative to the robot. In meters.
  // Color sensors all look straight down.
  final int BLUR_RADIUS = 10; // Number of pixels to blur - TBD - should base it on physical dimensions
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

  SensorModule(Field f, Robot r) {
    this.f = f;
    this.r = r;
  }

  int numColorSensors() {
    return currentColors.length;
  }



  void init() {
    // Data used by simulated color sensors
    floorPixels = constructBlurredFloorPixels();
    imu_reset();
    encoder_resetAll();
  }


  // Color
  // Returns the r/g/b values of the {i}th color sensor
  // IMPNOTE: See Processing documentation for red(), green() & blue() for explanation
  // of these faster conversion operators.
  // ALSO - don't rename thse red/green/blue because they override built-in red/green/blue
  // Processing functions and cause other RawSensorModule code to mess up
  //
  int color_red(int i) {
    return currentColors[i] >> 16 & 0xFF;
  }
  
  
  int color_green(int i) {
    return currentColors[i] >> 8 & 0xFF;
  }
  
  
  int color_blue(int i) {
    return currentColors[i] & 0xFF;
  }

  //
  // IMU position and orientation.
  // 
  private double startBearing = 0;
  // Reset the IMU. All readings will
  // be relative to this location and orientation.
  void imu_reset() {
    startBearing = r.base.a;
  }


  // Bearing in radians - since last reset
  double imu_bearing() {
    return normalizeAngle(r.base.a - startBearing);
  }


  void encoder_resetAll() {
    r.base.resetEncoders();
  }


  // Sets the unit of an encoder "tick".
  void encoder_setScale(double ticksPerMeter) {
    r.base.setEncoderScale(ticksPerMeter);
  }


  double encoder_FL() {
    return r.base.readEncoder(r.base.FL);
  }


  double encoder_FR() {
    return r.base.readEncoder(r.base.FR);
  }


  double encoder_BL() {
    return r.base.readEncoder(r.base.BL);
  }


  double encoder_BR() {
    return r.base.readEncoder(r.base.BR);
  }

  // Determines if a particular "complex" condition holds.
  // {options} contain the list of expected conditions (one char per
  // condition). {notFound} is returned if none of these conditions hold.
  // All that is done is to read the keyboard - if a particular key is pressed
  // that matches a character in {options}, that condition is considered to hold!
  char complex_condition(String options, char notFound) {
    char ret = notFound;
    if (keyPressed) {
      if (options.indexOf(key) >= 0) {
        ret = key;
      }
    }
    return ret;
  }

  // Updates the simulation,
  // assuming the absoute time is {t} seconds, and {dT} seconds have elapsed
  // since previous call
  void simloop(double t, double dT) {

    // Update color values
    for (int i = 0; i < currentColors.length; i++) {
      Point p = colorSensorLocations[i];
      double fx = r.base.fieldX(p.x, p.y);
      double fy = r.base.fieldY(p.x, p.y);
      color c = senseFloorColor(fx, fy);
      currentColors[i] = c;
      f.addExtendedStatus(String.format("ColorSensor[%d]: (%5.1f,%5.1f,%5.1f)", i, red(c), green(c), blue(c)));
    }
  }

  //
  // ******* PRIVATE METHODS ********
  //


  //
  // redNoise, blueNoise and greenNoise return Perlin noise that is correlated with the robot's location on field. 
  //

  private float redNoise() {
    return (float) (255*noise((float) (redNoiseBase + r.base.cx * noiseScale), (float) (redNoiseBase + r.base.cy * noiseScale)));
  }

  private float greenNoise() {
    return (float) (255*noise((float) (greenNoiseBase + r.base.cx * noiseScale), (float) (greenNoiseBase + r.base.cy * noiseScale)));
  }

  private float blueNoise() {
    return (float) (255*noise((float) (blueNoiseBase + r.base.cx * noiseScale), (float) (blueNoiseBase + r.base.cy * noiseScale)));
  }



  // Sense the floor color looking downards with a sensor that scans a region
  // of diameter {constant colorSensorDiameter}
  // at field location ({x}, {y}). All units in meters. Returns a composite color value 
  private color senseFloorColor(double x, double y) {
    final double FPM = f.elements.FLOORPIX_PER_M;
    // We have to tralsform field coordinates (x,y) to the
    // pre-rendered pixel buffer.
    int bx = bound((int) (x * FPM), 0, f.elements.FLOOR_PIXEL_BREADTH - 1);
    int by = bound((int) ((f.DEPTH-y) * FPM), 0, f.elements.FLOOR_PIXEL_DEPTH - 1);
    return floorPixels.get(bx, by);
  }


  // Construct the blurred floor pixels that are used by the simulated
  // color sensors. Since it can take several seconds to compute this, we
  // attempt to use a cashed version saved as a PNG.
  private PixelHelper constructBlurredFloorPixels() {
    byte[] curSig = constructFloorSignature().getBytes();
    final String sigFileName = "data/cache/blurryFloorSignature.txt";
    final String pngFileName = "data/cache/blurryFloor.png";
    File sigF = new File(sketchPath(sigFileName));

    // Retrieve and validate cached version...
    boolean sigMatch = false;
    int sizeX = f.elements.FLOOR_PIXEL_BREADTH;
    int sizeY = f.elements.FLOOR_PIXEL_DEPTH;

    if (sigF.exists()) {
      byte[] prevSig = loadBytes(sigFileName);
      if (prevSig != null) {
        if (Arrays.equals(curSig, prevSig)) {
          println("SIGNATURE MATCHED!");
          sigMatch = true;
        } else {
          println("SIGNATURE NOT MATCHED!");
        }
      }
    }
    if (sigMatch) {
      PImage img = loadImage(pngFileName);
      if (img == null) {
        println("Could not load file " + pngFileName);
      } else {
        img.loadPixels();
        color[] cachedPix = img.pixels;
        if (img.height != sizeY || img.width != sizeX || cachedPix.length != sizeX * sizeY) {
          println("Cached blurry file dimensions are wrong; re-creating cache.");
        } else {
          return new PixelHelper(cachedPix, sizeX, sizeY); // ****** EARLY RETURN ****
        }
      }
    }

    // There is no cache or the cache is invalid. So we generate the
    // floor pixels from scratch and create or update the cache.
    println("*********************************************************");
    println("PLEASE WAIT WHILE THE BLURRY FLOOR PIXELS ARE COMPUTED...");
    println("*********************************************************");
    PixelHelper rawPix = f.elements.generateFloorPixels();
    PixelHelper blurredPix =  rawPix.blurredCopy(BLUR_RADIUS);
    PImage img = createImage(sizeX, sizeY, RGB);
    img.loadPixels();
    color[] destPixels = img.pixels;
    assert(destPixels.length == blurredPix.pix.length);
    System.arraycopy(blurredPix.pix, 0, destPixels, 0, blurredPix.pix.length);
    img.updatePixels();

    // Save signature and PNG file representing blurry floor
    img.save(pngFileName);
    saveBytes(sigFileName, curSig);

    return blurredPix;
  }


  // Construct a string that uniquely identifies the state of the blurred floor.
  // Used to decide whether or not to re-compute the blurred floor, which
  // takes a while.
  private String constructFloorSignature() {
    StringBuilder sb = new StringBuilder();
    sb.append("BLURVERSION: 1.0 BLUR:"+BLUR_RADIUS+"\n");
    f.elements.appendFloorSignature(sb);
    return sb.toString();
  }
}
