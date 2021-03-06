# A Simple Robot Simulator
This is a Processing sketch that implements a simple 2D physics-based simulation of the drive
aspect of one or more robots on a rectangular field. It is designed for learning robotics concepts
and for prototyping autonomous code by FTC (and potentially FRC) teams.
It may also be useful for new driver practice and for developing game strategy.

This code is in the public domain; all code is free to be used for any purpose without
attribution, with the usual disclaimers. Code license is here: https://github.com/GreenJellyBeans/FTC12598/blob/master/LICENSE.

## Overview
The program supports both autonomous and driver controlled robots. Both modes are controlled by
robot code somewhat similar to the FIRST Java programming model in FTC and FRC, in that there
are `init` and `loop` methods that can query (limited) sensor information and set the power of 
four drive motors. There is also a "linear" mode that supports implementation of autonomous
logic as a sequence of blocking steps.

The physics calculations assume a mecanum drive, though the code structure
does not preclude other kinds of drives being introduced in the future. Multiple gamepads are
supported, that can be dynamically mapped to robots using special joystick button combinations
(including the START+A command familiar to
FTC drivers). A screenshot of the program is shown below.

![screenshot](media/intro_screenshot1.PNG)

More features:
- The physics simulation, while simple, is "pure" in the sense that only forces are directly
calculated. A robot's position and velocity are never set directly except in the very
beginning, and instead is determined by acceleration produced by various forces,
including friction and collision forces.
The physical model of the robot is very simple: a square rigid object with a fixed
mass and rotational inertia. It is "powered" by diagonal forces acting on its four corners - the
simulation of mecanum wheels. Various simplifying assumptions are made when calculating the net
force and torque on the robot - most of this logic is implemented in classes  `MecanumDrive` 
and `CollisionPhysics`.
- The field is configurable - its makeup is defined in the files `data/field_base.txt` and
  `data/field_extras.txt`. (Thanks to the Green Jellybeans team members for
  creating the field elements that represent the 2018-19 FTC Rover Ruckus competition.)
- Colored tape on the field can be picked up by sensors on the robot. The sensors read a "blurry"
  version of the field, representing the average over a certain field of view (this is the constant
  `BLUR_RADIUS` in class `SensorModule`). This is to develop and test autonomous code that
  reads the tape markings.
- One supported field element is a `block` - a rectangular immovable object. The lander
 in the center of the field is implemented as a block. Robots cannot penetrate blocks (well,
 there is a glitch in the simulation that allows penetration under certain circumstances...).
 The main purpose of blocks (as well as simulation of collisions with walls) is to be able to
 develop code that relies on bumping into or backing into flat surfaces to properly align the robot.
- Simulated floor-facing color sensor output, IMU bearing and wheel encoder values are available.
 Collectively, these features are
 designed to help early development of autonomous algorithms such as line following and PID code for driving
 in a straight line for a particular distance.
- Random perturbations make the robot behave slightly, well, randomly. This is to better
simulate real-world conditions. See the comments next to constants `RobotProperties.PERTUBRATION_*`
and methods `MecanumDrive.perturb*` for details. This is a newly-introduced feature and will
be extended to add randomness to encoder readings, color sensor readings, and position sensor
readings.

## About Processing
This program uses the Processing environment.
Processing is a wonderful interactive programming environment that lets one write
programs in Java (and some other languages) with minimal overhead. It has a simple IDE, and a vibrant
community has added many useful libraries. While it is possible to package a Processing
sketch into a stand-alone program, this robot simulator is designed to remain as Java
code, any aspect of which can be modified.

If you have never used Processing before, it may be worth it to take a detour and learn a bit
more about the environment. Learn more at www.processing.org.

_Note to Java programmers new to Processing_: All the tabs that make up a Processing Sketch are
concatenated and enclosed in a single, hidden, top-level class. This top-level class extends
built-in Processing class `processing.core.PApplet`.
Thus all the classes you see in the robot simulator are actually
inner classes of the top-level class, and have access to all its methods and instance variables
without any qualification. Thus, if you see a call to method `draw(...)`, this is a call
to the containing classes' method. This can be confusing the first time you come across it, but
it makes for less cluttered code. For more details, visit https://www.processing.org/reference/environment/#Sketchbook. Also, Processing does not currently support Java 8 language features such as
lambda expressions.

## Installing Processing and Required Libraries

The Processing IDE can be downloaded from www.processing.org. It is a stand-alone installation;
just unzip it and move it into a folder and run the Processing application from that folder.
No other RTEs or SDKs are required to run Processing - it has it's own complete Java implementation
and does not use any Java implementation you may have on your machine.

Processing supports its own set of optional libraries that may be loaded into its environment. The
robot simulator requires the following libraries: "Game Control Plus" - for gamepad integration,
and "G4P" for configuring new game controllers. 
(Currently the only gamepad controller supported by the robot simulator is the
Logitec Gamepad F310 - though it seems easy to add other controllers using the Game
Control Plus library and supporting UX. See http://lagers.org.uk/gamecontrol/)


To load these libraries into Processing, select the `Tools > Add Tool` menu,
then select the `Libraries` tab, scroll down until you see "G4P" and "Game Control Plus", select those
and press the `Install` button.


## Running the Robot Simulator and Binding Gamepads

To run the robot simulator, just click on `simple_robot_simuator.pde` and then click on the play icon.
The first time it is run, the program will take a while before rendering the field, because it is 
computing a "blurry" version of the floor that is used by the simulated color sensors. This
blurry version is saved as the file `data/cache/blurryFloor.png` and subsequent runs of the program will
start much quicker.

To bind one or more gamepads, edit the `data/config.txt` file - set `numGamepads` to the number
of Logitec F310 gamepads connected (unfortunately the program does not yet dynamically detect
the number of attached compatible gamepads). To bind a gamepad to robot "1" as role "A", press
the DPad `Up` key while pressing the `A` button. To bind a gamepad to robot "2" as role "A", press
the DPad `Left` key while pressing the `A` button. More details are provided in
design note "September 29, 2018-A" titled "Thoughts on Multiple Gamepad mapping to multiple robots"
in file `NOTES.md`.

## Defining Multiple Robots and their Individual Run-time Logic
Examine the code in `simple_robot_simulator.pde`. This is the "top-level" code that,
as mentioned previously, is implicitly enclosed in a containing class. The program uses
three global arrays. Array `g_robots` is a global array of robots. Array `g_iterativeOpModes` defines the list
of "iterative" op modes (FTC lingo), and array `g_linearOpModes` defines the list of "linear" op modes. Each op mode 
controls the behavior of a single robot for the duration of the program, so there is a
one-to-one mapping between robots and op modes.  The robots and op modes and their mapping are defined in method
`setup_robots` located file `setup_robots.pde`. Here is a sample `setup_robots` method:

```
void setup_robots() {
  // Create two robots, with their own names, colors, and initial position and orientation
  // Name choices are: ROBOT_[1-4].

  g_robots = new Robot[]{
    newRobot(ROBOT_1, color(0, 255, 0), g_field.BREADTH/2-0.5, g_field.DEPTH/2-0.5, radians(180)),
    newRobot(ROBOT_2, color(255, 255, 0), g_field.BREADTH/2+.45, g_field.DEPTH/2+0.45, radians(-135))
  };

  //
  // Setup each robot's op modes
  //

  // Iterative op modes
  g_iterativeOpModes = new IterativeOpMode[]{
    new DriveStraightOpMode(g_robots[0])
  };

  // Linear op modes
  g_linearOpModes = new LinearOpMode[]{
    new AOpMode_Forward_and_turn(g_robots[1])
  };
}

```

## A Sample Iterative Op Mode
An iterative op mode is created by defining a class that extends abstract class `IterativeOpMode`. Class `SampleIterativeOpMode`
provides an example of an op mode that implements "driver controlled" (AKA "teleop") logic. After initialization,
the simulator repeatedly calls the `loop` method to control the robot, and the method should not block.
Here is a code snippet from the class.

```
static class SampleIterativeOpMode extends IterativeOpMode {
  final Robot robot;
  boolean gamepadEnabled = false; // Stays disabled until "A" button is pressed


  SampleIterativeOpMode(Robot r) {
    this.robot = r;
  }


  @Override
    public void loop() {
    GamepadInterface gp = robot.gamepad1;
    if (!gamepadEnabled && gp.a()) {
      gamepadEnabled = true;
    }

    if (!gamepadEnabled) {
      return; // ***** EARLY RETURN ******
    }
    // If "Y" button is pressed, we clear the encoder values on all motors.
    if (gp.y()) {
      robot.base.resetEncoders();
    }

    if (gp.right_bumper()) {         //right bumper makes the robot spin clockwise
      robot.base.setMotorPowerAll(0.5, -0.5, 0.5, -0.5); // FL FR BL BR
    } else if (gp.left_bumper()) {    //left bumper makes the robot spin counterclockwise
      robot.base.setMotorPowerAll(-0.5, 0.5, -0.5, 0.5);
    } else {
      double fwd  = gp.right_stick_y();
      double turn  = gp.left_stick_x();
      double strafe = gp.right_stick_x();
      setHybridPower(fwd, turn, strafe);
    }
  }


  void setStartingPower() {
    double pFwd = 0;//0.5;
    double pStrafe = 0;//0.5;
    double pTurn = 0.3;
    double pFL = (pFwd + pStrafe + pTurn);
    double pFR = (pFwd - pStrafe - pTurn);
    double pBL = (pFwd - pStrafe + pTurn);
    double pBR = (pFwd + pStrafe - pTurn);
    robot.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
  }


  // Sets the power to each of the 4 motors of the mecanum drive given
  // the incoming request to go forward, turn and strafe by amounts
  // ranging within [-1, 1]
  void setHybridPower(double fwd, double turn, double strafe) {
    // Let's clip anyways, incase we get faulty input
    fwd = clipInput(fwd);
    turn = 0.5*clipInput(turn);
    strafe = clipInput(strafe);

    Field f = robot.field;
    f.addExtendedStatus(String.format("HPOWER  fwd:%5.2f  turn:%5.2f  strafe:%5.2f", fwd, turn, strafe));

    // Note: +ve strafe makes the robot go right, and with
    // the robot's front facing increasing x, to go right
    // means to go in the direction of decreasing y:
    //
    //                 ^ y-axis
    //      robot      |
    //    ...... FL    |
    //    .    .       --> x-axis
    //    ...... FR
    //
    double pFL = fwd - strafe + turn;
    double pFR = fwd + strafe - turn;
    double pBL = fwd + strafe + turn;
    double pBR = fwd - strafe - turn;

    // m is the max absolute value of the individual motor power amounts. If it is too small, we stop all motors.
    double m = Math.max(Math.max(Math.abs(pFL), Math.abs(pFR)), Math.max(Math.abs(pBL), Math.abs(pBR)));
    if (m<0.1) {
      robot.base.setMotorPowerAll(0, 0, 0, 0);
    } else {
      // Scale everything so no magnitude exeeds 1
      double scale = Math.min(1/m, 1);
      pFL *= scale;
      pFR *= scale;
      pBL *= scale;
      pBR *= scale;
      robot.base.setMotorPowerAll(pFL, pFR, pBL, pBR);
    }
  }
  ... (code elided)
 }
```

## A Sample Linear Op Mode
A linear op mode is created by defining a class that extends abstract class `LinearOpMode`. Class `SampleLinearOpMode`
provides an example of an op mode that  implements autonomous logic. In stark contrast to iterative op modes,
the entire execution of robot logic in a linear op mode is contained in a call to its `runOpMode` method.
Here is a code snippet from the class.

```
static class SampleLinearOpMode extends LinearOpMode {
  final Robot robot;


  SampleLinearOpMode(Robot r) {
    this.robot = r;
  }


  @Override
    public void runOpMode() {
      setStartingPower();
      long startMs = System.currentTimeMillis();
      while (opModeIsActive() && (System.currentTimeMillis() - startMs) < 3000) {
        // Do nothing
      }
      robot.base.setMotorPowerAll(0, 0, 0, 0);
    }
  ... (code elided)
}
```
The `runOpMode` method (or methods it calls) can execute a sequence of "busy loops" to wait for some condition to be true,
but when doing so it MUST call special inhereted method `opModeIsAcive` to share processor cycles with the rest of the system,
including the physics engine and animation threads, and the op modes running on other robots.
This is designed to be similar to the linear op modes in the FIRST FTC SDK.

# Simulated Sensors
Class `SensorModule` collects together various simulated sensors. It currently provides floor-facing color sensor output,
IMU bearing, and encoder readings from the four corners of the robot. The encoder readings are
calculated assuming mecanum wheels, and assuming zero wheel slippage.

# For More Information...

The `NOTES.md` file is an informal design and implementation log that charts the
development course of this project. The code is reasonably well commented and one should
feel free to alter any of the code and explore the changes in behavior. 
