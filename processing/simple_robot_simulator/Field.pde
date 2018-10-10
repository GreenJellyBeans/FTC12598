//
// The Field class implements rendering of the robot field, including display of robot stats.
//
class Field {

  public final double BREADTH = 12*12*0.0254; // field width in meters (12 ft).; // in meters
  public final double DEPTH = BREADTH;  // field depth in meters (along the y axis)
  public final color MAT_COLOR = 128; // Color of the mat.
  final int FIELD_OFFSET_PIXELS = 50; // Offset from origin (top-left)
  final int FIELD_DEPTH_PIXELS = height - 2 * FIELD_OFFSET_PIXELS;
  final double PIX_PER_M = FIELD_DEPTH_PIXELS / DEPTH; // pixels per meter
  String status = "NOTHING"; // Single line of status printed below the field.
  String extendedStatus ="NOT\nONE\nTHING"; // Multiline status printed to right of field
  final FieldElements elements = new FieldElements(this); 
  Wall[] walls; // initialized in init.

  void init() {
    elements.load();
    walls = makeWalls();
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

  Wall[] makeWalls() {
    List<Wall> walls = new ArrayList<Wall>();

    // Add the boundary walls...
    double thickness = BREADTH; // Not critical what this as long as it is deep enough to stop the robot!
    walls.add(new Wall(BREADTH/2, 0, BREADTH, thickness, Math.PI/2)); // bottom
    walls.add(new Wall(BREADTH/2, DEPTH, BREADTH, thickness, -Math.PI/2)); //top
    walls.add(new Wall(0, DEPTH/2, DEPTH, thickness, 0)); //left
    walls.add(new Wall(BREADTH, DEPTH/2, DEPTH, thickness, -Math.PI)); //right

    // Process all blocks
    for (FieldElements.Element e : elements.fieldElements) {
      if (e.type == ElementType.BLOCK) {
        addWallsFromBlock(walls, e);
      }
    }
    return walls.toArray(new Wall[walls.size()]);
  }

  void addWallsFromBlock(List<Wall> walls, FieldElements.Element e) {
    assert(e.type == ElementType.BLOCK);
    assert(e.path.length == 2);
    Point p1 = e.path[0]; // center
    Point p2 = e.path[1]; // dimensions
    double cx = p1.x;
    double cy = p1.y;
    double w = p2.x;
    double h = p2.y;
    double thickness = Math.min(w, h)/20; // Can't be too thick or it reaches and grabs robots from the other side!
    walls.add(new Wall(cx, cy + h/2, w, thickness, Math.PI/2)); // North facing - OK
    walls.add(new Wall(cx + w/2, cy, h, thickness, 0)); // East facing  - OK
    walls.add(new Wall(cx, cy - h/2, w, thickness, -Math.PI/2)); // South facing - OK
    walls.add(new Wall(cx - w/2, cy, h, thickness, -Math.PI)); // West facing
  }
}
