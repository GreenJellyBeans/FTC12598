import java.util.Arrays; //<>//

// Class RawSensorModule collects simulated sensor information made available to robot control software.
// Sensor errors are included in the simulation.
class RawSensorModule {

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

  RawSensorModule(Field f, Robot r) {
    this.f = f;
    this.r = r;
  }

  int numColorSensors() {
    return currentColors.length;
  }



  void init() {
    // Data used by simulated color sensors
    floorPixels = constructBlurredFloorPixels();
  }


  // Color
  // Returns the r/g/b values of the {i}th color sensor
  // IMPNOTE: See Processing documentation for red(), green() & blue() for explanation
  // of these faster conversion operators.
  // ALSO - don't rename thse red/green/blue because they override built-in red/green/blue
  // Processing functions and cause other RawSensorModule code to mess up
  //
  int sred(int i) {
    return currentColors[i] >> 16 & 0xFF;
  }
  int sgreen(int i) {
    return currentColors[i] >> 8 & 0xFF;
  }
  int sblue(int i) {
    return currentColors[i] & 0xFF;
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
      color c = senseFloorColor(fx, fy);
      currentColors[i] = c;
      f.addExtendedStatus(String.format("ColorSensor[%d]: (%5.1f,%5.1f,%5.1f)", i, red(c), green(c), blue(c)));
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



  // Sense the floor color looking downards with a sensor that scans a region
  // of diameter {constant colorSensorDiameter}
  // at field location ({x}, {y}). All units in meters. Returns a composite color value 
  color senseFloorColor(double x, double y) {
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
