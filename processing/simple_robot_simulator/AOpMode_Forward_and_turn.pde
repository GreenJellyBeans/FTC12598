static class AOpMode_Forward_and_turn extends LinearOpMode {
  final Robot robot;


  AOpMode_Forward_and_turn(Robot r) {
    this.robot = r;
  }


  @Override
    public void runOpMode() {
      
   MecanumWizard mw = new MecanumWizard(this, robot);

    mw.encoderDriveMec(0.5, 1.3);
    mw.encoderStrafeMecInches(-0.5, -2);

    mw.imuBearingMec(0.5, -135, 10000);

    mw.encoderDriveMec(0.5, 2.25);


    robot.base.setMotorPowerAll(0, 0, 0, 0);
    System.out.println("im completely done");
  }



}
