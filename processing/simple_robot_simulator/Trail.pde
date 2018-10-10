// The Trail class implements rendering of a robot trail, showing where the robot has been.
//
class Trail {

  private final int MAX_SIZE = 100; // Max number of points to track
  color c;
  Field f;
  Point[] points = new Point[MAX_SIZE];
  int curIndex = 0;


  public Trail(Field f, color c) {
    this.c = c;
    this.f = f;
  }


  public void addPoint(double x, double y) {
    int prevIndex = curIndex > 0 ? curIndex -1 : points.length - 1;
    Point prevPoint = points[prevIndex];
    Point p = null;
    if (prevPoint == null) {
      p = new Point(x, y);
    } else if (distinctPoint(prevPoint.x, prevPoint.y, x, y)) {
      // We re-use points if possible...
      prevPoint.set(x, y);
      p = prevPoint;
    }
    if (p != null) {
      points[curIndex] = new Point(x, y);
      curIndex = (curIndex+1) % points.length;
    }
  }


  // Points are sufficiently visually separated to record in the trail.
  boolean distinctPoint(double x1, double y1, double x2, double y2) {
    return (Math.abs(x1-x2) + Math.abs(y1-y2)) > 10/g_field.PIX_PER_M;
  }


  public void draw() {
    stroke(c);
    strokeWeight(2);
    for (Point p : points) {
      if (p != null) {
        f.drawPoint(p.x, p.y);
      }
    }
  }
}
