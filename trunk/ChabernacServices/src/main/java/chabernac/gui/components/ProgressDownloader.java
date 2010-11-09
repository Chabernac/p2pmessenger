package chabernac.gui.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

public class ProgressDownloader extends JFrame
{
  private static final Logger LOGGER = Logger.getLogger(ProgressDownloader.class);
  private JLabel status = null;
  private JProgressBar progressBar = null;

  public ProgressDownloader()
  {
    initialize();
    setupGui();
  }

  private void initialize()
  {
    status = new JLabel("Not downloading");
    status.setHorizontalAlignment(SwingConstants.CENTER);
    status.setVerticalAlignment(SwingConstants.CENTER);

    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setValue(0);

  }

  private void setupGui()
  {
    Container container = getContentPane();
    container.setLayout(new GridLayout(2,1));
    container.add(status,BorderLayout.NORTH);
    container.add(progressBar,BorderLayout.SOUTH);
    setSize(300,90);
    Dimension dimension = (Toolkit.getDefaultToolkit()).getScreenSize();
    setLocation((int)(dimension.getWidth()/2-150),(int)(dimension.getHeight()/2-45));
    //pack();
  }

  public void download(URL url, File file, long length)
  {
    long bytesRead = 0;
    InputStream theInputStream = null;
    FileOutputStream theFileOutputStream = null;

    try
    {
      theInputStream = url.openStream();
      theFileOutputStream = new FileOutputStream(file);
      status.setText("Downloading " + file.getName() + "...");
      byte[] theBytes = new byte[1028];
      int nrBytesRead;
      while((nrBytesRead = theInputStream.read(theBytes))!=-1)
      {
        theFileOutputStream.write(theBytes,0,nrBytesRead);
        bytesRead = bytesRead + nrBytesRead;
        progressBar.setValue((int)(100 * bytesRead/length));
      }
      status.setText(file.getName() + " downloaded");

      //Debug.log(this,"Module: " + name  + " downloaded successfully");
    }catch(Exception e){
      LOGGER.error("Could not download module: " + file.toString(),e);
    }
    finally
    {
      if(theInputStream!=null)
      {
        try
        {
          theInputStream.close();
        }catch(Exception e){
          LOGGER.error("Error occured while closing inputstream",e);
        }
      }
      if(theFileOutputStream!=null)
      {
        try
        {
          theFileOutputStream.flush();
          theFileOutputStream.close();
        }catch(Exception e){
          LOGGER.error("Error occured while closing outputstream",e);
        }
      }
    }
    dispose();

  }

}

