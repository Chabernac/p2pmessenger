/*
 * Created on Feb 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package chabernac.record;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author D1DAB1L
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Record {
    public static final int NUMERIC = 1;
    public static final int ALPHANUMERIC = 2;
    
    private Vector myFields = null;
    private Hashtable myContent = null;
    private int myTotalLength = 0;
    
    public Record(){
        myFields = new Vector();
        myContent = new Hashtable();
        defineFields();
    }
    
    protected void defineFields(){}
    
    public final void setField(String aName, int aLength, int aType){
        Field theField = new Field(aName, aLength, aType);
        myFields.addElement(theField);
        myContent.put(aName, theField);
        myTotalLength += aLength;
    }
    
    public final void setContent(byte[] aByteArray){
        int curPosition = 0;
        Iterator theIterator = myFields.iterator();
        Field theField = null;
        while(theIterator.hasNext()){
            theField = (Field)theIterator.next();
            byte[] theBytes = theField.getValue();
            System.arraycopy(aByteArray, curPosition, theBytes, 0, theField.getLength());
            curPosition += theField.getLength();
        }
    }
    
    public final byte[] getContent(){
        int curPosition = 0;
        byte[] theBytes = new byte[myTotalLength];
        Iterator theIterator = myFields.iterator();
        Field theField = null;
        while(theIterator.hasNext()){
            theField = (Field)theIterator.next();
            byte[] theSource = theField.getValue();
            System.arraycopy(theSource, 0, theBytes, curPosition, theField.getLength());
            curPosition += theField.getLength();
        }
        return theBytes;
    }
    
    
    public final void setValue(String aKey, double aValue){
        setValue(aKey, Double.toString(aValue));   
    }
    
    public final void setValue(String aKey, long aValue){
        setValue(aKey, Long.toString(aValue));
    }
    
    public final void setValue(String aKey, String aValue){
        setValue(aKey, aValue.getBytes());
    }
    
    public final void setValue(String aKey, byte[] aValue){
        getField(aKey).setValue(aValue);
    }
    
    public final byte[] getValue(String aKey){
        return getField(aKey).getValue();
    }
    
    private final Field getField(String aKey){
        return (Field)myContent.get(aKey);
    }
    
    public final String getStringValue(String aKey){
        //System.out.println("Key: " + new String(getValue(aKey)));
        return new String(getValue(aKey)).trim();
    }
    
    public final long getLongValue(String aKey){
        return Long.parseLong(getStringValue(aKey));
    }
    
    public final double getDoubleValue(String aKey){
        return Double.parseDouble(getStringValue(aKey));
    }
    
    
    private class Field{
        private static final byte SPACE = ' ';
        private static final byte ZERO = '0';
        
        private String myName;
        private int myLength;
        private byte[] myValue;
        private int myType;
        private byte myClearToken;
        
        public Field(String aName, int aLength, int aType){
            myName = aName;
            myLength = aLength;
            myType = aType;
            myValue = new byte[aLength];
            setClearToken();
            clear();
        }
        
        public String getName(){ return myName; }
        public int getLength(){ return myLength; }
        public byte[] getValue(){ return myValue; }
        
        public void setValue(byte[] aValue){
            //System.out.println("Value: " + new String(aValue));
            //System.out.println("Offset: " + getOffset(aValue.length));
            //System.out.println("Length: " + (aValue.length < myLength ? aValue.length : myLength));
            System.arraycopy(aValue, 0, myValue, getOffset(aValue.length), aValue.length < myLength ? aValue.length : myLength);
        }
        
        private int getOffset(int aLength){
            if(myType == ALPHANUMERIC) return 0;
            if(aLength > myLength) return 0;
            return (myLength - aLength);
        }
        
        private void setClearToken(){
            if(myType == NUMERIC) myClearToken = ZERO;
            else myClearToken = SPACE;
        }
        
        private void clear(){
            for(int i=0;i<myValue.length;i++){ myValue[i] = myClearToken; }
        }
    }

}

