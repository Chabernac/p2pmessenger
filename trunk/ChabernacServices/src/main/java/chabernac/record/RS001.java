package chabernac.record;

import chabernac.synchro.SynchronizedEvent;
import chabernac.synchro.SynchronizedRecord;
import chabernac.synchro.event.LocationChangedEvent;

/**
 * record that contains screen location information
 *
 * @version v1.0.0      Mar 27, 2008
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Mar 27, 2008 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */

public class RS001 extends SynchronizedRecord{

  public void defineFields(){
    setField("PLAYER", 2, NUMERIC);
    setField("X", 4, NUMERIC);
    setField("Y", 4, NUMERIC);

  }

  public SynchronizedEvent getEvent() {
    return new LocationChangedEvent(getIntValue("PLAYER"), getIntValue("X"), getIntValue("Y"));  
  }
}
