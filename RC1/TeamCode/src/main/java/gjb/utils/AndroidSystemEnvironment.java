package gjb.utils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import gjb.interfaces.SystemEnvironmentInterface;

/**
 * Created by josephj on 9/25/2017.
 */
// Implements access to various system support functions whose implementation is specific to
//    the Android OS
public class AndroidSystemEnvironment implements SystemEnvironmentInterface {
    final OpMode om;

    public AndroidSystemEnvironment(OpMode om) {
        this.om = om;
    }

    @Override
    public void dbgPrintln(String s) {

    }

    @Override
    public void dsPrintln(String s) {
        om.telemetry.addData("LOG", s);
    }

    @Override
    public void fsPrintln(String s) {

    }

    @Override
    public void fsFlush() {

    }

    @Override
    public void fsNewLogSession(String sessionId) {

    }

    @Override
    public void fsCloseLog() {

    }
}
