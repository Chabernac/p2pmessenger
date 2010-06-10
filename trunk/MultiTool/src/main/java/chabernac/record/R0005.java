package chabernac.record;


/**
 * 
 * Announcement packet record
 *
 */

public class R0005 extends Record{
        
        public void defineFields(){
            setField("VERSION", 10, ALPHANUMERIC);
            setField("PORT", 6, NUMERIC);
            setField("FIRST_NAME", 20, ALPHANUMERIC);
            setField("LAST_NAME", 20, ALPHANUMERIC);
            setField("STATUS", 1, NUMERIC);
        }
    }