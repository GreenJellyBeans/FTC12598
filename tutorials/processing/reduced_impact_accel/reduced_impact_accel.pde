/*
* Exploration of minimizing acceleration to reduce impact when start/stop/changing directions
 */
double x;
double y;
long previous = System.nanoTime();
final double NANOS_PER_SECOND = 1e9;
double speed  = 1.0;
double  maxAcceleration= 0.0; // Magnetude.
String displayText = "";
GJBPowerController controller = new GJBPowerController(0.1, 0.05, 0.2); // start, min-increase, min-decrease

void setup() {
  size(800, 800);
  x = width/2;
  y = height/2;
  textSize(32);
}

void draw() {
  //background(128);
  double delta = getTimeDelta();
  updateSpeed(delta);
  moveDisk(delta);
  drawDisk();

  if (frameCount % 3 == 0) {
    fill(128);
    rect(0, 0, width, 150);
    fill(255);
    displayText = "speed: " + speed + "\nmaxA: " + maxAcceleration;
  }
  if (frameCount % 30 == 0) {
    maxAcceleration = 0;
  }
  text(displayText, 50, 50);
}

double previousTargetSpeed = 0.0;

void updateSpeed(double delta) {
  final double MAX_SPEED = 1.0;
  double targetSpeed = speed;
  if (keyPressed) {
    if (keyCode == UP) {
      targetSpeed = MAX_SPEED;
    } else if (keyCode == DOWN) {
      targetSpeed = -MAX_SPEED;
    }
  } else {
    targetSpeed = 0.0;
  }
  if (Math.abs(targetSpeed-previousTargetSpeed) > 0.1) {
    line(0, (float)y, width, (float)y);
    previousTargetSpeed = targetSpeed;
  }

  double speedDelta = controller.calculatePowerIncrement(speed, targetSpeed);
  double newSpeed = speed + speedDelta;
  double acceleration = Math.abs((newSpeed-speed)/delta);
  maxAcceleration = Math.max(maxAcceleration, acceleration);
  speed = newSpeed;
}





void drawDisk() {
  ellipse((float)x, (float)y, 30, 30);
}


boolean moving = false;
void moveDisk(double delta) {
  final double SPEED_TO_PIXELS = 200;
  double dy = -speed*delta*SPEED_TO_PIXELS; // -ve because it's negative to go UP.
  moving =  Math.abs(speed)>0.001;
  if (moving || speed > 0.25) {
    y += dy;
  }
}


double getTimeDelta() {
  long now = System.nanoTime();
  double delta =  (now-previous)/NANOS_PER_SECOND;
  previous = now;
  return delta;
}