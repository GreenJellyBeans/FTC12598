/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/24/2017.
 */
package gjb.utils;

/*
Collection of utility methods, mostly static methods.
 */
public class GjbUtils {

    // Returns true IFF {a} and {b} are within {tolerance} apart.
    public static boolean closeEnough(double a, double b, double tolerance) {
        return Math.abs(a-b) <= tolerance;
    }

}
