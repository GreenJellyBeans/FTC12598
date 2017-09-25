package gjb.interfaces;

/**
 * Created by josephj on 9/24/2017.
 */

public interface SubSystemInterface {

    // Perform  initialization activities.
    // Must not block.
    // FUTURE: conside making this async
    void init();

    // Perform deinitialization activities
    // Must not block.
    // FUTURE: consider making this async
    void deinit();

}
