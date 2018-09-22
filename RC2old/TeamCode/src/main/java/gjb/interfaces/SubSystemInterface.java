/**
 * Created by github.com/josephmjoy (mentor for FTC#12598 & FRC#1899) on 9/24/2017.
 */
package gjb.interfaces;



public interface SubSystemInterface {

    // Perform  initialization activities.
    // Must not block.
    // FUTURE: consider making this async
    void init();

    // Perform deinitialization activities
    // Must not block.
    // FUTURE: consider making this async
    void deinit();

}
