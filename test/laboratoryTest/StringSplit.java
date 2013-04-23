

package laboratoryTest;

import java.util.Arrays;

public class StringSplit {
  public static void main(String args[]) throws Exception{
    String testString = "Real-How-To";
    String orderItems=java.util.Arrays.toString(
            testString.split("-"));
    System.out.println(
        java.util.Arrays.toString(
        testString.split("-")));
        
    
    

    // output : [Real, How, To]
    }
}