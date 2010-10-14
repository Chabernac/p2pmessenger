package chabernac.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 * @version v1.0.0      Sep 9, 2008
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 9, 2008 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class URLResource extends AbstractResource {
  private static final long serialVersionUID = -8777595044372472087L;

  public URLResource(URL anURL) {
    super(anURL.toString());
  }
  
  public URLResource(String anLocation) {
    super(anLocation);
  }

  public boolean exists() {
    InputStream theStream = null;
    try {
      URL theURL = new URL(getLocation());
      theStream = theURL.openStream();
      return true;
    } catch (Exception e) {
      return false;
    } finally {
      if(theStream != null){
        try {
          theStream.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public File getFile() throws IOException {
    URL theURL = new URL(getLocation());
    return new File(theURL.getFile());
  }

  public InputStream getInputStream() throws IOException {
    URL theURL = new URL(getLocation());
    return theURL.openStream();
  }

  public OutputStream getOutputStream() throws IOException {
    return null;
  }
}
