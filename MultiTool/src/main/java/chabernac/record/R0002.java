package chabernac.record;

import chabernac.record.Record;

/**
 * 
 * Request host adres of all users record
 *
 */

public class R0002 extends Record{
        
        public void defineFields(){
            setField("USER", 20, ALPHANUMERIC);
            setValue("USER", "ALL");
        }
    }