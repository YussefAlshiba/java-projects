/* Timer:                                                    HDO, 2006-03-20 */
/* ------                                                                    */
/* Simple timer class.                                                       */
/* ========================================================================= */

public class Timer {

    private static long startedAt, stoppedAt;

    public static void startTimer() {
        startedAt = System.currentTimeMillis();
    }
    
    public static void stopTimer() {
        stoppedAt = System.currentTimeMillis();
    }

    public static String elapsedTime() {
        return (stoppedAt - startedAt) + " ms";
    }
    
}
