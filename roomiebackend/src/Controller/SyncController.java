package Controller;

/**
 * Controls operations that are done intermittently outside the server's logic. E.G Precalculating values, checking
 * the database for certain conditions.
 */
public class SyncController{
    public SyncController() throws InterruptedException {
        MatchingPriorityController mpc = new MatchingPriorityController(); // For finding user similarities
        MatchingScannerController msc = new MatchingScannerController();
        mpc.start();
        msc.start(); // Every 15 seconds
        /**
         * Calculate similarities between users for 1 second, wait 10 seconds, then repeat.
         * For the future: make these wait values variable and dynamic (some change)
         */
        while (true) {
            Thread.sleep(1000);
            mpc.pauseThread();
            Thread.sleep(10000);
            mpc.resumeThread();
        }
    }
}
