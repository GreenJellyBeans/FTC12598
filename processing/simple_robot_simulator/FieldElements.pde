//
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

  final String FILE_NAME = "field.txt"; // Should be under ./data
  final String VERSION = "1.0"; // Increment to invalidate cache

  class Element {
    ElementType type;
    boolean virtual; // true == not visible to robot
    String label;
    color c;
    double size;
    Point[] path;


    Element(ElementType type, boolean virtual, String label, color c, double size) {
      this.type = type;
      this.virtual = virtual;
      this.label = label;
      this.c = c;
      this.size = size;
      path = null;
    }

    // Append a compact text representation to {sb}.
    void appendTo(StringBuilder sb) {
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
      sb.append(String.format("%5.3f", size));

      // Append linear elements
      for (Point p : path) {
        sb.append(String.format("(%5.3f,%5.3f)", p.x, p.y));
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
    // Input are in feet and inches
    final double FEET_TO_METERS = 0.3048;
    final double INCHES_TO_METERS = FEET_TO_METERS/12;
    final double TAPE_WIDTH = 2*INCHES_TO_METERS;
    final double PATH_WIDTH = 0.5*INCHES_TO_METERS;
    final double MARK_SIZE = 4*INCHES_TO_METERS;

    String[] fieldObjects = g_pa.loadStrings(FILE_NAME);
    List<Element> elementList = new ArrayList<Element>();

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
      String sepChar = ">"; // Separator character
      if (shape.startsWith("redTape")) {
        e = new Element(ElementType.TAPE, false, label, color(255, 50, 50), TAPE_WIDTH);
      } else if (shape.startsWith("blueTape")) {
        e = new Element(ElementType.TAPE, false, label, color(50, 100, 255), TAPE_WIDTH);
      } else if (shape.startsWith("block")) {
        e = new Element(ElementType.BLOCK, false, label, color(160), 0);
        sepChar = "|";
      } else if (shape.startsWith("path")) {
        e = new Element(ElementType.PATH, true, label, color(0, 128), PATH_WIDTH);
      } else if (shape.startsWith("mark")) {
        e = new Element(ElementType.MARK, true, label, color(255, 255, 50), MARK_SIZE);
      }
      if (e != null && loadElementDetails(e, s, sepChar)) {
        elementList.add(e);
      }
    }  

    fieldElements = elementList.toArray(new Element[elementList.size()]);
  }


  // Adds a text signature of the state of the floor elements - this is to facilitate caching 
  // of the blurred pixel array, which takes a while
  void appendFloorSignature(StringBuilder sb) {
    sb.append("Version: " + VERSION);
    for (Element e : fieldElements) {
      e.appendTo(sb);
    }
  }


  // Render all field elements
  void draw() {

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


  private boolean loadElementDetails(Element e, String in, String sepChar) {
    // We expect somehing like this:
    // 1 2 > 3 4 > 5 6
    Scanner s = new Scanner(in);
    int i = 0;
    List<Point> path = new ArrayList<Point>();
    while (s.hasNext()) {
      double x = s.nextDouble(); 
      double y = s.nextDouble();
      //println ("(x,y) = " + x + " " + y);
      path.add(new Point(x*0.3048, y*0.3048));
      if (s.hasNext()) {
        String t = s.next();
        if (!t.equals(sepChar)) {
          println("Unexpected token parsing point #"+i);
          break;
        }
      }
      i++;
    }
    s.close();

    // We expect at least one point.
    if (path.size()<1) {
      println("shape " + e.type + " has no points");
      return false;
    }

    e.path = path.toArray(new Point[path.size()]);
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
    float weight = Math.max(field.pixLen(e.size), 1);
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
    Point p = e.path[0];
    fill(e.c);
    stroke(0);
    strokeWeight(2);
    field.drawCircle(p.x, p.y, e.size/2);
    stroke(2);
    field.drawPoint(p.x, p.y);
    fill(0);
    if (e.label.length()>0) {
      field.drawText(e.label, p.x, p.y, 10, 0);
    }
  }

  private void renderBlockElement(Element e) {
    // Draw a rectangle, add label
    Point p = e.path[0];
    Point dim = e.path[1];
    double w = dim.x;
    double h = dim.y;
    fill(e.c);
    noStroke();
    field.drawRect(p.x, p.y, w, h); // Assumes rectmode is CENTER
    fill(0);
    if (e.label.length()>0) {
      field.drawText(e.label, p.x, p.y, 10, 0);
    }
  }

  // Apply the {sp.process} method to each segment of Element {e}.
  private void processLinearSegment(Element e, SegmentProcessor sp) {
    boolean first = true;
    double xFirst = 0;
    double yFirst = 0;
    double xPrev = 0;
    double yPrev = 0;
    for (Point p : e.path) {
      // The first point is absolute; rest are relative
      if (first) {
        xFirst = xPrev = p.x;
        yFirst = yPrev = p.y;
        first = false;
      } else {
        double x  = xFirst + p.x;
        double y  = yFirst + p.y;
        sp.process(xPrev, yPrev, x, y);
        xPrev = x;
        yPrev = y;
      }
    }
  }


  // Version of renderFloorLinearElement that renders to the floor
  // PGraphics, which has different units than the scren, so we use
  // the floorXX methods.
  private void renderFloorLinearElement(final PGraphics pg, Element e) {
    float weight = Math.max(floorPixLen(e.size), 1);
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
}
