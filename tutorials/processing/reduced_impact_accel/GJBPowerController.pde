// Incrementally increases/decreases power to avoid abrupt power transitions to motors.
// By JMJ,Mentor for FTC team #12598
class GJBPowerController {

  final double minStartPower;
  final double maxIncrease;
  final double maxDecrease;

  // Power jumps to {minStartPower} so we don't waste time reaching that level. Beyond {minStartPower},
  // absolute power increases (away from 0) by {maxIncrease} and decreases (towards 0) by {maxDecrease}.
  // No state is maintained.
  public GJBPowerController(double minStartPower, double maxIncrease, double maxDecrease) {
    this.minStartPower = minStartPower;
    this.maxIncrease = maxIncrease;
    this.maxDecrease = maxDecrease;
  }

  public double calculatePowerIncrement(double currentPower, double targetPower) {
    double speedDelta = targetPower-currentPower;
    double maxIncrement;
    double absCurrentPower = Math.abs(currentPower); 
    double absTargetPower  = Math.abs(targetPower);
    boolean powerIsIncreasing = absTargetPower > absCurrentPower;
    if (powerIsIncreasing) {
      if (absCurrentPower < minStartPower) {
        // if power is very low - below minStartPower, we jump to minStartPower
        maxIncrement = minStartPower;
      } else {
        maxIncrement = maxIncrease;
      }
    } else {
      // power is decreasing...
      maxIncrement = maxDecrease;
    }

    // This makes sure we increment the right amount even if speedDelta is negative.
    speedDelta = clipToRange(speedDelta, -maxIncrement, maxIncrement);

    return speedDelta;
  }

  // Clips value {in} to within interval [min, max]
  double clipToRange(double in, double min, double max) {
    return Math.max(Math.min(in, max), min);
  }
  
}