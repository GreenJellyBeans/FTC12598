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

  if (frameCount % 30 == 0) {
    fill(128);
    rect(0, 0, width, 150);
    fill(255);
    displayText = "speed: " + speed + "\nmaxA: " + maxAcceleration;
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

  double speedDelta = calculatePowerIncrement(speed, targetSpeed, 0.1, 1);
  double newSpeed = speed + speedDelta;
  double acceleration = Math.abs((newSpeed-speed)/delta);
  maxAcceleration = Math.max(maxAcceleration, acceleration);
  speed = newSpeed;
}

double clipToRange(double in, double min, double max) {
  return Math.max(Math.min(in, max), min);
}


double calculatePowerIncrement(double currentPower, double targetPower, double minStartPower, double maxPower) {
  final double MAX_INCREASE = 0.01; // The fractional amount of maxPower we increase each time
  final double MAX_DECREASE = 0.2;
  double speedDelta = targetPower-currentPower;
  double maxIncrement = maxPower*MAX_INCREASE;
  double absCurrentPower = Math.abs(currentPower); 
  double absTargetPower  = Math.abs(targetPower);
  boolean powerIsIncreasing = absTargetPower > absCurrentPower;
  if (powerIsIncreasing) {
    if (absCurrentPower < minStartPower) {
      // if power is very low - below minStartPower, we jump to minStartPower
      maxIncrement = minStartPower;
    } else {
      maxIncrement = maxPower*MAX_INCREASE;
    }
  } else {
    maxIncrement = maxPower*MAX_DECREASE;
  }

  // This makes sure we increment the right amount even if speedDelta is negative.
  speedDelta = clipToRange(speedDelta, -maxIncrement, maxIncrement);

  return speedDelta;
}

double calculatePowerIncrement2(double currentPower, double targetPower, double minStartPower, double maxPower) {
  double speedDelta = targetPower-currentPower;
  final double MAX_INCREASE = 0.2; // The fractional amount of maxPower we increase each time
  final double MAX_LOWPOWER_INCREASE = 0.01; // When power is low, the fractional amount of maxPower we increase each time.
  double maxIncrement = maxPower*MAX_INCREASE;
  double absPower = Math.abs(currentPower);

  if (absPower < minStartPower) {
    // if power is very low - below minStartPower, we jump to minStartPower
    maxIncrement = minStartPower;
  } else if (absPower >= minStartPower && absPower < minStartPower + 0.1*maxPower) {
    // if power is low - between minStartPower and minStartPower + 0.1 maxPower,
    // we increment by at most a very small amount
    maxIncrement = maxPower*MAX_LOWPOWER_INCREASE;
  } else {
    // increment by at most the normal amount
    maxIncrement = maxPower*MAX_INCREASE;
  }

  // This makes sure we increment the right amount even if speedDelta is negative.
  speedDelta = clipToRange(speedDelta, -maxIncrement, maxIncrement);

  return speedDelta;
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