package gjb.experimental;
import java.util.ArrayList;

/**
 * Created by josephj on 9/16/2017.
 */

// Generates time delays for morse code from text strings.
class MorseGenerator {
    final int DOT_DELAY = 4;
    final int DASH_DELAY = 12;
    final int LETTERGAP_DELAY = 28; // between dlett
    final int GAP_DELAY = 4; // between symbols

    final String[] morse  = {
            ".-", "-...", "-.-.", "-..", ".", // ABCDE
            "..-.", "--.", "....", "..", ".---", // FGHIJ
            "-.-", ".-..", "--", "-.", "---", // KLMNO
            ".--.", "--.-", ".-.", "...", "-", // PQRST
            "..-", "...-", ".--", "-..-", "-.--", // UVWXY
            "--.."
    };

    // Generate time delays for string {s}
    public int[] generateDelays(String s, int startDelay) {
        ArrayList<Integer> delays = new ArrayList<Integer>();
        assert(morse.length == 26);
        int delay = startDelay;
        s = s.toUpperCase();
        delays.add(startDelay); // Initial off
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c<'A' || c>'Z') {
                c = 'X'; // invalid char
            }

            String code = morse[c - 'A'];
            for (int j = 0; j < code.length(); j++) {
                char d = code.charAt(j);
                delay = (d == '-') ? DASH_DELAY : DOT_DELAY;
                delays.add(delay); // lights on
                delays.add(GAP_DELAY); // lights off
            }

            int last = delays.size()-1;
            delays.set(last, delays.get(last) + LETTERGAP_DELAY) ; // SHould be adding to off time
        }
        int[] delayArray = new int[delays.size()];
        for (int i = 0; i< delayArray.length; i++) {
            delayArray[i] = delays.get(i);
        }
        return delayArray;
    }
}
