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
  }


  public void draw() {
    drive.draw();
  }
};
