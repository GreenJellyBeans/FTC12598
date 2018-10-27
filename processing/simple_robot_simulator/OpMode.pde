 //<>//
static abstract class OpMode {

  private static List<OpMode> opModeList = new ArrayList<OpMode>();

  // Keeps track of internal state
  enum RunStatus {
    UNINIT, 
      INIT, 
      RUNNING, 
      STOPPED, 
      DEINIT
  };

  RunStatus status = RunStatus.UNINIT;

  //
  // Linear op-mode methods...
  //
  
  // Override to return true to indicat
  // this is a linear op-mode
  public boolean isLinear() {
    return false;
  }
  
  // Override this method to actually
  // run a linear op-mode
  public void runOpMode()  {
  }
  
  // Will yield the processor to other threads, and also
  // check if the op mode is still active. As in the FTC 
  // API, call this from runOpMode to break-up blocking operations,
  // such as:
  //      while (opModeIsActive() && _some_condition_) {
  //              _do_something_incremental_
  //      }
  final boolean opModeIsAcive() {
    return false;
  }


  //
  // Iterative op-mode methods
  //


  // Override for one-time initialization
  public void init() {
  }

  
  // Override for one-time deinitialization
  public void deinit() {
  }
 
  
  // Override for starting logic
  // (called after init())
  public void start() {
  }
  
  
  // Override for stopping logic.
  // Called before deinit()
  public void stop() {
  }
  
    
  // Override for iterative
  // operations. Called repeatedly
  // after start() and before stop().
  public void loop() {
  }
 

  //
  // Op-mode registration and scheduling
  // methods.
  

  // Registers a single op mode. MUST be called
  // before the runAll Method
  static void registerOpMode(OpMode op) {
  }


  //
  // Runs all registered op-modes.
  //
  static void runAll() {
  }


  // Must be called periodically.
  // Provides the context to 
  // call the loop methods of all
  // registered iterative op-modes.
  static void loopAll() {
  }
}
