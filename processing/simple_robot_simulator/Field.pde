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
  Point[] convexCorners = {}; // Corners between adjacent walls around any convex objects, if any. Initialized in makeWalls().
  final boolean visualizeCollisions = true; // set to true to display wall normal vectors and collision points for debugging
  final color collisionColor = color(0, 255, 0); // Collision visualizations have this color
  void init() {
    elements.load();
    makeWalls();
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
    for (int i = 1; i <= DEPTH/TILE_WIDTH; i++) {
      double offset = i * TILE_WIDTH;
      drawLine(0, offset, BREADTH, offset); // Horizontal lines
    }
    for (int i = 1; i <= BREADTH/TILE_WIDTH; i++) {
      double offset = i * TILE_WIDTH;
      drawLine(offset, 0, offset, DEPTH); // Vertical lines
    }

    // draw field elements
    elements.draw();

    // draw wall normals and convex corners
    if (visualizeCollisions) {
      visualizeWalls(walls);
      visualizeCorners(convexCorners);
    }

    // Status
    textAlign(LEFT);
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

  void makeWalls() {
    List<Wall> walls = new ArrayList<Wall>();
    List<Point> convexCorners = new ArrayList<Point>();

    // Add the boundary walls...
    double thickness = BREADTH; // Not critical what this as long as it is deep enough to stop the robot!
    walls.add(new Wall(BREADTH/2, 0, BREADTH, thickness, Math.PI/2, true)); // bottom
    walls.add(new Wall(BREADTH/2, DEPTH, BREADTH, thickness, -Math.PI/2, true)); //top
    walls.add(new Wall(0, DEPTH/2, DEPTH, thickness, 0, true)); //left
    walls.add(new Wall(BREADTH, DEPTH/2, DEPTH, thickness, -Math.PI, true)); //right

    // Process all blocks
    for (FieldElements.Element e : elements.fieldElements) {
      if (e.type == ElementType.BLOCK) {
        addWallsAndCornersFromBlock(walls, convexCorners, e);
      }
    }
    this.walls = walls.toArray(new Wall[walls.size()]);
    this.convexCorners = convexCorners.toArray(new Point[convexCorners.size()]);
  }

  void addWallsAndCornersFromBlock(List<Wall> walls, List<Point> corners, FieldElements.Element e) {
    assert(e.type == ElementType.BLOCK);
    double cx = e.x;
    double cy = e.y;
    double w = e.w;
    double h = e.h;
    double thickness = Math.min(w, h)/4; // Can't be too thick or it reaches and grabs robots from the other side!
    int wpos = walls.size();
    int cpos = corners.size();
    double angle = e.a;

    walls.add(new Wall(cx, cy + h/2, w, thickness, Math.PI/2, false)); // North facing
    walls.add(new Wall(cx + w/2, cy, h, thickness, 0, false)); // East facing
    walls.add(new Wall(cx, cy - h/2, w, thickness, -Math.PI/2, false)); // South facing
    walls.add(new Wall(cx - w/2, cy, h, thickness, -Math.PI, false)); // West facing

    corners.add(new Point(cx - w/2, cy - h/2));
    corners.add(new Point(cx - w/2, cy + h/2));
    corners.add(new Point(cx + w/2, cy - h/2));
    corners.add(new Point(cx + w/2, cy + h/2));

    for (int i = 0; i < 4; i++) {
      walls.get(wpos + i).rotate(angle, cx, cy);
    }

    for (int i = 0; i < 4; i++) {
      corners.get(cpos + i).rotate(angle, cx, cy);
    }
  }

  void visualizeWalls(Wall[] walls) {
    for (Wall w : walls) {
      stroke(collisionColor);
      strokeWeight(8);
      drawPoint(w.cx, w.cy);
      strokeWeight(2);
      double len = 0.2;
      drawLine(w.cx, w.cy, w.cx+len*w.nx, w.cy+len*w.ny);
    }
  }

  void visualizeCorners(Point[] corners) {
    fill(collisionColor);
    noStroke();
    for (Point p : corners) {
      drawCircle(p.x, p.y, 0.05);
    }
  }
}
