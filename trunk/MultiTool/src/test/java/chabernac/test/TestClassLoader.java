package chabernac.test;

import java.io.File;

import javax.imageio.ImageIO;

public class TestClassLoader {

  public static void main(String[] args) {
    try {
      File theFile = new File("images/calendar2.gif");
      if(theFile.exists()){
        System.out.println("Olé");
        ImageIO.read(TestClassLoader.class.getClassLoader().getSystemResourceAsStream("images/calendar2.gif"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
