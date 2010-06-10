package chabernac.record;

import chabernac.record.Record;

/**
 * 
 * Request host adres of 1 user record
 *
 */

public class R0001 extends Record{
        
        public void defineFields(){
            setField("USER", 20, ALPHANUMERIC);
        }
    }