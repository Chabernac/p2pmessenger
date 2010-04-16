package chabernac.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 *
 * @version v1.0.0      Sep 22, 2008
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 22, 2008 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class ObjectDataSource implements DataSource {
  private Object myObject = null;
  
  public ObjectDataSource(Object anObject){
    myObject = anObject;
  }

  public String getContentType() {
    return "application/octet-stream";
  }

  public InputStream getInputStream() throws IOException {
    ByteArrayOutputStream theByteArray = new ByteArrayOutputStream();
    ObjectOutputStream theOutputStream = new ObjectOutputStream(theByteArray);
    theOutputStream.writeObject(myObject);
    byte[] theBytes = theByteArray.toByteArray();
    return new ByteArrayInputStream(theBytes);
  }

  public String getName() {
    return Integer.toString(myObject.hashCode());
  }

  public OutputStream getOutputStream() throws IOException {
    return null;
  }

}
