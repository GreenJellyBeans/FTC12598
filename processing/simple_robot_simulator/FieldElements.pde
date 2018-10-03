//
// Class FieldElements loads and applies additional user-proved elements to the field.
//
import java.util.Scanner;
import java.util.List;

enum ElementType {
  // Elements visible to sensors on robot
  TAPE, 

    // Virtual elements, invisible to robot
    MARK, // Marks a position
    PATH  // Marks a path
};

class FieldElements {

  final String FILE_NAME = "field.txt"; // Should be under ./data


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
  }


  Field field;
  Element[] fieldElements = new Element[0];


  FieldElements(Field f) {
    field = f;
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
      if (shape.startsWith("redTape")) {
        e = new Element(ElementType.TAPE, false, label, color(255, 50, 50), TAPE_WIDTH);
      } else if (shape.startsWith("blueTape")) {
        e = new Element(ElementType.TAPE, false, label, color(50, 100, 255), TAPE_WIDTH);
      } else if (shape.startsWith("path")) {
        e = new Element(ElementType.PATH, true, label, color(0, 128), PATH_WIDTH);
      } else if (shape.startsWith("mark")) {
        e = new Element(ElementType.MARK, true, label, color(255, 255, 50), MARK_SIZE);
      }
      if (e != null && loadElementDetails(e, s)) {
        elementList.add(e);
      }
    }  
    
    fieldElements = elementList.toArray(new Element[elementList.size()]);
  }


  boolean loadElementDetails(Element e, String in) {
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
        if (!t.equals(">")) {
          println("Unexpected token parsing point #"+i);
          break;
        }
      }
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


  // Render all field elements
  void draw() {
    for (Element e : fieldElements) {
      if (e.type == ElementType.TAPE) {
        renderLinearElement(e);
      } else if (e.type == ElementType.MARK) {
        renderMarkElement(e);
      } else if (e.type == ElementType.PATH) {
        renderLinearElement(e);
      }
    }
  }


  // Calculate and return the area of overlap between Element {e} and a disk
  // of diameter {dia} centered at ({x}, {y})
  double getOverlapArea(Element e, double x, double y, double dia) {

    // Virtual elements like paths do not overlap with anything
    if (e.virtual) {
      return 0; // ******* EARLY RETURN *********
    }

    // For now, we handle only tape.
    assert(e.type == ElementType.TAPE);


    return 0;
  }

  // These consist of multiple line-segments
  void renderLinearElement(Element e) {
    float weight = Math.max(field.pixLen(e.size), 1);
    stroke(e.c);
    strokeWeight(weight);
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
        field.drawLine(xPrev, yPrev, x, y);
        xPrev = x;
        yPrev = y;
      }
    }
  }


  void renderMarkElement(Element e) {
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
}
