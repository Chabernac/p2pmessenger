package chabernac.record;

import chabernac.record.Record;

/**
 * 
 * Announce dead of user record
 *
 */

public class R0003 extends Record{
        
        public void defineFields(){
            setField("USER", 20, ALPHANUMERIC);
        }
    }