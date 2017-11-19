void setup() {
  size(800, 800);
  colorMode(HSB, 360, 1, 500);
  int x = getColor();
  println("getColor returns " + x);

}


void draw() {
 }



// Place additional helper methods here.
public int getColor() {
  final int UNKNOWN = 0;
  final int RED = 1;
  final int BLUE = 2;

  float h = 100;
  float s = 1.0;
  float v = 300;

  background(h, s, v);
  
  if ((h<20 || h>350) && s>0.3 && v>10 && v<200) {
    return RED;
  } else if (h>170 && h<215 && s>0.3 && v>10 && v<200) {
    return BLUE;
  } else {
    return UNKNOWN;
  }
}