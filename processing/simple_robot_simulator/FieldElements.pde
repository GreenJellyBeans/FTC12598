// //<>// //<>// //<>//
// Class FieldElements loads and applies additional user-proved elements to the field.
//
import java.util.Scanner;
import java.util.List;

enum ElementType {
  // Elements visible to sensors on robot
  TAPE, 
    BLOCK, 

    // Virtual elements, invisible to robot
    MARK, // Marks a position
    PATH  // Marks a path
};

// Passed in as a parameter to method processLinearSegment
interface SegmentProcessor {
  void process(double xPrev, double yPrev, double x, double y);
};


class FieldElements {

  final String BASE_FILE_NAME = "field_base.txt"; // Should be under ./data
  final String EXTRAS_FILE_NAME = "field_extras.txt"; // Should be under ./data

  final String VERSION = "1.2"; // Increment to invalidate cache
  final double INCHES_TO_METERS = 0.3048/12;


  class Element {
    final ElementType type;
    final boolean virtual; // true == not visible to robot
    final String label;
    final color c;
    double x; // x position in meters
    double y; // y position in meters
    double w; // width in meters
    double h; // height in meters
    double a;  // angle or orientation in radians
    Point[] path; // May be null, points in meters


    Element(ElementType type, boolean virtual, String label, color c, double x, double y) {
      this.type = type;
      this.virtual = virtual;
      this.label = label;
      this.c = c;
      this.x = x;
      this.y = y;
      path = null;
    }


    // Append a compact text representation to {sb}.
    private void appendTo(StringBuilder sb) {
      char sp = ' ';
      sb.append("ELEMENT");
      sb.append(sp);
      sb.append(type);
      sb.append(sp);
      sb.append(virtual);
      sb.append(sp);
      sb.append(label);
      sb.append(sp);
      sb.append(c);
      sb.append(sp);
      sb.append(String.format("%6.4f", x));
      sb.append(String.format("%6.4f", y));
      sb.append(String.format("%6.4f", w));
      sb.append(String.format("%6.4f", h));
      sb.append(String.format("%6.4f", a));

      // Append linear elements
      if (path != null) {
        for (Point p : path) {
          sb.append(String.format("(%6.4f,%6.4f)", p.x, p.y));
        }
      }
      sb.append("\n");
    }
  }


  Field field;
  Element[] fieldElements = new Element[0];

  //
  // Visible floor elements are also rendered to a FLOOR_PIXEL_SIZE * FLOOR_PIXEL_SIZE
  // array of pixels that is accessable to the robot's simulated sensors.
  // The resolution of this pixel array is different from the screen, so we
  // need separate scaling and drawing functions, typically beginning with "floor".
  //
  final int FLOOR_PIXEL_BREADTH  = 1000; // This is the width of the PGraphics buffer into which the floor and field elements
  final int FLOOR_PIXEL_DEPTH; // This is the height of the PGraphics buffer into which the floor and field elements

  // are rendered
  final double FLOORPIX_PER_M; // Floor pixels per meter


  FieldElements(Field f) {
    field = f;
    FLOOR_PIXEL_DEPTH = (int) (FLOOR_PIXEL_BREADTH * f.DEPTH / f.BREADTH);
    FLOORPIX_PER_M = FLOOR_PIXEL_BREADTH / f.BREADTH; // floor pixels per meter
  }


  // Loads all field elements
  void load() {
    List<Element> elementList = new ArrayList<Element>();

    // Load the base elements that define the field - these change infrequently
    String[] fieldObjects = g_pa.loadStrings(BASE_FILE_NAME);
    appendLoad(elementList, fieldObjects);

    // Load optional extra objects that are typically annotations
    File extrasF = new File(sketchPath("data/" + EXTRAS_FILE_NAME));
    if (extrasF.exists()) {
      fieldObjects = g_pa.loadStrings(EXTRAS_FILE_NAME);
      appendLoad(elementList, fieldObjects);
    }
    fieldElements = elementList.toArray(new Element[elementList.size()]);

    // Save motion paths, if any, to a file
    saveMotionPaths();
  }

  // Add the elements represented by the text array {fieldObjects} to
  // the list of flements.
  private void appendLoad(List<Element> elementList, String[] fieldObjects) {
    // Input are in feet and inches
    final double TAPE_WIDTH = 2*INCHES_TO_METERS;
    final double FAT_TAPE_WIDTH = 5*INCHES_TO_METERS;
    final double PATH_WIDTH = 0.5*INCHES_TO_METERS;
    final double MARK_SIZE = 4*INCHES_TO_METERS;

    // Process elements
    for (String s : fieldObjects) {
      // Trim beginning and ending blanks and everything including and after #
      s = s.replace('\t', ' ');
      s = s.replaceFirst("#.*", "").trim();
      if (s.length() == 0) {
        continue;
      }
      // Extract shape name and label
      int i = s.indexOf(' ');
      String w;
      if (i>=0) {
        w = s.substring(0, i);
        s = s.substring(i+1);
      } else {
        w = s;
        s = "";
      }
      i = w.indexOf('.'); // First '.'
      String shape = w;
      String label = "";
      if (i>=0) {
        shape = w.substring(0, i);
        label = w.substring(i+1);
      }

      Element e = null;
      Scanner in = new Scanner(s);
      boolean noErrors = true;

      // Extract position - all shapes have a position
      double x = in.nextDouble() * INCHES_TO_METERS;
      double y = in.nextDouble()  * INCHES_TO_METERS;

      if (shape.startsWith("red_tape")) {
        e = new Element(ElementType.TAPE, false, label, color(255, 50, 50), x, y);
        e.w = TAPE_WIDTH;
        noErrors = loadPath(in, e);
      } else if (shape.startsWith("blue_tape")) {
        e = new Element(ElementType.TAPE, false, label, color(50, 100, 255), x, y);
        e.w = TAPE_WIDTH;
        noErrors = loadPath(in, e);
      } else if (shape.startsWith("fat_black_tape")) {
        e = new Element(ElementType.TAPE, false, label, color(30), x, y);
        e.w = FAT_TAPE_WIDTH;
        noErrors = loadPath(in, e);
      } else if (shape.startsWith("block")) {
        e = new Element(ElementType.BLOCK, false, label, color(160), x, y);
        noErrors = loadBlock(in, e);
      } else if (shape.startsWith("path")) {
        e = new Element(ElementType.PATH, true, label, color(0, 128), x, y);
        e.w = PATH_WIDTH;
        noErrors = loadPath(in, e);
      } else if (shape.startsWith("mark")) {
        e = new Element(ElementType.MARK, true, label, color(255, 128), x, y);
        e.w = e.h = MARK_SIZE;
      }
      if (e != null && noErrors) {
        elementList.add(e);
      }
      in.close();
    }
  }


  // Adds a text signature of the state of the floor elements - this is to facilitate caching 
  // of the blurred pixel array, which takes a while
  void appendFloorSignature(StringBuilder sb) {
    sb.append("Version: " + VERSION);
    for (Element e : fieldElements) {
      if (!e.virtual) {
        e.appendTo(sb);
      }
    }
  }


  // Render all field elements
  void draw() {

    // All text in elements is centered..
    textAlign(CENTER);
    for (Element e : fieldElements) {
      if (e.type == ElementType.TAPE) {
        renderLinearElement(e);
      } else if (e.type == ElementType.BLOCK) {
        renderBlockElement(e);
      } else if (e.type == ElementType.MARK) {
        renderMarkElement(e);
      } else if (e.type == ElementType.PATH) {
        renderLinearElement(e);
      }
    }
  }


  // Reads in an array of points from {in} into {e.path}. Assumes these input values are
  // in FEET and so does the conversion to METERS. Input is pairs of floating point numbers.
  // Each pair is separated by space and '>'
  private boolean loadPath(Scanner s, Element e) {
    // We expect something like this 
    // > 3 4 > 5 6
    int i = 0;
    List<Point> path = new ArrayList<Point>();
    while (s.hasNext()) {
      String t = s.next();
      if (!t.equals(">")) {
        println("Unexpected token parsing point #"+i);
        break;
      }
      double x = s.nextDouble(); 
      double y = s.nextDouble();
      //println ("(x,y) = " + x + " " + y);
      path.add(new Point(x*INCHES_TO_METERS, y*INCHES_TO_METERS));
      i++;
    }

    // We expect at least one point.
    if (path.size()<1) {
      println("shape " + e.type + " has no points");
      return false;
    }

    e.path = path.toArray(new Point[path.size()]);
    return true;
  }



  // Load parameters specific to blocks
  private boolean loadBlock(Scanner s, Element e) {
    // A block looks like this:
    // block.obsticle 4 4 | 1.92 1.92 | 90
    // Of this, only this remains in the scanner buffer:
    // | 1.92 1.92 | 90
    final String BAR = "|";
    String b = s.next();
    if (!b.equals(BAR)) return false; // EARLY RETURN
    double w = s.nextDouble();
    double h = s.nextDouble();
    b = s.next();
    if (!b.equals(BAR)) return false; // EARLY RETURN
    double rot = s.nextDouble();
    e.w = w * INCHES_TO_METERS;
    e.h = h * INCHES_TO_METERS;
    e.a = radians((float)rot);
    return true;
  }


  // Generate a pixel array that represents the the mat background and visible floor elements.
  // This is for input to any color sensor simulation
  PixelHelper generateFloorPixels() {
    PGraphics floorPG = createGraphics(FLOOR_PIXEL_BREADTH, FLOOR_PIXEL_DEPTH);
    floorPG.beginDraw();
    floorPG.background(field.MAT_COLOR);
    renderVisibleFloorElements(floorPG);
    floorPG.endDraw();
    floorPG.loadPixels();
    return  new PixelHelper(floorPG.pixels, FLOOR_PIXEL_BREADTH, FLOOR_PIXEL_DEPTH);
  }

  // Render just the elements that are visible to
  // floor-looking sensors
  private void renderVisibleFloorElements(PGraphics pg) {
    // For now we just render TAPE, which we assert is not virtual, i.e., is visible
    for (Element e : fieldElements) {
      if (e.type == ElementType.TAPE) {
        assert(!e.virtual);
        renderFloorLinearElement(pg, e);
      }
    }
  }


  // These consist of multiple line-segments
  private void renderLinearElement(Element e) {
    float weight = Math.max(field.pixLen(e.w), 1);
    stroke(e.c);
    strokeWeight(weight);
    // Note: Processing does not support Lambda expressions.
    // See https://github.com/processing/processing/wiki/Supported-Platforms#java-versions
    processLinearSegment(e, 
      new SegmentProcessor() {
      public void process(double xPrev, double yPrev, double x, double y) {
        field.drawLine(xPrev, yPrev, x, y);
      }
    }
    );
  }


  private void renderMarkElement(Element e) {
    // Draw a circle with a cross, and add label.
    fill(e.c);
    stroke(0);
    strokeWeight(2);
    field.drawCircle(e.x, e.y, e.w/2);
    stroke(2);
    field.drawPoint(e.x, e.y);
    drawLabel(e.label, e.x, e.y, 20);
  }

  private void renderBlockElement(Element e) {
    // Draw a rectangle, add label
    double angle = e.a;
    double w = e.w;
    double h = e.h;
    pushMatrix();
    translate(field.screenX(e.x), field.screenY(e.y));
    rotate(-(float)angle); // In Processing, rotate(-t) rotates the axes by t;
    fill(e.c);
    noStroke();
    rect(0, 0, field.pixLen(w), field.pixLen(h)); // Assumes rectmode is CENTER
    popMatrix();
    drawLabel(e.label, e.x, e.y, 0);
  }

  private void drawLabel(String text, double x, double y, int yPixOffset) {
    fill(0);
    if (text.length() > 0) {
      field.drawText(text, x, y, 0, yPixOffset);
    }
  }

  // Apply the {sp.process} method to each segment of Element {e}.
  private void processLinearSegment(Element e, SegmentProcessor sp) {
    double xFirst = 0;
    double yFirst = 0;
    double xPrev = 0;
    double yPrev = 0;
    xFirst = xPrev = e.x;
    yFirst = yPrev = e.y;
    for (Point p : e.path) {
      double x  = xFirst + p.x;
      double y  = yFirst + p.y;
      sp.process(xPrev, yPrev, x, y);
      xPrev = x;
      yPrev = y;
    }
  }


  // Version of renderFloorLinearElement that renders to the floor
  // PGraphics, which has different units than the scren, so we use
  // the floorXX methods.
  private void renderFloorLinearElement(final PGraphics pg, Element e) {
    float weight = Math.max(floorPixLen(e.w), 1);
    pg.stroke(e.c);
    pg.strokeWeight(weight);
    // Note: Processing does not support Lambda expressions.
    // See https://github.com/processing/processing/wiki/Supported-Platforms#java-versions
    processLinearSegment(e, 
      new SegmentProcessor() {
      public void process(double xPrev, double yPrev, double x, double y) {
        floorDrawLine(pg, xPrev, yPrev, x, y);
      }
    }
    );
  }

  // Converts the length in field units (meters) to floor pixels (not screen pixels)
  private float floorPixLen(double len) {
    return (float) (len * FLOORPIX_PER_M);
  }

  // Draws a line onto the floor PGraphics.
  // These are in field coordinate, so units are in meters.
  private void floorDrawLine(PGraphics pg, double x1, double y1, double x2, double y2) {
    pg.line(floorX(x1), floorY(y1), floorX(x2), floorY(y2));
  }


  // Floor coordinates (in pixels) of field cordinate {x} (in meters)
  private float floorX(double x) {
    return (float) (x * FLOORPIX_PER_M);
  }


  // Floor coordinates (in pixels) of field cordinate {y} (in meters)
  private float floorY(double y) {
    return (float) (FLOOR_PIXEL_DEPTH - y * FLOORPIX_PER_M); // y grows upwards, py grows downwards
  }


  // Generate and save the motion paths that correspond to
  // each path. 
  private void saveMotionPaths() {
    String outFile = "./data/motion_paths.txt";
    List<String> out = new ArrayList<String>();
    out.add("# Units: degrees and inches");
    for (Element e : fieldElements) {
      if (e.type == ElementType.PATH) {
        generateMotionPath(e, out);
      }
    }
    saveStrings(outFile, out.toArray(new String[out.size()]));
  }

  void generateMotionPath(Element e, List<String> out) {
    assert e.type == ElementType.PATH;
    double prevA = 0; // previous angle.
    out.add(""); //
    out.add("path." + e.label);
    double prevX = 0;
    double prevY = 0;
    
    for (int i = 0; i < e.path.length; i++) {
      Point p = e.path[i];
      double dx = p.x - prevX;
      double dy = p.y - prevY;
      println(String.format("x:%f  y:%f  a:%f", dx, dy, prevA)); 
      double dist = Math.sqrt(dx*dx + dy*dy);
      double angle = 0;
      if (dist > EPSILON_LENGTH) {
        // Pick arcCos or arcSin depending on which component is larger...
        angle = Math.abs(dx) > Math.abs(dy) ? Math.acos(dx/dist) : Math.asin(dy/dist);
      }
      out.add(String.format("  %02d: rot %6.2f,  mov %6.2f", i+1, degrees(balancedAngle(angle-prevA)), 12*feet(dist)));
      prevA = angle;
      prevX = p.x;
      prevY = p.y;
    }
  }
}
