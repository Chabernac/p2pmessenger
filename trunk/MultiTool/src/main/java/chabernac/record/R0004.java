package chabernac.record;

import chabernac.record.Record;

/**
 * 
 * Announcement packet record
 *
 */

public class R0004 extends Record{
        
        public void defineFields(){
            setField("USER", 20, ALPHANUMERIC);
            setField("HOST", 15, ALPHANUMERIC);
            setField("DATA", 100, ALPHANUMERIC);
        }
    }