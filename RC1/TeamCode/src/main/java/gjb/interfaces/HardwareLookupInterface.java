/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/26/2017.
 */
package gjb.interfaces;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.AnalogOutputController;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;


/*
DcMotor - interface that extends an interface that extends an interface
DcMotor extends DcMotorSimple
DcMotorSimple extends HardwareDevice
Servo extends HardwareDevice
Public class AnalogOutput implements HardwareDevice // simple class that internally keeps an AnalogOutputController.
Public interface AnalogOutputController extends HardwareDevice
public interface ColorSensor extendsHardwareDevice
public interface NormalizedColorSensor extends HardwareDevice
public interface DigitalChannel extendsHardwareDevice (touch sensor example uses a DigitalChannel)

MR's CDM:
public interface DeviceInterfaceModule extends DigitalChannelController,AnalogInputController,PWMOutputController,I2cController,AnalogOutputController{
public interface OpticalDistanceSensor extends LightSensor
public interface LightSensor extends HardwareDevice

This one's a class:
publicclassModernRoboticsI2cRangeSensorextendsI2cDeviceSynchDevice<I2cDeviceSynch>implementsDistanceSensor,OpticalDistanceSensor,I2cAddrConfig

 */

public interface HardwareLookupInterface {
    DeviceInterfaceModule getDeviceInterfaceModule(String name);
    AnalogInput getAnalogInput(String name);
    AnalogInputController getAnalogInputController(String name);
    AnalogOutput getAnalogOutput(String name);
    AnalogOutputController getAnalogOutputController(String name);
    ColorSensor getColorSensor(String name);
    DcMotor getDcMotor(String name);
    DigitalChannel getDigitalChannel(String name);
    Servo getServo(String name);
}
