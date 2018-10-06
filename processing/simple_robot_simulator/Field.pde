//
// The Field class implements rendering of the robot field, including display of robot stats.
//
class Field {

  public final double BREADTH = 12*12*0.0254; // field width in meters (12 ft).; // in meters
  public final double DEPTH = BREADTH * 1.5;  // field depth in meters (along the y axis)
  public final color MAT_COLOR = 128; // Color of the mat.
  final int FIELD_OFFSET_PIXELS = 50; // Offset from origin (top-left)
  final int FIELD_DEPTH_PIXELS = height - 2 * FIELD_OFFSET_PIXELS;
  final double PIX_PER_M = FIELD_DEPTH_PIXELS / DEPTH; // pixels per meter
  String status = "NOTHING"; // Single line of status printed below the field.
  String extendedStatus ="NOT\nONE\nTHING"; // Multiline status printed to right of field
  final FieldElements elements = new FieldElements(this);

  void init() {
    elements.load();
  }


  public void draw() {

    // Boundary and mat
    fill(MAT_COLOR);
    stroke(0);
    strokeWeight(4);
    drawRect(BREADTH/2, DEPTH/2, BREADTH, DEPTH);

    // Draw foam tile boundaries.
    final double TILE_WIDTH = (0.3048*12)/6; // Six tiles per 12 feet, in meters
    strokeWeight(1);
    stroke(100);
    for (int i = 1; i <= (int) DEPTH/TILE_WIDTH; i++) {
      double offset = i * TILE_WIDTH;
      drawLine(0, offset, BREADTH, offset); // Horizontal lines
    }
    for (int i = 1; i <= BREADTH/TILE_WIDTH; i++) {
      double offset = i * TILE_WIDTH;
      drawLine(offset, 0, offset, DEPTH); // Vertical lines
    }

    // draw field elements
    elements.draw();

    // Status
    fill(0);
    drawText(status, 0, 0, 0, 20);
    drawText(extendedStatus, BREADTH, DEPTH, 20, 0);
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
}
