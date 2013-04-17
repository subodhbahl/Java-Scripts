package com.test;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

public class TestAD {

public static void main (String[] args) {

try {
Hashtable env = new Hashtable();
//This works without DC and MD5hash
String username="subodhb";
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL,"LDAP://torcldc01.gadventures.internal:389"); 
//"DIGEST-MD5"
env.put(Context.SECURITY_AUTHENTICATION,"DIGEST-MD5"); 
env.put(Context.SECURITY_PRINCIPAL, username); 
env.put(Context.SECURITY_CREDENTIALS, "Il0vemym0m");

//Testing OD
/*
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL,"LDAP://torprod01.gadventures.internal:389"); 
env.put(Context.SECURITY_AUTHENTICATION,"simple"); 
env.put(Context.SECURITY_PRINCIPAL, "uid="+"subodhb" + ",cn=users,dc=lima,dc=gap,dc=ca"); 
env.put(Context.SECURITY_CREDENTIALS, "");
*/

//Testing AD with DC
/*
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, "ldap://torcldc01.gadventures.internal:389");
env.put(Context.SECURITY_AUTHENTICATION, simple");
env.put(Context.SECURITY_PRINCIPAL, "sAMAccountName=" + "aduser1" + ",cn=MigratedUsers,dc=gadventures,dc=Internal");
*/

DirContext ctx = new InitialDirContext(env);

if(ctx != null){
	System.out.println("NOT empty");
}

ctx.close();

} catch(NamingException ne) {
System.out.println("Error authenticating user:");
System.out.println(ne.getMessage());
return;
}

//if no exception, the user is already authenticated.
System.out.println("OK, successfully authenticating user");
}


}
