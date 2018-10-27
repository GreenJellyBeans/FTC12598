// These classes implement "FTC-style" linear and iterative op modes. //<>//
// Author: Joseph M. Joy FTC 12598 and FRC 1899 mentor
//


static class OpModeManager {
  
  static RoundRobinScheduler rrs  = new RoundRobinScheduler(); // for iterative op modes
  static List<IterativeOpMode> iterativeList = new ArrayList<IterativeOpMode>(); // for linear op modes

  // Registers a linear op mode. MUST be called
  // before the runAll Method
  static void registerIterativeOpMode(IterativeOpMode op) {
    iterativeList.add(op);
  }


  // Registers an iterative op mode. MUST be called
  // before the runAll Method
  static void registerLinearOpMode(LinearOpMode op) {
    rrs.addTask(op, "LIN-OP");
  }


  //
  // Starts all registered op-modes.
  //
  static void startAll() {
    
    // Linear op modes are RoundRobinSimulator.Tasks, and they
    // will be each associated with their own thread, waiting for the first step.
    rrs.stepAll();
    
    // Iterative op modes should be inited and started here.
    for (IterativeOpMode op: iterativeList) {
      op.init();
      op.start();
    }
  }


  // Must be called periodically.
  // Provides the context to 
  // call the loop methods of all
  // registered iterative op-modes.
  static void loopAll() {
    
    // Linear op mode run-time execution is managed by
    // the RoundRobinScheduler.
    rrs.stepAll();
    
    // Iterative op modes run-time execution is managed directly here.
    for (IterativeOpMode op: iterativeList) {
      op.loop();
    }
  }
}


static abstract class IterativeOpMode {

  // Keeps track of internal state
  enum RunStatus {
    UNINIT, 
      INIT, 
      RUNNING, 
      STOPPED, 
      DEINIT
  };

  RunStatus status = RunStatus.UNINIT;


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
}

static abstract class LinearOpMode implements RoundRobinScheduler.Task {

  // Override this method to actually
  // run a linear op-mode
  abstract public void runOpMode();

  // Will yield the processor to other threads, and also
  // check if the op mode is still active. As in the FTC 
  // API, call this from runOpMode to break-up blocking operations,
  // such as:
  //      while (opModeIsActive() && _some_condition_) {
  //              _do_something_incremental_
  //      }
  final boolean opModeIsActive() {
    return false;
  }
  
  final void run(RoundRobinScheduler.TaskContext context) {
  }
  
  
}
