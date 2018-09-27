//
// The Field class implements rendering of the robot field, including display of robot stats.
//
class Field {

  public final double WIDTH = 12*12*0.0254; // field width in meters (12 ft).; // in meters

  final int FIELD_OFFSET_PIXELS = 50; // Offset from origin (top-left)
  final int FIELD_WIDTH_PIXELS = width - 2 * FIELD_OFFSET_PIXELS;
  final double PIX_PER_M = FIELD_WIDTH_PIXELS / WIDTH; // pixels per meter
  String status = "NOTHING";


  public void draw() {
    // Boundary
    fill(128);
    stroke(0);
    strokeWeight(4);
    drawRect(WIDTH/2, WIDTH/2, WIDTH, WIDTH);

    // Draw foam tile boundaries.
    strokeWeight(1);
    stroke(100);
    for (int i = 1; i <= 5; i++) {
      double offset = i * WIDTH/6;
      drawLine(offset, 0, offset, WIDTH);
      drawLine(0, offset, WIDTH, offset);
    }

    // Status
    fill(0);
    text(status, screenX(0), screenY(0) + 20);
  }


  // Screen coordinates (in pixels) of field cordinate {x} (in meters)
  float screenX(double x) {
    return (float) (FIELD_OFFSET_PIXELS + x * PIX_PER_M);
  }


  // Screen coordinates (in pixels) of field cordinate {y} (in meters)
  float screenY(double y) {
    return (float) (-FIELD_OFFSET_PIXELS + height - y * PIX_PER_M); // y grows upwards, py grows downwards
  }


  // Length (in pixels) of field distance {len} (in meters)
  float pixLen(double len) {
    return (float) (len * PIX_PER_M);
  }


  public void drawPoint(double x, double y) {
    point(screenX(x), screenY(y));
  }


  void drawRect(double x, double y, double w, double h) {
    rect(screenX(x), screenY(y), pixLen(w), pixLen(h));
  }


  void drawLine(double x1, double y1, double x2, double y2) {
    line(screenX(x1), screenY(y1), screenX(x2), screenY(y2));
  }


  void updateStatus(String s) {
    status = s;
  }
}
