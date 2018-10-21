import java.lang.Runnable;

static abstract class OpMode {
  
  // Keeps track of internal state
  enum RunStatus {
    UNINIT,
    INIT,
    RUNNING,
    STOPPED,
    DEINIT
  };
  
  RunStatus status = RunStatus.UNINIT;
  private Object lock = new Object();
  
  public boolean isLinear() {
    return false;
  }
  
  public void runOpMode() throws Exception {
  }
  
  public void init() {}
  public void deinit() {}
  public void start() {}
  public void stop() {}
  public void loop() {}
  
  static void launch(final OpMode om) {
    assert(om.status == RunStatus.UNINIT);
    if (om.isLinear()) {
      Runnable r = new Runnable() {
        public void run() {
          try {
            om.runOpMode();
          }
          catch (Exception e) {
            System.err.println("BG task threw exception " + e + "; Ending it");
          }
        }
      };
      Thread bgThread = new Thread(r);
      bgThread.run();
    }
  }
  
}

class MyOpmode extends OpMode {
  boolean isLinear() {
    return true;
  }
  
  void runOpMode()  {
    System.out.println("IN runOpMode()");
  }
  
}
