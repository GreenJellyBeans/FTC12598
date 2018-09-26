//
// The Field class implements rendering of the robot field, including display of robot stats.
// Author: Joseph M. Joy, FTC12598 mentor.
//
class Field {

  public final double WIDTH = 12*12*0.0254; // field width in meters (12 ft).; // in meters

  final int FIELD_OFFSET_PIXELS = 50; // Offset from origin (top-left)
  final int FIELD_WIDTH_PIXELS = width - 2 * FIELD_OFFSET_PIXELS;
  final double PIX_PER_M = FIELD_WIDTH_PIXELS / WIDTH; // pixels per meter
  
  public void draw() {
    // Boundary
    float pixWidth = pixLen(WIDTH);
    fill(128);
    rect(FIELD_OFFSET_PIXELS+pixWidth/2, FIELD_OFFSET_PIXELS+pixWidth/2, pixWidth, pixWidth);
  }

  // Screen coordinates (in pixels) of field cordinate {x} (in meters)
  float screenX(double x) {
    return (float) (FIELD_OFFSET_PIXELS + x * PIX_PER_M);
  }

  // Screen coordinates (in pixels) of field cordinate {y} (in meters)
  float screenY(double y) {
    return (float) (FIELD_OFFSET_PIXELS + height - y * PIX_PER_M); // y grows upwards, py grows downwards
  }

  // Length (in pixels) of field distance {len} (in meters)
  float pixLen(double len) {
    return (float) (len * PIX_PER_M);
  }
}
