void test_op_modes() {
  println("HOWDY!");
  Robot r1 = null;
  Robot r2 = null;
  IterativeOpMode op1 = new TestIterativeOpMode(r1);
  LinearOpMode op2 = new TestLinearOpMode(r2);
  OpModeManager.registerIterativeOpMode(op1);
  OpModeManager.registerLinearOpMode(op2);
  OpModeManager.startAll();
}

void test_op_modes_loop() {
  OpModeManager.loopAll();
}


static class TestIterativeOpMode extends IterativeOpMode {
  final Robot r;
  int count = 0;
  TestIterativeOpMode(Robot r) {
    this.r = r;
  }

  @Override
    public void init() {
      println("A: IN INIT");
  }


  @Override
    public void deinit() {
      println("A: IN DEINIT");
  }


  @Override
    public void start() {
      println("A: IN START");
  }


  @Override
    public void stop() {
      println("A: IN STOP");
  }


  @Override
    public void loop() {
      if (count < 10) {
        println("A: IN LOOP # " + count);
      }
      count++;
  }
}

static class TestLinearOpMode extends LinearOpMode {
  final Robot r;
  
  TestLinearOpMode(Robot r) {
    this.r = r;
  }


  @Override
    public void runOpMode() {
      for (int i = 0; opModeIsActive() && i < 10; i++) {
        System.out.println("B: LIN OM step: " + i);
        my_delay(1);// Wait a tiny amount
        
      }     
    }
}

static void my_delay(int ms) {
  try {
    Thread.sleep(ms);
  }
  catch (InterruptedException e) {
    // We just absorb this exception.
  }
}
