package chabernac.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * <pre>
 * Use this class if you want to load a resource from the classpath
 * 
 * e.g.
 * 
 * iResource theWebXML = new ClassPathResource("be/axa/fi/io/ClassPathResource.class");
 * InputStream theInputStream = theWebXML.getInputStream();
 * 
 * </pre>
 *
 * @version v1.0.0      20-nov-08
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 20-nov-08 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class ClassPathResource extends AbstractResource {

  private static final long serialVersionUID = -9047338542319433212L;

  public ClassPathResource(String anLocation) {
    super(anLocation.replace('\\', '/'));
    if(anLocation.length() > 0 && anLocation.charAt(0) == '/'){
      setLocation(anLocation.substring(1));
    }
  } 

  public boolean exists() {
    String theLocation = getLocation();
    
    if(getClass().getClassLoader().getResource(theLocation) != null){
      return true;
    }
    
    if(theLocation.length() > 0 && theLocation.indexOf(0) != '/'){
      theLocation = "/" + theLocation;
    }
    
    if(getClass().getClassLoader().getResource(theLocation) != null){
      setLocation(theLocation);
      return true;
    }
    
    return false;
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
  
  public OutputStream getOutputStream() throws IOException {
    return null;
  }
}
