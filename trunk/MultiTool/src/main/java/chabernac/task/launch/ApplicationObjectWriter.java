package chabernac.task.launch;

import java.io.File;

import chabernac.io.IOOperator;
import chabernac.updater.Application;
import chabernac.updater.Version;

public class ApplicationObjectWriter {

  public static void main(String[] args) {
    Application theHeavyApplication = new Application();
    theHeavyApplication.setName("Heavy");
    theHeavyApplication.setMain("chabernac.task.launch.ApplicationLauncher");
    theHeavyApplication.addJar("log4j-1.2.6.jar");
    theHeavyApplication.addJar("sheduler.jar");
    theHeavyApplication.addJar("chservices.jar");
    theHeavyApplication.addJar("3dengine.jar");
    theHeavyApplication.addJar("framework.jar");
    theHeavyApplication.addJar("mail.jar");
    theHeavyApplication.addJar("activation.jar");
    theHeavyApplication.setDebugEnabled(false);
    IOOperator.saveObject(theHeavyApplication, new File("applications/heavy.bin"));
    
    Application theLightApplication = new Application();
    theLightApplication.setName("Light");
    theLightApplication.setMain("chabernac.task.launch.ApplicationLauncherLight");
    theLightApplication.addJar("log4j-1.2.6.jar");
    theLightApplication.addJar("sheduler.jar");
    theLightApplication.addJar("chservices.jar");
    theLightApplication.addJar("3dengine.jar");
    theLightApplication.addJar("framework.jar");
    theLightApplication.addParameter("service", "false");
    theLightApplication.setDebugEnabled(false);
    IOOperator.saveObject(theLightApplication, new File("applications/light.bin"));
    
    Application theServiceApplication = new Application();
    theServiceApplication.setName("Service");
    theServiceApplication.setMain("chabernac.task.launch.ApplicationLauncherLight");
    theServiceApplication.addJar("log4j-1.2.6.jar");
    theServiceApplication.addJar("sheduler.jar");
    theServiceApplication.addJar("chservices.jar");
    theServiceApplication.addJar("3dengine.jar");
    theServiceApplication.addJar("framework.jar");
    theServiceApplication.addParameter("service", "true");
    theServiceApplication.setDebugEnabled(false);
    IOOperator.saveObject(theServiceApplication, new File("applications/service.bin"));
    
    Version theVersion = new Version("4.1.35");
    IOOperator.saveObject(theVersion, new File("applications/version.bin"));
    
  }
}
