package chabernac.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class JARResource extends AbstractResource {

  public JARResource(String anLocation) {
    super(anLocation);
  }

  public boolean exists() {
//    return Thread.currentThread().getContextClassLoader().getResource("/" + getLocation()) != null;
    return getClass().getClassLoader().getResource(getLocation()) != null;
  }

  public InputStream getInputStream() throws IOException {
    return getClass().getClassLoader().getResourceAsStream(getLocation());
  }

  public File getFile() {
    try {
      return new File(URLDecoder.decode(getClass().getClassLoader().getResource(getLocation()).getFile(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

}
