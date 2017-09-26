package gjb.utils;

import gjb.interfaces.SystemEnvironmentInterface;

/**
 * Created by josephj on 9/25/2017.
 */
// Implements access to various system support functions whose implementation is specific to
//    the Android OS
public class DesktopSystemEnvironment implements SystemEnvironmentInterface {
    @Override
    public void dbgPrintln(String s) {

    }

    @Override
    public void dsPrintln(String s) {

    }

    @Override
    public void fsPrintln(String s) {
        System.out.println(s);
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
