//
// The ProcessingGamepad class emulates the FIRST FTC Gamepad class by calling
// the Processing Game Control Plus API built by Peter Lager, which is a revised
// version of the proCONTROLL created by Christian Riekoff. For more information,
// see http://lagers.org.uk/gamecontrol/.
//
// It's purpose is for basic experimentation and validation
// of robot drive control algorithms, and potentially augment drive practice.
// Author: Joseph M. Joy, FTC12598 mentor.
//
import org.gamecontrolplus.*;

public class ProcessingGamepad implements GamepadInterface {
  private final String configName;
  private ControlIO control;
  private ControlDevice device; // Representing the controller/gamepad
  boolean disabled = true; // set to true on successful init.

  // Buttons, Sliders and Hats
  private ControlButton left_bumper;
  private ControlButton right_bumper;
  private ControlButton a;
  private ControlButton y;
  private ControlHat dpad;
  private ControlSlider left_stick_x;
  private ControlSlider left_stick_y;
  private ControlSlider right_stick_x;
  private ControlSlider right_stick_y;


  // {configName} must match the name of a file under the ./data directory. The
  // file contains mapping from strings such as "left_stick_x" to a hardware-specific
  // button/hat/slider of the controller. See http://lagers.org.uk/gamecontrol/.
  // for how to create a file for a new controller.
  public ProcessingGamepad(String configName) {
    this.configName = configName;
  }


  public void init() {
    
    if (configName == null) {
      // No gamepad!
      return; // *********** EARLY RETURN
    }
    
    // Initialise the ControlIO
    control = ControlIO.getInstance(g_pa);

    // Find a device that matches the configuration file
    device = control.getMatchedDevice(configName); // Under .\data
    if (device == null) {
      System.err.println("Could not find gamepad device [" + configName + "]");
      return; // *****  EARLY RETURN
    }
    left_stick_x = device.getSlider("left_stick_x");
    left_stick_y = device.getSlider("left_stick_y");
    right_stick_x = device.getSlider("right_stick_x");
    right_stick_y = device.getSlider("right_stick_y");
    left_bumper = device.getButton("left_bumper");
    right_bumper = device.getButton("right_bumper");
    a = device.getButton("a");
    y = device.getButton("y");
    dpad = device.getHat("dpad");
    disabled = false;
  }


  public void deinit() {
  }

 
  @Override
    public double left_stick_x() {
    return disabled ? 0: left_stick_x.getValue();
  }


  @Override
    public double left_stick_y() {
    return disabled ? 0: left_stick_y.getValue();
  }


  @Override
    public double right_stick_x() { 
    return disabled ? 0: right_stick_x.getValue();
  }


  @Override
    public double right_stick_y() {
    return disabled ? 0: right_stick_y.getValue();
  }


  @Override
    public boolean left_bumper() {
    return disabled ? false: left_bumper.pressed();
  }


  @Override
    public boolean right_bumper() {
    return disabled ? false: right_bumper.pressed();
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
    return disabled ? false : y.pressed();
  }


  @Override
    public boolean a() {
    return disabled ? false : a.pressed();
  }
}
