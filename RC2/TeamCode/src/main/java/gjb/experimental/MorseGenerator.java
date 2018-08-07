/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/16/2017.
 */
package gjb.experimental;
import java.util.ArrayList;

// Generates time delays for morse code from text strings.
class MorseGenerator {
    // These are relative times.
    final int DOT_TIME = 1;
    final int DASH_TIME = 3;
    final int LETTERGAP_TIME = 7; // between dlett
    final int GAP_TIME = 1; // between symbols

    final String[] morse  = {
            ".-", "-...", "-.-.", "-..", ".", // ABCDE
            "..-.", "--.", "....", "..", ".---", // FGHIJ
            "-.-", ".-..", "--", "-.", "---", // KLMNO
            ".--.", "--.-", ".-.", "...", "-", // PQRST
            "..-", "...-", ".--", "-..-", "-.--", // UVWXY
            "--.."
    };

    // Generate time delays for string {s}. {dotWidth} is the time
    // for one dot. An initial delay of 0 is inserted first - this is
    // a "switch off" transition.
    public double[] generateDelays(String s, double dotTime) {
        final double frac = dotTime/DOT_TIME;
        ArrayList<Double> delays = new ArrayList<Double>();
        assert(morse.length == 26);
        double delay = 0;
        s = s.toUpperCase();
        delays.add(0.0); // Initial off
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c<'A' || c>'Z') {
                c = 'X'; // invalid char
            }

            String code = morse[c - 'A'];
            for (int j = 0; j < code.length(); j++) {
                char d = code.charAt(j);
                delay = (d == '-') ? frac*DASH_TIME : frac*DOT_TIME;
                delays.add(delay); // lights on
                delays.add(frac*GAP_TIME); // lights off
            }

            int last = delays.size()-1;
            delays.set(last, delays.get(last) + frac*LETTERGAP_TIME) ; // SHould be adding to off time
        }
        double[] delayArray = new double[delays.size()];
        for (int i = 0; i< delayArray.length; i++) {
            delayArray[i] = delays.get(i);
        }
        return delayArray;
    }
}
