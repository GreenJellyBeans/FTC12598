// Class Robot implements a simulation of a ROBOT, for developing autonomous algorithms
// and potentially for drive practice.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
class Robot {

  final Field field; // Passed in during constructor - the field the robot operates within
  final ProcessingGamepad gamepad;
  final MeccanumDrive drive;
  final DriveTask driveTask;

  public Robot(Field f) {
    field = f;
    gamepad  = new ProcessingGamepad("Gamepad-F310");
    driveTask = new DriveTask(this);
    drive = new MeccanumDrive(field, field.WIDTH/2, field.WIDTH/2, 0);
  }


  public void init() {
    gamepad.init();
    driveTask.init();
  }


  public void deinit() {
  }


  public void start() {
    driveTask.start();
  }


  public void stop() {
    driveTask.stop();
    drive.markSpot();
  }


  public void loop(double t, double dT) {
    driveTask.loop();
  }


  public void simloop(double t, double dT) {
    drive.simloop(t, dT);
    field.updateStatus(String.format("t:% 7.3f  x:%1.2f  y:%1.2f  a:%1.2f LB:%s RB:%s", t, drive.x, drive.y, drive.a, gamepad.left_bumper(), gamepad.right_bumper()));
  }


  public void draw() {
    drive.draw();
  }
};
