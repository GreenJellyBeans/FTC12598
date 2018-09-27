// The Trail class implements rendering of a robot trail, showing where the robot has been.
//
class Trail {

  private final int MAX_SIZE = 10000; // Max number of points to track

  class Point {
    double x;
    double y;
    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
  color c;
  Field f;
  ArrayList<Point> pointList = new ArrayList<Point>();


  public Trail(Field f, color c) {
    this.c = c;
    this.f = f;
  }


  public void addPoint(double x, double y) {

    if (pointList.size() < MAX_SIZE) {
      Point p = new Point(x, y);
      pointList.add(p);
    }
  }


  public void draw() {
    stroke(c);
    strokeWeight(1);
    for (Point p : pointList) {
      f.drawPoint(p.x, p.y);
    }
  }
}
