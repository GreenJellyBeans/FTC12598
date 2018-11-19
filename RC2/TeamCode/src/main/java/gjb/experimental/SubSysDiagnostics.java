package gjb.experimental;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import gjb.interfaces.RuntimeSupportInterface;
import gjb.interfaces.SubSystemInterface;

public class SubSysDiagnostics implements SubSystemInterface {
    final public RuntimeSupportInterface rt;
    RevBlinkinLedDriver blinkinLedDriver;
    RevBlinkinLedDriver.BlinkinPattern pattern;

    Telemetry.Item patternName;
    Telemetry.Item display;
    //DisplayKind displayKind;
    Deadline ledCycleDeadline;
    Deadline gamepadRateLimit;

    //getBlinkinDriver

    public SubSysDiagnostics(RuntimeSupportInterface rt) {
        this.rt = rt;
    }

    @Override
    public void init() {
        blinkinLedDriver = rt.hwLookup().getBlinkinDriver("blinkin");
        pattern = RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE;
        blinkinLedDriver.setPattern(pattern);

    }

    @Override
    public void deinit() {
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    protected void goBlack()
    {
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

}
