package chabernac.ldapuserinfoprovider;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

/**
 *
 * @version v1.0.0      16-dec-08
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 16-dec-08 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:Guy.Chauliac@axa.be"> Guy Chauliac </a>
 */
public class AXALDapTools {
  private static final String PROVIDER="ldap://user.dir:1034/o=axa.be";
  private static AXALDapTools instance = null;

  private InitialLdapContext myDirContext = null;


  private AXALDapTools() throws NamingException{
    initContext();
  }

  public static synchronized AXALDapTools getInstance() throws NamingException{
    if(instance == null){
      instance = new AXALDapTools();
    }
    return instance;
  }

  private void initContext() throws NamingException{
    Hashtable theEnvironment = new Hashtable(11);
    theEnvironment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
    theEnvironment.put(Context.PROVIDER_URL, PROVIDER);
    theEnvironment.put(Context.SECURITY_AUTHENTICATION, "none");
    myDirContext = new InitialLdapContext(theEnvironment, null);
  }
  
  public boolean uidAuthentication(String anUID, String aPassword) throws NamingException{
    Hashtable theEnvironment = new Hashtable(11);
    theEnvironment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
    theEnvironment.put(Context.PROVIDER_URL, PROVIDER);
    theEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");

    theEnvironment.put(Context.SECURITY_PRINCIPAL, "uid=" + anUID + ",ou=Employees,ou=People,o=axa.be");
    theEnvironment.put(Context.SECURITY_CREDENTIALS, aPassword);
    DirContext theContext = null;
    try{
      theContext = new InitialDirContext(theEnvironment);
      return true;
    }catch(AuthenticationException e){
      return false;
    } finally {
      if(theContext != null){
        theContext.close();
      }
    }
  }

  public NamingEnumeration searchUser(String aSearhAttribute, String aSearchValue) throws NamingException{
    SearchControls srchControls = new SearchControls();
    //srchControls.setReturningAttributes(new String[]{"uid"});
    srchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    String searchFilter = "(" + aSearhAttribute + "=" +   aSearchValue  + ")";

    NamingEnumeration theEnum = myDirContext.search("ou=Employees,ou=People", searchFilter, srchControls);
    if(!theEnum.hasMoreElements()){
      //maybe it's an extnernal 
      theEnum = myDirContext.search("ou=Externals,ou=People", searchFilter, srchControls);
    }
    return theEnum;
  }


  public String searchUserUID(String aSearhAttribute, String aSearchValue) throws NamingException{
    SearchControls srchControls = new SearchControls();
    srchControls.setReturningAttributes(new String[]{"uid"});
    srchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    String searchFilter = "(" + aSearhAttribute + "=" +   aSearchValue  + ")";

    NamingEnumeration theEnum = myDirContext.search("ou=Employees,ou=People", searchFilter, srchControls);

    if(!theEnum.hasMoreElements()){
      return null;
    }

    SearchResult theResult = (SearchResult)theEnum.nextElement();
    Attribute theAttribute = theResult.getAttributes().get("uid");
    String theUID = theAttribute.get().toString();
    return theUID;
  }

  public boolean intranetAuthentication(String anIntranetUser, String aPassword) throws NamingException{
    String theUID = searchUserUID("axauid", anIntranetUser);
    return uidAuthentication(theUID, aPassword);
  }

  public boolean mainframeAuthentication(String aMainframeUser, String aPassword) throws NamingException{
    String theUID = searchUserUID("axauidbank", aMainframeUser);
    return uidAuthentication(theUID, aPassword);
  }

}
