/*
** This example illustrates class instances and methods
** After understanding the code, here are some things to try:
** 1. Create another class, called myCircle, that looks much like MyBox (you can copy the code), but
**    draws a circle (of some fixed diameter like 40), instead of a rectangle. Add code to instantiate one variable
**    with a MyCircle object, and then make that circle move too, by adding code to the draw() method.
**
** 2. Try to make each object a different color. There are several ways to do this. Experiment. 
**     Note that fill(r, g, b) is used set the color of all subsequently drawn shapes to a particular value of
**     read(r), green(g), and blue(b). So fill(255, 0, 0) sets subsequently drawn shapes to 0.
**    
** You can search the Internet for Processing help by simply typing things like "processing fill" or 
**  maybe "processing fill java". You'll get a description and samples. The help is on www.processing.org.
*/

// This is a class - a template or pattern - for how to create objects of type MyBox
class MyBox {
  
  // These are called "instance variables" - there is one copy of these variables for EACH object that is created.
  // So, if there are two MyBox objects, then each will have it's own copy of these variables.
  int x = 0;
  int y = 0;
  int r = 0;
  int g = 0;
  int b = 0;

  // Moves a particular box by the amount specified, but will
  // "roll over" if the box moves off screen.
  void moveBy(int x1, int y1) {
    x = (x + x1) % height; // height is the height of window - currently 800 (see setup() method below)
    y += y1;
    y = (y + y1) % width; // width is width of window
    
    if (x < 0) {
      x = width;
    }
    if (y < 0) {
      y = height;
    }
  }

  // Draws a specific box on screen
  void drawMe() {
    fill(r % 256, g % 256 , b % 256);
    rect(x, y, 20, 30);
  }
}



// Create 2 objects, both of type MyBox, and assign them to 2 variables
// (box1 and box2)
int i = 3;
MyBox box1 = new MyBox();
MyBox box2 = new MyBox();
MyBox box3 = new MyBox();

void setup() {
  size(800, 800);
  box3.x =  400;
  box3.y = 400;
  box1.r = 255;
  box2.g = 255;
  box3.b = 255;
}

// Remember that draw method is called many times per second
void draw() {
  // Uncomment the following line to remove the "trails" of past items drawn
  background(160, 32, 240);
  box1.drawMe(); // Draws one object -  box1
  box2.drawMe(); // Draws a different object - box2 this time
  box1.moveBy(2, -3); // Move box1 slightly
  box2.moveBy(-1,1);  // Move box2 slightly, but a DIFFERENT amount than box1
  
  box3.drawMe();
  box3.moveBy(4, 4);
}