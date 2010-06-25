package chabernac.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.activation.DataSource;

public interface iResource extends DataSource, Serializable {
  public boolean exists();
  public InputStream getInputStream() throws IOException;
  public File getFile() throws IOException;
} 
