void test_op_modes() {
  println("HOWDY!");
  Robot r1 = null;
  Robot r2 = null;
  OpMode op1 = new TestIterativeOpMode(r1);
  OpMode op2 = new TestLinearOpMode(r2);
  OpMode.registerOpMode(op1);
  OpMode.registerOpMode(op2);
  OpMode.runAll();
}

void test_op_modes_loop() {
  OpMode.loopAll();
}


static class TestIterativeOpMode extends OpMode {
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

static class TestLinearOpMode extends OpMode {
  final Robot r;
  
  TestLinearOpMode(Robot r) {
    this.r = r;
  }

  @Override
    public boolean isLinear() {
    return true;
  }

  @Override
    public void runOpMode() {
      for (int i = 0; i < 10; i++) {
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
