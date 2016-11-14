/*************************************************************************
  * Name: Dominic Whyte
  *
  * Description: Mines for a key which, when concatenated to the end of a given
  * String, can be hashed using SHA-256 to yield a String with N leading zeros
  *  
  * 
  *****************************************************************************/

public class Miner {
    //checks if a given String has at least N leading zeros
    public static Boolean checksuccess(int N, String key) {
        //check the first N digits and if any of them are not zero (char 48),
        //return false. Else return true
        for(int i = 0; i < N; i++){
            if (((int) key.charAt(i)) != 48) {
                return false;
            }
        }
        return true;
        
    }
    //takes number of required leading zeros N and String code to which key will
    //be concatenated with and returns the key which yields a code + key 
    //concatenation with at least N leading zeros
    //Warning: this will take a considerable amount of time depending on how 
    //large N is (as it is supposed to: this is a "proof of work")
    public static String findkey(int N, String code) {
        Boolean success = false; //Has the right hash been found?
        String key = ""; //the current key being tested for success
        int i = 0; //the trial number
        //run until success has been achieved
        //a list of ascii characters
        String ascii = " !#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        while(!success) {
            i++; //increment which trial we are on 
            int digits = 1; //how long the test key is
            //digits should be 1 for ascii.length() times, 2 for (ascii.length())
            //squared times, etc. (so you try more random keys with more random
            //digits)
            boolean bool = true;
            while(bool) {
                //if ascii.length()^digits < i, then increment digits
                //Use change of log base rule for this
                if (((double) Math.log(i)/Math.log((double)ascii.length())) > digits) {
                    digits++;
                }
                else 
                    bool = false;
            }
            //set the key to be a random String from ascii alphabet with length
            //digits
            key = RandomString.randomString(digits);
            //If the concatenation of the code and the possible key yields a 
            //hash with at least N leading zeros, deem the key a success
            if(checksuccess(N, Sha256.hash(code + key))) {
                success = true;
            }
        }
        //StdOut.println("Trials: " + i);
        return key;
        
    }
    
    public static void main(String[] args) {
        //start timer
        Stopwatch timer = new Stopwatch();
        //number of leading zeros required
        int N = Integer.parseInt(args[0]);
        //code to which key will be concatenated with
        String code = args[1];
        //print out the key found by the method findkey
        System.out.println(findkey(N, code));
        //print time taken
        System.out.println("Time elapsed: " + timer.elapsedTime());
    }
}
