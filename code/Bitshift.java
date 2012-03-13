/******************************  Bitshift.java  *******************************/

/**
 * @version 15.02.12
 *
 * @author manschwa
 */

public class Bitshift {

  public static void main(String[] argv) {
    int flag = 0;
    for(int i = 0; i < 8; i++)
    {
        System.out.println("Flag: " + flag);
        flag |= 1<<i;
    }

    System.out.println("Flag: " + flag);
  }
}

