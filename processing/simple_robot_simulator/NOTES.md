# Design and Implementation Notes
This document contains an informal log of design and implementation decisions for this project,
the "Simple Robot Simulator."

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
early prototyping and testing of autonomous programs. Since our team is trying out a meccanum-wheel based
holonomic drive this season (2018-2019), I decided to implement a very simple 2D physics-based model of a meccanum-
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
The heart of the meccanum drive simulation is in the class MeccanumDrive. It models the drive as 4 "motive" forces
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
contribution to drag, taking into account it's direction (remember that these are meccanum wheels) and also
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
