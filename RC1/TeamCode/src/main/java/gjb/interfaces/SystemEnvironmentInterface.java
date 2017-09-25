package gjb.interfaces;

/**
 * Created by josephj on 9/24/2017.
 */

// Methods that implement access to system resources that depend
// on the runtime environment.
public interface SystemEnvironmentInterface {

    // Print to debug output
    void dbgPrintln(String s);

    // Print to Driver Station log
    void dsPrintln(String s);

    // Print to File System log
    void fsPrintln(String s);

    // Flush file system log.
    void fsFlush();

    // Start a new log session (typically a new file)
    void fsNewLogSession(String sessionId);

    // Close the open log file.
    void fsCloseLog();

}
