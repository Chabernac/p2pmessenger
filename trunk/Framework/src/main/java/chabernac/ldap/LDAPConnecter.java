/*
 * Copyright (c) 1998 Anhyp, NV. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Anhyp.
 *
 */

package chabernac.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

/**
 *
 *
 * @version v1.0.0      Dec 29, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Dec 29, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class LDAPConnecter {
  
  private DirContext myContext = null;
  
  public LDAPConnecter(String aProvider) throws NamingException{
    Hashtable ldapProperties = new Hashtable();
    ldapProperties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
//    ldapProperties.put(Context.PROVIDER_URL,"ldap://ldapuser.axa.be:1034/ou=homebanking,ou=prd,ou=Services,o=axa.be");
    ldapProperties.put(Context.PROVIDER_URL,aProvider);
    ldapProperties.put(Context.SECURITY_AUTHENTICATION,"simple");
    //ldapProperties.put(Context.SECURITY_PRINCIPAL,"uid=minerva,ou=Administrative Users,o=axa.be");
    //ldapProperties.put(Context.SECURITY_CREDENTIALS,"m!n3rvA");
    myContext = new InitialDirContext(ldapProperties);
  }
  
  public void list(String aNode) throws NamingException{
    NamingEnumeration theEnum = myContext.list(aNode);
    while(theEnum.hasMoreElements()) System.out.println(theEnum.next());
  }
  
  public NamingEnumeration search(String aNode, String aFilter) throws NamingException{
    return myContext.search(aNode, aFilter, new SearchControls());
    
  }
  
  public LdapContext lookup(String aNode) throws NamingException{
    return (LdapContext)myContext.lookup(aNode);
  }
  
  
  public static void main(String args[]){
    //ldap://ldapuser.axa.be:1034/ou=customers,ou=people,o=axa.be??sub?(uid=*.vanoosterwyck.*)

    try{ 
      LDAPConnecter theConnector = new LDAPConnecter("ldap://ldapuser.axa.be:1034/o=axa.be");
      //LDAPConnecter theConnector = new LDAPConnecter("ldap://stg.ldap.axa.be:1034/o=axa.be");
      //LDAPConnecter theConnector = new LDAPConnecter("ldap://ldapuser.axa.be:1034/ou=customers,ou=people,o=axa.be??sub?(uid=*.vanoosterwyck.*)");
      

      //theConnector.list("ou=Customers,ou=People");
      //theConnector.list("ou=customers,ou=people");
      //theConnector.list("ou=customers,ou=people??sub?(uid=*.vanoosterwyck.*");
      //theConnector.list("ou=PTUsers");
      NamingEnumeration theEnumeration = theConnector.search("ou=customers,ou=people", "(& (uid=*.chauliac*.*))");
      viewDetails(theEnumeration);
      //NamingEnumeration theEnumeration = theConnector.search("ou=customers,ou=people", "(& (customerid>=0) (customerid<=100))");
      //NamingEnumeration theEnumeration = theConnector.search("ou=customers,ou=people", "(& (customerid>=015200000) (customerid<=015200100))");
      //extractExpired(theEnumeration);
      //theConnector.search("ou=customers,ou=people", "(& (certnotafter<=2005-12-30 11:30:00.000))");
      //"(&(sn=Geisel)(mail=*))
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public static void viewDetails(NamingEnumeration anEnumeration) throws NamingException{
    while(anEnumeration.hasMoreElements()) {
      SearchResult theResult = (SearchResult)anEnumeration.next();
      Attributes theAttributes = theResult.getAttributes();
      NamingEnumeration theAttributesList = theAttributes.getAll();
      while(theAttributesList.hasMoreElements()){
        System.out.println(theAttributesList.next().toString());
      }
    }
  }
  
  public static void extractExpired(NamingEnumeration anEnumeration) throws NamingException{
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    Date now = new Date();
    
    while(anEnumeration.hasMoreElements()) {
      SearchResult theResult = (SearchResult)anEnumeration.next();
      Attributes theAttributes = theResult.getAttributes();
      //System.out.println(theAttributes);
      Attribute theExpiry = theAttributes.get("certnotafter");
      if(theExpiry != null){
        try{
          Date theDate = formatter.parse((String)theExpiry.get());
          if(theDate.before(now)){
            Attribute theCustomer = theAttributes.get("objectClass");
            if(theCustomer != null) System.out.println("Customer: " + theCustomer.get().getClass() + " " + theCustomer);
            System.out.println("Expired certificate: " + theAttributes.toString());
          }
        }catch(ParseException e){
          e.printStackTrace();
        }
      } else {
        //System.out.println(theResult);
      }
    }
  }
  
}
