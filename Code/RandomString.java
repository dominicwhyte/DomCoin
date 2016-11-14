/*************************************************************************
  * Name: Dominic Whyte
  *
  * Code modified from:
  * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
  * 
  * Description: Outputs the SHA-256 hash of a given String
  *  
  * 
  *****************************************************************************/

import java.util.Random;
public class RandomString {
    //ascii alphabet
    static final String AB = " !#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    static Random rnd = new Random();
    public static String randomString(int len){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ ) 
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
    //tester method
    public static void main(String[] args) {
        
        StdOut.println(randomString(3));
        
    }
}