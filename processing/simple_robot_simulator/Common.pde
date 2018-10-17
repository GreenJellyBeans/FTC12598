// Common Classes and utility methods for the Robot Simulator go here

// Represents a 2D point - units depend on application.
class Point {
  double x;
  double y;


  Point() {
    this(0, 0);
  }


  Point(double x, double y) {
    set(x, y);
  }


  void set(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  // Rotate the point by angle {a} about point (px, py)
  void rotate(double a, double px, double py) {
    double c = Math.cos(a);
    double s = Math.sin(a);
    
    // Translate origin (temporarily) to (px, py)
    double x1 = x - px;
    double y1 = y - py;
    x = px + c*x1 - s*y1;
    y = py + c*y1 + s*x1;    
  }
  
}

// The DriveTask collects together methods that control the robot movement
interface DriveTask {
  void init();
  void deinit();
  void start();
  void stop();
  void loop();
}



// Returns clipped version of {in} guaranteed to between [{mn}, {mx}]
int bound(int in, int mn, int mx) {
  assert(mn <= mx);
  return max(min(in, mx), mn);
}


// Double version of the above
double bound(double in, double mn, double mx) {
  assert(mn <= mx);
  return Math.max(Math.min(in, mx), mn);
}

// Returns clipped version of {in} guaranteed to between [{mn}, {mx}]
// Note that both bounds are inclusive.
boolean inBounds(int in, int mn, int mx) {
  assert(mn <= mx);
  return in >= mn && in <= mx;
}


// Double version of the above
boolean inBounds(double in, double mn, double mx) {
  assert(mn <= mx);
  return in >= mn && in <= mx;
}


double meters(double feet) {
  return feet * 0.3048;
}

// Emulates a 2D array of colors
// and supports blur
class PixelHelper {

  final int w;
  final int h;
  final color outOfBoundColor;
  final color pix[];


  // Constructs a PixelHelper that works with 
  // packed array {pix}, assumed to be row-major, with
  // {h} rows of width {w}. Therefore pix.length == w*h.
  PixelHelper(color[] pix, int w, int h, color outOfBoundColor) {
    assert(pix.length == w*h);
    this.pix = pix;
    this.w = w;
    this.h = h;
    this.outOfBoundColor = outOfBoundColor;
  }


  // Black is the default outOfBoundColor
  PixelHelper(color[] pix, int w, int h) {
    this(pix, w, h, 0);
  }


  color get(int x, int y) {
    return inBounds(x, 0, w) && inBounds(y, 0, h) ? pix[y*w + x] : outOfBoundColor;
  }


  void set(int x, int y, color c) {
    if (inBounds(x, 0, w) && inBounds(y, 0, h)) {
      pix[y*w + x] = c;
    }
  }


  // Returns a blurred copy. Original array remains unmodified.
  PixelHelper blurredCopy(int blurWidth) {
    color[] pxOut = new color[w*h];
    PixelHelper out = new PixelHelper(pxOut, w, h, outOfBoundColor);
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int n = 0;

        // Calculate blur
        for (int ki = -blurWidth; ki <= blurWidth; ki++) {
          int x = i + ki;
          if (x >= 0 && x < w) {
            for (int kj = -blurWidth; kj <= blurWidth; kj++) {
              int y = j + kj;
              if (y >= 0 && y < h) {
                color c = get(x, y);
                redSum += red(c);
                greenSum += green(c);
                blueSum += blue(c);
                n++;
              }
            }
          }
        }

        if (n > 0) {
          out.set(i, j, color((float)redSum/n, (float)greenSum/n, (float)blueSum/n));
        }
      }
    }
    return out;
  }
}
