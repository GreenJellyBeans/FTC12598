/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/24/2017.
 */
package gjb.utils;

import java.util.concurrent.atomic.AtomicInteger;

import gjb.interfaces.LoggingInterface;
import gjb.interfaces.SystemEnvironmentInterface;


public class Logger {
    final String PRI0 = "0"; // Tag indicating pri0
    final String PRI1 = "1"; // Tag indicating pri0

    private final SystemEnvironmentInterface env;
    private final String rootName;
    private LoggingImplementation rootLog;
    private String sessionId;
    private long sessionStart;
    private boolean sessionStarted = false;
    private AtomicInteger seqNo = new AtomicInteger(0);

    private class LoggingImplementation implements LoggingInterface {

        final String component;
        boolean enabled = true;

        LoggingImplementation(String component) {
            this.component = scrubText(component); // Replace  ',' etc (these shouldn't be there) by '#'
        }

        @Override
        public LoggingInterface newLogger(String component) {
            return new LoggingImplementation(rootName + "." + component);
        }

        @Override
        public void err(String msgType, String s) {
            rawLog("0", "ERR", msgType, s);
        }

        @Override
        public void warn(String msgType, String s) {
            rawLog("1", "WARN", msgType, s);
        }

        @Override
        public void pri1(String msgType, String s) {
            rawLog("1", "INFO", msgType, s);
        }

        @Override
        public void pri2(String msgType, String s) {
            rawLog("2", "INFO", msgType, s);
        }

        @Override
        public void pri3(String msgType, String s) {
            rawLog("3", "INFO", msgType, s);
        }

        @Override
        public void err(String s) {
            err(OTHER, s);
        }

        @Override
        public void warn(String s) {
            warn(OTHER, s);
        }

        @Override
        public void pri1(String s) {
            pri1(OTHER, s);
        }

        @Override
        public void pri2(String s) {
            pri2(OTHER, s);
        }

        @Override
        public void pri3(String s) {
            pri3(OTHER, s);
        }

        @Override
        public void pause() {
            pri1(LOGGER, "Logging paused.");
            enabled = false;
        }

        @Override
        public void resume() {
            enabled = true;
            pri1(LOGGER, "Logging resumed.");
        }

        @Override
        public void loggedAssert(boolean cond, String s) {
            if (!cond) {
                // Small race condition here where we may
                // attempt to end session twice - but this is
                // OK because we are on the path to an assertion failure
                // anyways - end of program execution.
                if (sessionStarted) {
                    err(ASSERTFAIL, s);
                    endSession();
                    assert(false);
                }
            }

        }

        @Override
        public void flush() {
            if (sessionStarted) {
                env.fsFlush();
            }
        }

        private void rawLog(String pri, String cat, String msgType, String msg) {
            // Example:
            //  _sid:989, _sn:1, _ts: 120, _co: .b, _pri:1, _sev:INFO, _ty:OTHER, Hello world!
            if (!enabled) return; // ******** EARLY RETURN ********************

            msgType = scrubText(msgType);
            msg = escapeMsg(msg);
            int curSeq = seqNo.incrementAndGet();
            long timestamp = System.currentTimeMillis() - sessionStart;
            String output = String.format("%s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s",
                    LoggingInterface.SESSION_ID, sessionId,
                    LoggingInterface.SEQ_NO, curSeq,
                    LoggingInterface.TIMESTAMP, timestamp,
                    LoggingInterface.COMPONENT, component,
                    LoggingInterface.PRI, pri,
                    LoggingInterface.CAT, cat,
                    LoggingInterface.TYPE, msgType,
                    msg
                    );
            if (sessionStarted) {
                env.fsPrintln(output);

                // For now, Pri0 and Pri1 logs are also sent to the Driver Station
                if (pri.equals(PRI0) || pri.equals(PRI1)) {
                    env.dsPrintln(output);
                }
            }
        }
    }

    // Replace invalid chars by a '#'
    private String scrubText(String msgType) {
        // Presumably this is faster than using a Regex? Not sure.
        return msgType.replace(',', '#').replace('"', '#').replace('\n', '#').replace('\r', '#');
    }

    // TODO: Do proper CSV escaping
    private String escapeMsg(String msg) {
        return msg.replace(',', '#').replace('"', '#').replace('\n', '#').replace('\r', '#');
    }


    public Logger(SystemEnvironmentInterface env, String rootName) {
        this.env = env;
        this.rootName = rootName;
    }

    // Get the root ("top level") logging object.
    public synchronized LoggingInterface getRootLog() {
        if (rootLog == null) {
            rootLog = new LoggingImplementation(rootName);
        }
        return rootLog;
    }

    // Begins the logging session. The Session timestamp is set.
    // Caller must ensure no other thread attempts to log concurrently with
    // this call - actual logging calls are not synchronized for performance
    // reasons.
    public synchronized void  beginSession(String sessionDescription) {
        assert(!this.sessionStarted);
        long startTime = System.currentTimeMillis();
        String sessionID = "" + startTime; // WAS String.format("%020d", startTime);
        env.fsNewLogSession(sessionId);
        this.sessionId = sessionID;
        this.sessionStart  = startTime;
        this.sessionStarted = true;
        seqNo.set(0); // First logged sequence number in the session is 1.
        LoggingInterface rootLog = getRootLog();
        rootLog.pri1(LoggingInterface.LOGGER, sessionDescription + " session started.");
    }

    // Caller must encure no other thread attempts to log concurrently
    // with this thread - actual logging calls are not synchronized for
    // performance reasons.
    public synchronized void endSession() {
        assert(this.sessionStarted);
        LoggingInterface rootLog = getRootLog();
        rootLog.pri1(LoggingInterface.LOGGER, "Session stopped.");
        this.sessionStarted = false;
        env.fsFlush();
        env.fsCloseLog();

    }
}
