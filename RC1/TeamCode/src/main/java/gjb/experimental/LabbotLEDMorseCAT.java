/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package gjb.experimental;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.util.ElapsedTime;

import gjb.utils.TimeChecker;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * Initial source: external.samples.TemplateOpMode_Iterative, copied by JMJ on July 23, 2017
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 */
@TeleOp(name="LabbotLEDMorseCAT.01", group="Iterative Opmode")
public class LabbotLEDMorseCAT extends OpMode
{
    /* Declare OpMode members. */
    private ElapsedTime runtime = new  ElapsedTime();
    private int loopCounter = 0; // incremented each time a loop method is called.
    TimeChecker tc;

    AnalogOutput ao;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        alwaysLog("LED:ENTERING init().");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        // leftMotor  = hardwareMap.dcMotor.get("left_drive");
        // rightMotor = hardwareMap.dcMotor.get("right_drive");

        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        //  rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        loopCounter = 0;
        tc = new TimeChecker();
        ao = hardwareMap.analogOutput.get("ellie-read");
        alwaysLog("LED:  ao:" + ao);
        ao.setAnalogOutputMode((byte)0); // Voltage output
        MorseGenerator mg  = new MorseGenerator();
        int[] delays = mg.generateDelays("CAT", 10);
        tc.setTimesByDurations(delays, 75);
        alwaysLog("LED:EXITING  init().");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        periodicLog("LED:ENTERING init_loop().");
        periodicLog("LED:EXITING  init_loop().");
        loopCounter++;
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        alwaysLog("LED:ENTERING start().");
        runtime.reset();
        loopCounter=0;
        tc.reset();
        alwaysLog("LED:EXITING  start().");
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        periodicLog("LED:ENTERING loop().");

        // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
        // leftMotor.setPower(-gamepad1.left_stick_y);
        // rightMotor.setPower(-gamepad1.right_stick_y);
        processStages(tc);
        periodicLog("LED:EXITING  loop().");
        loopCounter++;

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        alwaysLog("LED:ENTERING stop().");
        alwaysLog("LED:EXITING  stop().");
    }

    /*
     * Log supplied message every Nth value of loopCounter which is expected to
     * be incremented in the call to the containing loop method.
     */
    void periodicLog(String msg) {
        final int PERIOD = 50;
        if (loopCounter % PERIOD == 0) {
            telemetry.addData("Status", "[" + msg + "] lc: " + loopCounter + " rt: " + runtime.toString());
        }
    }

    /*
    * Log supplied message.
    */
    void alwaysLog(String msg) {
        telemetry.addData("Status", "[" + msg + "] rt: " + runtime.toString());
    }

    void processStages(TimeChecker tc) {
        int stage = tc.getCurrentStage(); // Returns i where time is < ith value
        boolean lightsOn = false;
        if (stage % 2 == 1 && stage < tc.getNumStages()) {
            lightsOn = true; // turn lights on
        }
        //Sets the channel output voltage. If mode == 0: takes input from -1023-1023, output in the range -4 to +4 volts. If mode == 1, 2, or 3: takes input from 0-1023, output in the range 0 to 8 volts.
        int voltage = (int) ((3.5/4.0) * 1023); // 3.5 v
        if (lightsOn) {
            //fill(0, 255, 0);
            voltage = 0; // 0 maps to 0V
        } else {
            //fill(0);
        }
        //periodicLog("Setting ao voltage " + voltage);
        ao.setAnalogOutputVoltage(voltage);

        //ellipse(width/2, height/2, 100, 100);
    }
}
