# Design and Implementation Notes
This document contains an informal log of design and implementation decisions for this project,
the "Simple Robot Simulator."

## November 25, 2018-A JMJ Generating a motion path from a field element path
Field element {path} contains the coordinates of a single path. This is not directly
useful to program a robot path, because the robot needs relative rotations and movements.
The proposal is to write out robot "motion paths" for each path in the file. The motion
path would be series of text commands like so:

```
path.P1
  01: rot   0.00, mov   6.00
  02: rot  80.54, mov  36.50
  03: rot -80.54, mov  18.00

```
Where `rot 80.54` means "rotate CCW by 80.54 degrees" and `mov 6.0` means "go forward by 6 inches".
The idea is that this information can be used to write robot code.
These motion paths are written out to file `./data/out/motion_paths.txt` on load.
IMPORTANT: The starting orientation is ASSUMED to be 0 degrees - i.e., aligned with the
x-axis and facing to the right of field. The starting position is the first (and absolute)
coordinate in the path. Units are degrees and inches.

The implementation is in `FieldElements.saveMotionPaths`, which  calls
`generateMotionPaths` for each path. The motion paths file is
re-generated each time the program is run. It does not need to be checked in.

## November 16, 2018-A JMJ Adding complex condition "detector" to SensorModule
This is a very crude approximation of the kind of conditions detected by computer
vision systems like Vuforia VuMarks and Tensor Flow or by combining sensors to
detect collision.  Added a method
`char complex_condition(String options, char default)` to `SensorModule`.
For example: `char c = sm.complex_condition("LCR", '-');` would query
the state of the keyboard and if at that instant either 'L', 'C', or 'R' is
pressed it will return that character, else it will return '-'.

This can be used to simulate the Tensor Flow detection of the gold sampling
mineral in the 2018 Rover Ruckus FTC challenge. Similarly, it could be used
to detect events such as collision detection events, or general conditions 
like a particular VuMark being identified.


## November 2, 2018-A JMJ Changed field linear units from feet to inches
Inches are easier to work with on the field.

## October 29, 2018-B JMJ Added imu and encoder methods to RawSensorModule
Currently: `imu_reset`, `imu_bearing`, `encoder_resetAll`, `encoder_setScale`,
`encoder_FL`, `encoder_FR`, `encoder_BL`, `encoder_BR`.
Also renamed `sred`, etc, to `color_red`. The convention is to
prefix the methods by the sensor type. This is lighter-weight than
defining classes for each type of sensor. Also, for now, encoder
information is located in the base..


## October 29, 2018-A JMJ Robot and task setup is now in setup_robots.pde
This way, folks don't have to be faced with the clutter in `simple_robot_simulator.pde`

## October 27, 2018-D JMJ Completed switch over from Tasks to OpModes
Removed the Task interface, and moved the one under-development task to an `IterativeOpMode` and
deleted the `SampleDriveTask`.

## October 27, 2018-C JMJ Milestone: Sample linear and iterative op modes implemented!
The old sample iterative code that lives in `SampleDriveTask` has been ported over to
new class `SampleIterativeOpMode`. New sample code `SampleLinearOpMode` has been added - 
currently it makes the robot drive for a few seconds. Two robots are instantiated, 
one with each sample op mode. All works great!

There is a lot of console spew from `RoundRobinScheduler` - need to move all those 
log messages to trace messages.

## October 27, 2018-B JMJ Milestone: Linear and Iterative op modes implemented!
With this checkin, both Linear (the far more complicated) and Iterative op modes
are working. Not extensively tested, but basic functionality is verified: the test
code instantiates one of each type of op mode, and each type goes through multiple steps.

## October 27, 2018-A JMJ Back to implementing OpModes...
There are three op-mode classes:
`OpModeManager` - for registering op modes. It provides these methods:
	`registerLinearOpMode`
	`registerIterativeOpMode`
	`runAll`
	`loopAll`

`LinearOpMode` - Abstract class for linear (blocking) op-modes. It provides these methods:
	`runOpMode` - abstract - must be implemented.
	`opModeIsActive` - final - for sharing processing cycles and determining termination

`IterativeOpMode` - Abstract class for iterative op-modes. It provides these methods:
	`init`
	`deinit`
	`start`
	`stop`
	`loop`

Implementation notes:
- `LinearOpMode` extends `RoundRobinScheduler.Task`, and instances of `LinearOpMode` are
   actually managed by `RoundRobinScheduler`
- `LinearOpMode` and `IterativeOpMode` do not share a common parent, as they really do not
   have anything in common.

Skeleton code for all of this is part of this checkin, which replaces the old `OpMode` class.

## October 26, 2018-A JMJ Round Robin Scheduler implemented in another repository...
The Round Robin Scheduler turned out to be quite complex to implement, and I have implemented
it in another repository - where the main version resides, periodically copying that
implementation over to this folder. The master implementation is here:
 `https://github.com/josephmjoy/robotics/tree/master/processing/roundrobin_scheduler`.
Here is the outline of the class.

```
static class RoundRobinScheduler {
  // Supplies context to a client-provided  task
  public interface TaskContext {
    public String name();

    // Worker side: blocks until next step can be done.
    // Return:  true if there will be more steps, false
    //     if this is the final step (so cleanup may be performed).
    // If an exception is thrown, the caller MUST NOT attempt to
    // wait for any more steps.
    // Once false is returned, a subsequent call to this method
    // WILL result in an InterruptedException being thrown.
    public boolean waitForNextStep() throws InterruptedException;
  }

  // Interface to client-provided task - similar to java.lang.Runnable,
  // but with the added context.
  public interface  Task {
    public  abstract void run(TaskContext context);
  }

  public RoundRobinScheduler() {}


  // Adds a task with the given name to the list of
  // round-robin tasks. The name is to help with
  // debugging and logging. The task will be added to
  // the end of the list of tasks.
  // MUST be called from the (single) main 
  // thread, which is the thread that created the
  // RoundRobinScheduler.
  // MUST NOT be called after task rundown has started (via
  // call to rundownAll)
  public void addTask(Task task, String name) {}


  // Steps once through all tasks. This is a blocking
  // call as it will wait until each task goes through 
  // one step.
  // MUST be called from the (single) main thread.
  // MUST NOT be called after task rundown has started (via
  // call to rundownAll)
  public void stepAll() {}


  // Cancels all tasks.
  // MUST be called from the single main thread.
  // MUST NOT be called after task rundown has started (via
  // call to rundownAll)
  public void cancelAll() {}


  // Waits for all tasks to complete OR until
  // {waitMs} milliseconds transpires. No more
  // tasks must be added after this method is called.
  // MUST be called from the single main thread.
  // MUST be called only once.
  // Return: true if all tasks have completed.
  //   false if an InterruptedException was thrown, typically
  // indicating the timeout has expired.
  public boolean rundownAll(int waitMs) {} 

}
```

Along for the ride is a simple logging implementation called `SimpleLogger` that
logs messages to the console.

## October 23, 2018-A JMJ A Round Robin Scheduler
Each OpMode instance runs in a separate thread but needs to be serialized with each other and
the main Processing animation thread (see previous note). Tackling this problem head-on,
I decided to implement a round robin scheduler with it's self-contained interfaces that are
not specific to the robot simulator so that it may be used else where. It proceeds in a very
predictable way - giving each task a turn to run.

Here's a snapshot of design, in the form of a skeleton of the classes and interfaces. I plan
to use two semaphores per each instance of task to implement the blocking logic.

```
//
// Class RoundRobinScheduler maintains a list of
// tasks and steps through each one in turn.
// Each task runs in it's own thread, but only
// one task runs at a time, and all tasks
// run only when the caller is blocked on
// the stepAll method.
//
static class RoundRobinScheduler {


  // Supplies context to a running task
  public interface TaskContext {
        public String name();
        public void waitForNextStep() throws InterruptedException;
  }


  // Interface to client-provided task
  public interface  Task {
    public  abstract void run(TaskContext context);
  }

  // Adds a task with the given name to the list of
  // round-robin tasks. The name is just to help with
  // debugging and logging. The task will be added to
  // the end of the list of tasks.
  public void addTask(Task task, String name) {
  }

  // Steps once through all tasks. This is a blocking
  // call as it will wait until each task goes through
  // one step.
  public void stepAll() throws InterruptedException {
  }


  // Cancels all tasks. If a task is still running, it will
  // raise a InterrupedException on that task's thread.
  public void cancelAll() {
  }


  // Waits for all tasks to complete OR until
  // {waitMs} milliseconds transpires.
  public boolean waitForAllDone(int waitMs) {
    return true;
  }


}

```
Note that a couple of calls throw `InterruptedException`. The `waitForNextStep` method
is called in the context of the`run` method of a task, which executes in its own thread. This 
exception will be thrown if the the client cancels all tasks. This is a clean way to for the 
blocking task logic implemented in `run` to deal with cancellation, rather than checking
every time it blocks whether or not the task has been canceled.


## October 22, 2018-A JMJ OpMode design

Continuing the previous note...

More `OpMode` methods:
- static `registerOpMode(op)` - adds `op` to list of op modes
- static `runAll()` - runs all op modes. What happens depends on whether or not the
        op mode is linear or iterative. Each linear op mode has a dedicated thread from which
        it's `unOpMode()` is called. Each iterative op mode will have it's `init` and
        `start` methods called in the context of `runAll`, and it's loop method in the
	context of Processing's `draw`.

Concurrency considerations: the list of op modes are only accessed by the main Processing
thread so doesn't need to be locked. The internal state of an op mode is only looked at by
Processing code in the very beginning.

The actual op mode logic (auton mode) is now in a separate thread altogether. So it needs
to be serialized with any simulation or rendering code. This can be done by
holding a lock while run op mode is called, releasing it briefly when special method equivalent
to FTC's `opModeIsActive()` is called. This seems a bit draconian. Another is to
make sure all methods called in the context of `runOpMode` are thread safe. This is 
a lot of work as there are many such calls - to access robot methods, sensors and
updating status.

Final plan is to implement a round-robin task scheduler. It is the cleanest and most
predictable implmentation. But details are complex. More details on that in an upcoming note.

## October 20, 2018-A JMJ Thoughts on simulating Linear OpModes
Since linear `OpModes` block during the call to `runOpMode`, they have to run in their own
thread in Processing, and MUST NOT call any Processing animation functions.

Plan:
- Add static abstract class `OpMode`. Robot tasks, formally inheriting from `DriveTaskInterface`, 
 will now extend `OpMode` instead. `OpMode` has the following two methods in addition to the
 ones from `DriveTaskInterface`:
- `boolean isLinear()` - if the task to run as linear
- `void runOpMode()` that is called in a separate thread
If it is a linear task, we will not call its `init`, `deinit`, `start`, `stop` and `loop`
methods.
`OpMode` is an abstract class instead of an interface because default interface methods
is a Java 8 feature, not supported in Processing. Besides it could allow for simulation of
more aspects of the FTC/FRC SDK down the road. It is a static class to help ensure that
Processing methods do not creep into op mode code, making it harder to port back to
real robot code.

## October 14, 2018-A JMJ  Beginning to add random perturbations to the system.
The *main* purpose of perturbations is to make simulation of control algorithms (such as
PID applied to drive in a certain direction and distance) more realistic by
making the robot behave less predictably and the sensor outputs have more noise.
It also adds some randomness when the user
drives a robot, which could be considered either annoying or entertaining, especially
if one sets more aggressive perturbation parameters!

So far I've added random perturbations to motive force and torque. The constants that
control this are `PERTURBATION_*` in `RobotProperties`, and the force and torque
perturbations are implemented in `MecanumDrive` methods `perturbForceX`, `perturbForceY`,
and `perturbTorque`.

*Approach:* Use Perlin noise (Processing function `noise()`), passing in the scaled version
of the current simulated time. Several "instances" of Perlin noise are used - by starting
at different offsets in the input space to `noise`. For perturbing any particular value
such as the force in the x direction, two instances of noise are used, one additive and
one multiplicative. The final perturbed value is `in + A*noise1 + B*noise2`, where `A` and
`B` are constants, and `noise1` and `noise2` are the two different instances of noise.

There are some subtleties. In the case of force, the force in either x or y directions are
"proportional" to the force in both x and y directions, in other words to the magnitude of the force,
slightly favoring the x-direction force. Similarly, the torque is also impacted by the
force (see `perturbTorque`). 

The whole approach is not terribly elegant, but it will suffice for now, and can be adopted
to other perturbations, in particular to perturb the reported values of sensors - color, 
encoder values, and absolute and relative position values.

## October 12, 2018-C JMJ  DriveBase encoder values
These seem to work, but realize that they are emulating a normal wheel, not a mecanum wheel.
In particular, when strafing, the forward direction doesn't advance, and so the simulated
encoders don't advance either, while of course the wheels themselves are rotating.

But if moving purely forward, I think that mecanum drives behave more or less like normal
drives, except perhaps the factor by which encoder ticks have to be multiplied will be 
different, and perhaps not a simple product of wheel circumference and wheel RPM and time.

NOTE: `SampleDriveTask` monitors the `Y` button - if pressed it will reset all encoders by calling
`DriveBase.resetEncoders`.

## October 12, 2018-B JMJ  Finished moving non-mecanum stuff into new class DriveBase
Had to also move velocities (`vx, vy, va1`) because they are used to in collision
force computation. Encoder methods in `DriveBase` are implemented but not tested.

## October 12, 2018-A JMJ  Moving non-mecanum stuff out of MecanumDrive and into new class DriveBase
The move has not yet happened, but the new class `DriveBase` has been created with the 
required functionality. `DriveBase` keeps track of the position and orientation of the robot,
the location of the boundary points and walls, and the power to each of the 4 motors. It also
has a little bit of new functionality - to keep track of forward travel of each of the 4 corners,
and to use this information to return simulated encoder values.

Class `MeccanumDrive` is still unchanged - it will need to switch over to using `DriveBase` in a
future checkin.


## October 11, 2018-F JMJ  Collision of robot sides with corners is implemented!
This is implemented in `calculateCollisionImpact`, which is now called twice, once
to calculate the impact forces and torques due to collision between robot corners and
external walls (as before) and once to do this for collision between robot sides and
external corners.

There is still some deviant behaviour around corners due to the pseudo collisions with
adjacent walls (as explained earlier), but this effect is now tolerable.

## October 11, 2018-E JMJ  Updated collision visualization; robots keep walls
To better assist in visualizing collision physics, points of collision are now
red when they are actually colliding, and both robot and field walls and wall boundaries
are visualized. This visualization is enabled if `Field.visualizeCollisions` is set to true.

The robot now keeps a constantly-updated set of it's own walls in it's `walls` array. This is
for upcoming implementation of collision of robot walls with wall corners.

## October 11, 2018-D JMJ  Field now keeps a list of corners block walls
This is the first part of the support for robot sides (not corners) collision with 
wall boundaries. This is maintained in `Field.convexCorners`.

## October 11, 2018-C JMJ  Tapering thickness of non-boundary walls at their ends
This mitigates the issue for walls that define convex objects, such as block field elements.
(mentioned in "October 9, 2018-B JMJ" note and earlier notes. The mitigation is specifically
designed to work for 90-degree corners - the thickness of all non-boundary walls are
tapered by 45 degree at both ends of the wall. This is implemented in method
`collisionMagnetude`. With this change, no part of the compound wall structure have regions
where walls overlap. This allows for thicker walls, and that in turn reduces the case
where the robot breaks through walls.

A tapered wall is illustrated below - this one has it's normal facing upwards. If you
visualize 4 of these walls making up a box, you can see that the walls do not overlap.
```
----------------------------------
\                               /
 \-----------------------------/
```
The downside is that the walls can be penetrated more easily near their corners. However in practice
this effect is not dominant, and the net result is positive - for example, robots no longer
simply blast through blocks. Of course there is still the issue that robots go through wall corners
that was mentioned in earlier notes..


## October 11, 2018-B JMJ  Field elements are now specified in two files
File `data/field_base.txt` contains elements that define the standard field - this file shouldn't
change once the rules of the competition are defined.

Optional file `data/field_extras.txt` contains optional elements that are typically part of
a particular team's strategy, such as autonomous paths.

## October 11, 2018-A JMJ  Changed field element structure and processing
It was a hack to try to extract angles and starting positions from the path. How
`Element` has explicit fields for initial position (x and y) and also width, height
and rotation. The field elements data file format is unchanged EXCEPT for the block
format, which is now:

```
# block cx cy | w h | rot
# where:
#    (cx, cy) is center coordinates in feet
#    w and h are width and height, also in feet
#    rot is rotation amount (anticlockwise) in degrees
block.obsticle 4 4 | 1.92 1.92 | 90
```
With these changes, blocks can be rendered at an arbitrary rotation specified as the 
last number in block specification.

## October 10, 2018-B JMJ  Block field elements can be positioned at an angle.
Blocks were positioned as axis-aligned rectangles. Now they can be positioned
at any angle. However, the input format (`files.txt`) doesn't support
specifying an angle, so it is hardcoded at 45 degrees for now.


## October 10, 2018-B JMJ  Added "fat_black_tape" and renamed other tapes
Here's a sample of the new format for `field.txt`:

```
blue_tape 6 6 > 1 0 > 1 1 > 0 1 > 0 0
red_tape 7 7 > 1 0 > 1 1 > 0 1 > 0 0
fat_black_tape 0.5 0.5 > 0.5 0 > 1 1
block.obsticle 4 4 | 1.92 1.92
path 9 9 > 1 0 > 1 2.5 > 2 0 
mark.start 9 9
```
Note the replacing of camel casing by underscores. "fat_black_tape" can
be used to mark the crater boundaries in this years' competition.

## October 10, 2018-A JMJ  Too many points in trail was causing delays, causing simulation to misbehave
Previous to this checkin each trail was keeping up to 10,000 points. As time progressed and this
limit was reached, collisions behaved oddly, in particular the asymmetric collision force was
not having the desired effect and robotics would bounce off walls with increasing vigor. This was
clearly because the delta between successive calls to update the simulation were increasing because 
of the overhead of rendering all those points.

With this checkin, a trail keeps far fewer points in a round-robin array, and only records new
points if the distance from the previous point is visually some distance away, and also it 
trims the oldest point to keep the number of points within the limit.

## October 9, 2018-B JMJ  New field element: block, plus asymmetric collision physics
A `block` is a rectangular field element that has 4 walls:

```
# block cx cy | wx wy
block 10 20 | 30 40
```
In this first iteration, blocks are axis-aligned and are rendered gray.
In future iterations, they could be at any angle, color, and potentially could
have different resistive forces

`Field.makeWalls` makes all the walls - including the boundary walls and the walls
contributed by all the blocks among the field elements. Walls have a new parameter,
`thickness`, that is used in determining collisions.

Method `collisionMagnitude`, which determines the magnitude of a collision, if there is one,
now implements a heavily damped collision. This is to address the issue of the robots bouncing
off walls with way to much vigor. The method
now takes the velocity of the robot into account. If the robot is colliding and
moving *into* the wall, it resits with a much greater force than if it is moving *out* of the wall.

There is the very annoying effect (that was anticipated) of convex objects behaving oddly
at their corners, because a wall thinks that a robot corner is behind it when it is in fact
just intruding into a neighboring wall. After much tweaking of wall thickness and resistive
force, blocks now are not completely impassible - the robot can ram into them and go into them. This
is a consequence of having thinner walls. The thinner the wall, the more localized the
phantom intrusion effect. I had thought that with asymmetric collision forces I could now
make the resistive force extremely large, so that there is very little intrusion, thereby
enabling thinner walls, but this produces such a kickback that (I think) even that initial
kick is enough to produce a big rebound. I also experimented with introducing added
dampening forces while the robot is colliding and it was not producing any beneficial effect,
at least in the simplistic forms I was trying.

The other annoying thing is that the collision is detected only at the corners of the robots, so the
robot can introduce into blocks if it hits the corner of the block with the side of the robot. This was discussed in entry "October 5, 2018-C JMJ  Beginning implementation of collision physics" below.

## October 9, 2018-A JMJ  Fixed the motor torque-speed curve function
It was previously slewing between -1 and 1. 

New version of `motiveForce` - from the comments: Calculates the motive force in N of a single motor, given input unitless power, that ranges
within [-1, 1]. This model is based on the description in `http://lancet.mit.edu/motors/motors3.html#tscurve`.
For any particular input power, the relationship between torque and RPM is a line with -ve slope. The y-intercept
is the stall torque and the x-intercept is the max RPM, AKA no-load RPM. The way this method uses power is in
defining the line itself - so it defines a family of lines, or rather a continuum of lines, one line for each
value of power. The line furthest from the original is the torque-RPM line described above. The remaining are
simply the line multiplied by {power}:
```
 |\
 |\\
 |\\\
 |\\\\
 |---------- > RPM
```
With this fix, the robot speeds can now be properly controlled by the joysticks.

## October 8, 2018-F JMJ  Milestone - collision impact with walls works!
Currently, there are 4 hardcoded walls representing the 4 field boundary walls - these
are in the `Field` object. The walls are bit too "springy"/elastic. Currently the impact force
is linear in the distance that the robot penetrates the wall - there is no damping, i.e.,
no loss of energy during impact, so the robot bounces back. But this is a pretty decent
state of affairs, and good enough for now.

## October 8, 2018-E JMJ  Misc enhancements for collision physics
- Implemented `calculateCollisionImpact`, though it's not tested.
- `MecanumDrive`: keeps track of boundary points - the corners of the robot, expressed in field-coordinates.

Also:
- `MecanumDrive`: cache the values of sin(a) and cos(a) whenever angle a is updated.

## October 8, 2018-D JMJ  Fixed bug in strafe direction
    Note: +ve strafe makes the robot go right, and with
    the robot's front facing increasing x, "to go right"
    means to go in the direction of decreasing y:
    
                    ^ y-axis
         robot      |
       ...... FL    |
       .    .       --> x-axis
       ...... FR

## October 8, 2018-C JMJ  More code reorganization
This reorganization is chiefly to allow independent development of multiple drive tasks without having to change the
code in the main robot class, and also to allow different drive task implementations for different robot instances.
- Defined interface `DriveTask` and the old `DriveTask` is now `SampleDriveTask` - and now implements `DriveTask`.
- Removed `DriveTask` from `Robot` - it lives outside robot code now. The main program code in `simple_robot_simulator.pde` now
  creates a drive task for each robot and initializes and starts each task and directly calls each task's loop method.
  Since the robot no longer has a drive task, I also removed its `start`, `stop` and `loop` methods.
- The list of tasks is maintained in global variable `g_driveTasks`.
- Moved some status reporting around. In particular, position is now reported as part of each robot's properties.


## October 8, 2018-B JMJ  Some code reorganization
Moved several robot properties and helper methods based on those properties from `MecanumDrive` to new class `RobotProperties`. These include mass, various friction coefficients, and
methods `newForce`, etc, that depend on the absolute mass of the robot.  These are not static constants as they vary from robot instance to instance (in principle).
The primary motivation is to make these properties and methods available to other classes, such as `Wall`, and anyways, they were not specific to a mecanum drive.
`RobotProperties` is currently created in the `Robot` constructor and passed in as a constructor parameter to `MecanumDrive`.

## October 8, 2018-A JMJ  Fixed bug calculating forces in MecanumDrive
`MecanumDrive.simLoop` had it's fronts and rights reversed when computing the net force along the x and y axes. This error was not detected earlier

## October 5, 2018-D JMJ  Support a non-square field
This is really to make it viable for FRC, which has a rectangular field. Had to replace the `Field.WIDTH` with two constants,
`Field.BREADTH` (along x-axis) and `Field.DEPTH` (along y-axis). This of course had a ripple-effect in other calculations.
The pre-rendered blurry floor also had to be made non-square. The floor tile-rendering code had to be changed slightly to
completely tile the field.

## October 5, 2018-C JMJ  Beginning implementation of collision physics
Most of the code is in `CollisionPhysics.pde`, which contains a collection of classes and methods:
The `Wall` class keeps information about a single vertical wall - the position of it's center, the orientation of it's normal,
and whether or not it is a boundary wall (latter is for quickly checking if there is a collision). Method `Wall.collisionMagnitude` 
(mostly unimplemented) calculates the magnitude of the collision force of a single wall on a point, assuming frictionless collision, so the direction
of the force is along the normal of the wall, i.e., perpendicular to the wall.

The `CollisionResult` class aggregates net force and torque of  a robot, represented by a set of boundary points, with all walls.
The `calculateCollisionImpact` method (empty at present) calculates this aggregate impact. It returns null if there is no collision with any wall,
or a `CollisionResult` object if there was one or more collisions.

This approach has one key limitation - if the robot collides with a convex object (such as a rectangular prism) made up of a set
of walls, the first point of contact could be between the side of the robot and the corner of the object at the boundary of
two walls. This condition will not be detected. The robot will simply penetrate the object until one of the corners of the
robot hits a wall of the object. If the object is smaller than the robot a collision may never be detected.

In the case of a collision between two robots, even if one robot doesn't detect the collision, the other one will, so the net effect
(I think) will be that the robots never penetrate each other, though the robot whose corner hits will appear to be the only robot 
effected.

One way to detect the collision of a robot side with a corner made by two walls is to try collisions both with the boundary points on the robot and a set of walls AND
the set of wall ends with the planes between consecutive boundary points of the robot. But we are not going to try this
any time soon, as a key scenario is already supported: backing up against a field element larger than the robot.

## October 5, 2018-B JMJ  Finished implementation of multiple robots
Implemented selection of robot - the hat position of the controller is used to specify which robot a particular real gamepad is to
be bound to. hat+a, hat+b - selects a role AND robot, while START+a, START+b - selects just the role, keeping the robot 
the same (unless there wasn't a robot selected, in which case the robot with id ROBOT_1 is selected). This logic is implemented in `checkMappings` in the main program and
`GamepadManager.switchRoles`. The logic is subtle.

## October 5, 2018-A JMJ  Implemented multiple robots - mostly
The main program (`simple_robot_simulator.pde`) initializes an array of robots, `g_robots`, calling `newRobot` to create a robot with a particular Id and placed
at a particular position and orientation on the field. The main draw method has been updated to process all the robots.

It was awkward to have the role and robot ID constants defined in `GamepadManager` - so moved those constants (`ROLE_A`, etc) to the main program, along
with the one `GamepadManager` method that was referencing, them, `checkMappings`. Much cleaner.

Added `hatPos` to `GamepadInterface`. This returns an integer representation of hat state - see http://lagers.org.uk/gamecontrol/api.html. This is different
than the up/down/left/right buttons in the First FTC SDK - these latter versions are also supported by Game Control Plus, but that's 4 methods, and
I need to check that only up is pressed (not up and left, for example) - that is equivalent to a `hatPos` value of 2, so a simpler check if I use
the `hatPos` integer.

Both `Robot` and `MeccanumDrive` have a new method call `place` - to place the robot at a particular position and location. These placement parameters were
previously cluttering up the constructor parameters. The `place` method is just for initial placement, and may perhaps be used to re-set the robot
in special cases.

There are also some gratuitous re-formatting because of auto-format in this checkin.

## October 4, 2018-A JMJ  Implemented multiple gamepads with dynamic mapping
`GamepadManager`: new class that manages multiple "real" gamepads that are connected to real hardware, and multiple "proxy" gamepads that can dynamically connect with any
of the real gamepads or to nothing at all. The old `ProcessingGamepad` class has been removed.

A real gamepad (class `GamepadManager.RealGamepad`) is matched with a proxy gamepad (class `GamepadManager.ProxyGamepad`) if it matches both the "robot Id" and the "role".
Each proxy gamepad is permanently assigned a robot Id and
a role, when it is created in the main program (simple_robot_simulator.pde). The `Robot` class constructor is passed-in two gamepads, both proxy gamepads, and this assignment stays
for the life of the robot.

Initially real gamepads are not assigned to any proxy gamepads, so effectively the robots are not controlled by any gamepad. If the user presses START+A on any gamepad that has not yet been
mapped, that real gamepad is now linked to the proxy gamepad #1 of the first robot in the list of robots. If the user presses START+B, the gamepad is mapped to gamepad2. If the real
gamepad is already mapped to a proxy robot, pressing START+A or START+B will stay with the robot that it is already bound to.

If a particular real gamepad (r1) is mapped to a robot's gamepad1 (role "A") when a user presses START+A on another real gamepad(r2), gamepad1's mapping on the robot will switch to r2,
so real gamepad r1 is now not mapped to anything - it is effectively ignored until it gets mapped to some proxy gamepad on some robot.

[Not yet implemented] To select a particular robot, the user presses the DPAD - N selects robot 1, E robot 2, S robot 3 and W robot 4. By default, all gamepads are mapped
to robot 1 (i.e., the robot with Id "1"). Multiple robots are not implemented yet - this just lays the groundwork for having multiple robots.


## October 3, 2018-B JMJ  Finished implementing caching the blurred floor pixels
The code was added to classes `FieldElements` and `RawSensorModule`. `FieldElements` is responsible for rendering visible floor elements to a pixel array - on demand, in the 
method `generateFloorPixels`. `RawSensorModule` calls `generateFloorPixels` and creates a blurred version of these pixels. Since this process (particularly the blurring) is time consuming,
it caches the blurred pixels to a PNG file under data/cache. To validate the cache, a text signature of the floor elements and other parameters is generated and saved. This is done by
`RawSensorModule.constructFloorSignature` which delegates most of the signature construction to `FieldElements.appendFloorSignature`. The caching logic is implemented in
`RawSensorModule.constructBlurredFloorPixels`.
The cache files under `/data/cache` should not be checked in to source control. I added a `.gitignore` file at the `processing` level that ignores anything under `**/data/cache` for all
processing projects.


## October 3, 2018-A JMJ  Finished first cut of implementation of simulated color sensors!
`PixelHelper`: new class to manage a pixel array represented in row-major form - same as Processing's `PGraphics.pixels` field, and also to created a blurred version.
`FieldElements`: added `PixelHelper floorPixels` field that contains pre-rendered pixels (packed in row-major order) of visible floor field elements.
   This is computed on a separate buffer of size FLOOR_PIXEL_SIZE x FLOOR_PIXEL_SIZE, to which only the visible floor elements (currently only TAPE) are rendered.
`RawSensorModule`: Added init-only code that retrieves the floor pixels and creates a smoothed version of the same. This is looked up when determining color sensor values. 

Currently the process of creating the blurred version takes many seconds - so this needs to be addressed by saving a cashed version as a file, as the field elements
don't typically change.

## October 1, 2018-A JMJ  More details on simulating downward-pointing color sensors
Building on "September 28, 2018-B" note. Plan is for class Field to implement method `color senseFloorColor(double x, double y, double sensorRadius)` - this returns a color (composite
(r, g, b)) value of a simulated sensor looking downwards over a uniform region of radius r. All units in meters. A robot method can take local robot coordinates and translate it to
field coordinates and call the `senseFloorColor` method. The `senseFloorColor` method has to add all the colors of elements (including floor) that fall within the specified sensor radius. Thus
as a sensor moves over a piece of tape, the reported will gradually transition.

Implementation: simplest implementation is for `Field` to keep a single array of field elements. Each of these elements is composed of one or more segments or other primitives. It has to
intersect the sensor disk with each of these segments and compute the areas of overlap. Then factor in that element's color. A more sophisticated implementation keeps a 2D array of cells,
and each cell keeps segments that overlap with that cell. The query checks which cells overlap with the sensor disk and only checks the segments in the overlapping cells.

ALTERNATE implementation: Keep an off-screen buffer that consists of the rendered NON-virtual field elements (which never change once loaded) with a smoothing filter applied
to simulate the sensor field of view - this assumes a single sensor field of view diameter, which is fine. This buffer is computed on load. During the simulation, all that one needs
to lookup at each location is the pixel value! We can render however many of however complex shapes  needed, using Processing's built-in rendering functions. 
The downside is loss of resolution - limited to the resolution of the buffer. If we keep a 1000x1000 buffer, the spatial resolution is 144/1000, or 0.144 inch for a 12" field.
That's plenty for now, especially given that the sensor senses over an area that is probably an inch or so wide. If necessary, we can keep a higher resolution buffer.

Class `RawSensorModule` will collect all the sensor data made available to the robot, and will also be where simulated sensor errors are added. 

## September 29, 2018-C JMJ Thoughts on colliding with walls
After much thought, here's the plan. This strategy is shown here for a single case - a robot corner's x-coordinate (in field coordinates) gets close to zero or goes negative, meaning that it is
just breaching the left wall (which is the x-axis). In this case, different physics equations
take over, with the robot pivoting about this corner. Calculate the torque and angular momentum
about this point of contact and rotate the robot appropriately about this point.

The strategy can be easily applied to the 3
other walls, and potentially to other solidly anchored surfaces. 

However, It will be complex to calculate the net torque, so in the interim, looking for a simpler
solution that doesn't try to be based on physics - basically, fudge it....

Another strategy: if a corner's x coordinate is < 0, add an Fx force acting at that point,
and that goes into the existing equations. The Fx force grows exponentially as x gets
more negative. This is calculated for all four corners, though typically only 1 will hit
first. This can be generalized to other rigidly anchored surfaces - need to apply the force 
normal to that surface.

## September 29, 2018-B JMJ Thoughts on supporting multiple robots
Keep an array list of robots. Each robot has a unique number and color. The number is
rendered on the robot. Each robot is initialized with two gamepads (see below) representing
gamepad1 and gamepad2, though they may or may not be mapped to actual gamepads.

Each robot displays its internal extended status, starting with it's number - these occupy the space
to the right of the field - there should be enough space to display the internal state of 4 robots.

## September 29, 2018-A JMJ Thoughts on Multiple Gamepad mapping to multiple robots
Each gamepad self-selects which robot it goes to - robots are numbered from 1 to 4. 
By pressing start+dpad(N) the gamepad selects the robot. dpad(N) is 1 for North, 2 for East,
3 for South and 4 for West. The last gamepad to select a particular robot wins. Pressing start+A or start+B selects the particular gamepad (gamepad1 or gamepad2 in FTC terms) for that robot. 

I don't know how to query and load all compatible gamepads without bring up any UX. That's to be figured out. 

From the robot's perspective, it's given 2 gamepads, these are (implementation wise) special
re-mapping gamepad implementations that dynamically route the control query requests
to the currently mapped real gamepad for that role on that robot. I suspect that there may
be some nuances with collisions and default assignments but I don't they will be blocking.

Also, it should be possible to control multiple robots with a single joystick that timeslices across the robots - painful but possible.


## September 28, 2018-E JMJ Added ability to read and display field elements
See September 28, 2018-A note below. What I've implemented are a few hardcoded shapes:
redTape, blueTape, path and marker. The code reads a single file, called "field.txt" that 
contains the layout. For example...

```
blueTape 6 6 > 1 0 > 1 1 > 0 1 > 0 0
redTape 7 7 > 1 0 > 1 1 > 0 1 > 0 0
path 8 8 > 1 0 > 1 1 > 0 1 > 0 0 
mark.p1 9 9
mark.p2 2 2
```
The first pair of numbers is an absolute position. Remaining numbers are relative to that
first point. Units are in feet. They are converted appropriately.
The text after the "." in "park.p1" is considered a label of that mark. Other objects
can also have labels, but currently we only display labels on marks.
Most of the functionality is implemented in class FieldElements and its contained classes.

There is limited error checking, so a badly formatted field.txt file will probably throw
an exception.


## September 28, 2018-D JMJ Added a config.txt file, and ability to disable gamepad
The file ./data/config.txt is read and used to set various global variables.
The only one so far is g_noGamepad, which is false by default, but set to
true if the config file contains the line `noGamepad`. The config file
can contain  the # comment character.

Even if the gamepad is enabled, if it can't load (typically because it can't find a compatible gamepad) the program can run but with the gamepad functionality
not available.

## September 28, 2018-C JMJ Thoughts on collision with the field walls and other elements
Track collision of the 4 corners of the robot. If they collide, assume frictionless contact - so the 
impact force is acting on the corner, and perpendicular to the wall (or other object surface). The impact
force is calculated to be high enough to bring the robot to a stop in (say) 1cm. This force is included
in simulation calculations - both net force and net torque. This *should* result in the robot swinging
so that another corner will also collide - at which point we have two forces that will cancel out torque,
and the robot should come to a hard stop - unless autonomous code or user backs away. Fairly straightforward in principle,
but requires collision detection and vector calculations.

## September 28, 2018-B JMJ Thoughts on providing feedback to the robot on position, color, etc
It would be nice to provide simulated sensor inputs to be able to more comprehensively test autonomous
code. Absolute position and bearing can of course be reported with perfect accuracy, and in fact some errors
will need to be added to simulate real-life issues. Color and IR, bump or range sensors are much trickier, and one
needs to decide what is the right level of investment  - bang for the buck so to speak. One idea is to support
querying the field color at a specific point relative to the robot. This can be done by interrogating the list of
field elements and seeing if any of them intersect at that point. A graded value can be provided if they only
partly overlap the query region. Similarly, one can ask if a specified point relative to the robot is inside or 
outside the field, providing a kinda-sorta simulation of distance and bump sensors. Potentially one can also
query distances to specific labeled annotation markers. In all cases, simulated errors should be added to emulate
real-world situations under different lighting conditions.


More thought needs to be given to this subject, and probably any implementation should be driven by a very specific need.
## September 28, 2018-A JMJ Thoughts on adding tape and annotations to the field
It would be nice to be able to apply field elements, especially the red and blue tape, but also to be able
to load arbitrary annotations marking potential paths and destinations for different stages of autonomous
driving, or to set up challenges for driver practice.

It would be nice if this can be done using external data files, rather hardcoding in the code. Here's the proposal:
- File colors.txt gives names to colors. Each line has the form:
   `color-name r g b [alpha]`
   For example:
   `blue 50 100 255` #approximately the color of the FIRST blue gaffer tape
- File shapes.txt gives names to shapes. Each line has the form:
   `shape-name color shape-type width`
   For example:
   `blue_tape blue tape 2` # 2"-wide tape of color 'blue'
- For now, we will have the following shape types built into the code:
  - tape - tape on the ground of any thickness and color
  - mark - an annotated mark - this will not be a field element but rather an annotation
- File field.txt contains specific shapes that make up the field. Each line has the form:
   `shape-name x0 y0 followed by shape-specific information`
   For example:
   `blue_tape 12 12 > 0 1 > 1 0 > 0 0 # Start at (12, 12) and them relative to that point, go to subsequent points.`
- File annotations.txt contain annotations that are not part of the field. This file has the same format.
   For example:
   `mark 10 6 P1` Defines a mark at (12, 12) with label "P1"
  Annotations are like field elements, except they do not feature in sensor input, which is a topic of
  a forthcoming note.

## September 27, 2018 JMJ Looking back at original motivations and progress
The original motivation was and remains to allow our FTC team, and potentially other FTC teams, to do
early prototyping and testing of autonomous programs. Since our team is trying out a mecanum-wheel based
holonomic drive this season (2018-2019), I decided to implement a very simple 2D physics-based model of a mecanum-
drive based robot
that is integrated into an FTC field animation. I wanted to write this from scratch rather than using an existing
2D or 3D physics library because I hoped (and still hope) for the team members to understand its inner workings
more deeply and hopefully make changes themselves. I don't use any sophisticated physics or Java constructs. In theory
any HS student with AP-CS type background and standard HS physics classes should be able to understand and modify
the code.

A secondary objective was to enable simple drive practice. This is contingent on the simulation at least 
vaguely approaching the experience of driving the real robot. This remains to be seen. There are many
constants (such as friction forces, motor power and weight) that can be tweaked, so I am hopeful that it will
serve as at least a starting point for driver practice.

### The Physics Model
The heart of the mecanum drive simulation is in the class `MeccanumDrive`. It models the drive as 4 "motive" forces
acting diagonally on the 4 corners of the robot, like so:

```
/-----\
|     |
|     |
\-----/
```
These 4 forces are coupled with friction forces to generate a net force and torque. The torque is assume to be acting
about the center of the robot. The net force determines the linear acceleration of the center of the robot,
and on top of that, the net torque is used to compute angular acceleration about the center. These accelerations
are used in the simulation loop to update linear (x,y) and angular (bearing) positions on the field.

Here are some of the biggest simplifications:
1. Friction forces are not calculated and applied at each wheel. Rather an aggregate friction force is computed,
using a combination of weight and number of stopped motors, and this force is applied in a direction opposite
to the direction of motion of the *center* of the robot.
A resistive/dampening torque is similarly applied, that is proportional
to this aggregate friction force. This is a vast simplification, because in reality, each wheel has it's unique
contribution to drag, taking into account it's direction (remember that these are mecanum wheels) and also
the relative speed of the wheels and the ground - is there slipping? If not, is the speed matching what the
motor would have it do? So this is a very ham-handed approximation.
2. The wheels are assumed to be perfectly located and oriented at the corners of a square.
However deviations from the perfect can be approximated by adjusting a constant factor applied to each
motor to attenuate that motor's power - these are the following constants:

```
  final double powerAdjustFL = 1.0;
  final double powerAdjustFR = 1.0;
  final double powerAdjustBL = 1.0;
  final double powerAdjustBR = 1.0;
```

Setting the first constant to 0, for example, effectively disables sending power to the front-left motor.

