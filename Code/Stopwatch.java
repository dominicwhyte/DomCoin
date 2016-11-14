/*************************************************************************
 * Name:
 * Login:
 * Precept:
 *
 * Description: This is a stopwatch! Ready, set, go!
 *************************************************************************/

public class Stopwatch { 

    // start keeps track of the time the stopwatch was created
    private long start;

    // constructors initialize instance variables
    public Stopwatch() {
        start = System.currentTimeMillis();
    } 

    // return time (in seconds) since this object was created
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    } 
}
