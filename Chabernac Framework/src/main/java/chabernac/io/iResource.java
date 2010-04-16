package chabernac.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface iResource {
  public boolean exists();
  public InputStream getInputStream() throws IOException;
  public File getFile() throws IOException;
}
