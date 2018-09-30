// Class Robot implements a simulation of a ROBOT, for developing autonomous algorithms
// and potentially for drive practice.
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms.
class Robot {

  final Field field; // Passed in during constructor - the field the robot operates within
  final GamepadInterface gamepad;
  final MeccanumDrive drive;
  final DriveTask driveTask;

  public Robot(Field f, GamepadInterface gamepad) {
    field = f;
    this.gamepad  = gamepad;
    driveTask = new DriveTask(this);
    drive = new MeccanumDrive(field, field.WIDTH/2, field.WIDTH/2, 0);
  }


  public void init() {
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
    field.updateStatus(String.format("t:% 7.3f  x:%1.2f  y:%1.2f  a:%1.2f", t, drive.x, drive.y, drive.a));
  }


  public void draw() {
    displayGamepadStatus();
    drive.draw();
  }

  // Shows state of gamepad controls in the field's extended status area
  void displayGamepadStatus() {
    if (g_noGamepad) {
      field.addExtendedStatus("GAMEPAD OFF");
      return; // ******* EARLY RETURN
    }
    GamepadInterface gp = gamepad;
    field.addExtendedStatus(String.format("STICKS LSx: %5.2f  LSy: %5.2f  RSx: %5.2f  RSy: %5.2f", 
      gp.left_stick_x(), gp.left_stick_y(), gp.right_stick_x(), gp.right_stick_y()
      ));

    String buttons =  
      (gp.left_bumper()? " LB" : "") + 
      (gp.right_bumper()? " RB" : "") + 
      (gp.a()? " A" : "") + 
      (gp.y()? " Y" : "");
    String buttonStatus = (buttons.length() == 0) ? "BUTTONS: none" : "BUTTONS: " + buttons;
    field.addExtendedStatus(buttonStatus);
  }
};