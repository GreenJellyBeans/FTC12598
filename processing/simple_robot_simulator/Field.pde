//
// The Field class implements rendering of the robot field, including display of robot stats.
//
class Field {

  public final double WIDTH = 12*12*0.0254; // field width in meters (12 ft).; // in meters

  final int FIELD_OFFSET_PIXELS = 50; // Offset from origin (top-left)
  final int FIELD_WIDTH_PIXELS = height - 2 * FIELD_OFFSET_PIXELS;
  final double PIX_PER_M = FIELD_WIDTH_PIXELS / WIDTH; // pixels per meter
  String status = "NOTHING"; // Single line of status printed below the field.
  String extendedStatus ="NOT\nONE\nTHING"; // Multiline status printed to right of field
  final FieldElements elements = new FieldElements(this);

  void init() {
    elements.load();
  }


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

    // draw field elements
    elements.draw();

    // Status
    fill(0);
    drawText(status, 0, 0, 0, 20);
    drawText(extendedStatus, WIDTH, WIDTH, 20, 0);
    extendedStatus = "";
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

  void drawCircle(double x, double y, double r) {
    float sr = pixLen(r);
    ellipse(screenX(x), screenY(y), sr, sr);
  }



  void drawLine(double x1, double y1, double x2, double y2) {
    line(screenX(x1), screenY(y1), screenX(x2), screenY(y2));
  }
  
  // ({xPix}, {yPix}) in pixels is added before rendering text
  void drawText(String txt, double x1, double y1, int xPix, int yPix) {
    text(txt, screenX(x1)+xPix, screenY(y1)+yPix);
  }


  void updateStatus(String s) {
    status = s;
  }

  void addExtendedStatus(String s) {
    extendedStatus += s + "\n";
  }

  // Sense the flor color looking downards with a sensor that scans a region
  // of diameter {sensorDiameter}
  // at field location ({x}, {y}). All units in meters. Returns a composite color value 
  color senseFloorColor(double x, double y, double sensorDiameter) {
    float r = (float) (255*x/WIDTH);
    float g = (float) (255*y/WIDTH);
    return color(r, g, 0);
  }
  

}
