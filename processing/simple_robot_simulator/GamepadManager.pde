// The GamepadManager class manages multiple gamepads dynamically mapped to
// multiple robots. It uses the Processing Game Control Plus API built by Peter Lager, which is a revised
// version of the proCONTROLL created by Christian Riekoff. For more information,
// see http://lagers.org.uk/gamecontrol/.
// Author: Joseph M. Joy, FTC12598 mentor.
//
import org.gamecontrolplus.*;

class GamepadManager {

  // These are the supported roles...
  final String ROLE_A = "A";
  final String ROLE_B = "B";

  // Thease are the supported robot IDs:
  final String ROBOT_1 = "1";
  
  final String configName;
  final int numHwGamepads;

  private ControlIO control; // set in init()
  private final List<ProxyGamepad> proxyGamepads = new ArrayList<ProxyGamepad>();
  private final RealGamepad[] realGamepads;

  // A virtual or proxy gamepad that routes all its methods to
  // an underlying real gamepad (if there is one) or to nothing at all.
  class ProxyGamepad implements GamepadInterface {

    final String robotId;
    final String role;
    RealGamepad rg; // May be null


    // Each instance of ProxyGamepad is permanently bound
    // to a particulary role ({role} on a particular robot
    // (robot with Id {robotId}).
    ProxyGamepad(String robotId, String role) {
      this.robotId = robotId;
      this.role = role;
    }


    @Override
      public double left_stick_x() {
      return rg==null ? 0 : rg.left_stick_x();
    }


    @Override
      public double left_stick_y() {
      return rg==null ? 0 : rg.left_stick_y();
    }


    @Override
      public double right_stick_x() { 
      return rg==null ? 0 : rg.right_stick_x();
    }


    @Override
      public double right_stick_y() {
      return rg==null ? 0 : rg.right_stick_y();
    }


    @Override
      public boolean left_bumper() {
      return rg==null ? false : rg.left_bumper();
    }


    @Override
      public boolean right_bumper() {
      return rg==null ? false : rg.right_bumper();
    }


    @Override
      public float right_trigger() {
      return rg==null ? 0 : rg.right_trigger();
    }


    @Override
      public float left_trigger() {
      return rg==null ? 0 : rg.left_trigger();
    }


    @Override
      public boolean y() {
      return rg==null ? false : rg.y();
    }


    @Override
      public boolean a() {
      return rg==null ? false : rg.a();
    }


    @Override
      public boolean b() {
      return rg==null  ? false : rg.b();
    }


    @Override
      public boolean start() {
      return rg==null  ? false : rg.start();
    }
  }


  // This class controls an actual hardware gamepad.
  // It may or may not be linked with a proxy gamepad.
  class RealGamepad implements GamepadInterface {

    // Buttons
    private ControlButton start;
    private ControlButton left_bumper;
    private ControlButton right_bumper;
    private ControlButton a;
    private ControlButton b;
    private ControlButton y;

    // Sliders
    private ControlSlider left_stick_x;
    private ControlSlider left_stick_y;
    private ControlSlider right_stick_x;
    private ControlSlider right_stick_y;

    // Hat
    private ControlHat dpad;

    ProxyGamepad pg; // May be null - managed by GamepadManager.linkGamepads()

    // {device} represents the h/w gamepad
    public void init(ControlDevice device) {

      // Buttons
      start = device.getButton("start");
      left_bumper = device.getButton("left_bumper");
      right_bumper = device.getButton("right_bumper");
      a = device.getButton("a");
      b = device.getButton("b");
      y = device.getButton("y");

      // Sliders
      left_stick_x = device.getSlider("left_stick_x");
      left_stick_y = device.getSlider("left_stick_y");
      right_stick_x = device.getSlider("right_stick_x");
      right_stick_y = device.getSlider("right_stick_y");

      // Hat
      dpad = device.getHat("dpad");
    }


    public void deinit() {
    }


    @Override
      public double left_stick_x() {
      return left_stick_x.getValue();
    }


    @Override
      public double left_stick_y() {
      return left_stick_y.getValue();
    }


    @Override
      public double right_stick_x() { 
      return right_stick_x.getValue();
    }


    @Override
      public double right_stick_y() {
      return right_stick_y.getValue();
    }


    @Override
      public boolean left_bumper() {
      return left_bumper.pressed();
    }


    @Override
      public boolean right_bumper() {
      return right_bumper.pressed();
    }


    @Override
      public float right_trigger() {
      return 0; // TODO
    }


    @Override
      public float left_trigger() {
      return 0; // TODO
    }


    @Override
      public boolean y() {
      return y.pressed();
    }


    @Override
      public boolean a() {
      return a.pressed();
    }


    @Override
      public boolean b() {
      return b.pressed();
    }


    @Override
      public boolean start() {
      return start.pressed();
    }
  }


  // {configName} refers to a file that lives under the ./data folder.
  // The file contains mapping from strings such as "left_stick_x" to a hardware-specific
  // button/hat/slider of the controller. See http://lagers.org.uk/gamecontrol/.
  // for how to create a file for a new controller.
  // Currently we must specify here the number of actual hardware
  // gamepads expected - as {numHwGamepads} because we do not
  // auto-detect the number of gamepads.
  GamepadManager(String configName, int numHwGamepads) {
    this.configName = configName;
    this.numHwGamepads = numHwGamepads;
    realGamepads = new RealGamepad[numHwGamepads];
  }


  void init() {

    if (numHwGamepads > 0) {
      // Initialise the ControlIO
      control = ControlIO.getInstance(g_pa);

      for (int i = 0; i < numHwGamepads; i++) {
        // Find a device that matches the configuration file
        ControlDevice device = control.getMatchedDevice(configName); // Under .\data
        if (device == null) {
          System.err.println("Could not find " + i + "th gamepad device [" + configName + "]");
          return; // *****  EARLY RETURN
        }
        RealGamepad rg = new RealGamepad();
        rg.init(device);
        println("Created real device " + rg);
        realGamepads[i] = rg;
      }
    }
  }

  // Creates a new proxy gamepad that will be permanently identified
  // with the robot with Id {robotId}, and with role {role}.
  GamepadInterface newProxyGamepad(String robotId, String role) {
    ProxyGamepad gp = new ProxyGamepad(robotId, role);
    proxyGamepads.add(gp);
    return gp;
  }


  // Must be called periodically
  // to check if there is a change in mapping
  // of real gamepads to robots and roles
  void checkMappings() {

    for (RealGamepad rg : realGamepads) {
      if (rg != null && rg.start()) {
        if (rg.a()) {
          switchRoles(rg, ROLE_A);
        } else if (rg.b()) {
          switchRoles(rg, ROLE_B);
        }
      }
    }
  }


  private void switchRoles(RealGamepad rg, String newRole) {
    if (proxyGamepads.size() == 0 || (rg.pg != null && rg.pg.role.equals(newRole))) {
      // Nothing to do. Either there are no proxy gamepads (an unlikely situation)
      // or rg is already mapped to a proxy gamepad with role {newRole} 
      return; // *********** EARLY RETURN ***********
    }

    // If previously unmapped, the default robot ID is always the ID of the
    // first proxy gamepad.
    String defaultRobotId = proxyGamepads.get(0).robotId;
    String robotId = rg.pg == null ? defaultRobotId : rg.pg.robotId;

    // Find a matching proxy gamepad.
    ProxyGamepad pgNew = null;
    for (ProxyGamepad pg : proxyGamepads) {
      if (pg.role.equals(newRole) && pg.robotId.equals(robotId)) {
        // Found one!
        pgNew = pg;
        break;
      }
    }

    if (pgNew == null) {
      System.err.println("Could not find a proxy gamepad with role " + newRole);
    } else {
      assert(rg.pg != pgNew); // we already checked for this condition earlier
      linkGamepads(rg, pgNew);
    }
  }


  synchronized void linkGamepads(RealGamepad rg, ProxyGamepad pg) {   

    // If necessary, break existing links
    if (rg.pg != null) {
      assert(rg.pg.rg == rg); // integrity check
      rg.pg.rg  = null;
      rg.pg = null;
    }
    if (pg.rg != null) {
      assert(pg.rg.pg == pg); // integrity check
      pg.rg.pg = null;
      pg.rg = null;
    }

    // Set up the two-way link
    assert(rg.pg == null && pg.rg == null);
    rg.pg = pg;
    pg.rg = rg;
  }
}
