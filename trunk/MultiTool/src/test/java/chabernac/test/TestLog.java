package chabernac.test;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TestLog {
  private static Logger logger = Logger.getLogger(TestLog4j.class);
  public static void main(String args[]){
    try{
      Properties theProps = new Properties();
      theProps.load(new FileInputStream("log4j.properties"));
      PropertyConfigurator.configure(theProps);
      logger.debug("psst");
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
