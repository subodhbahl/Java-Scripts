package com.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.Context;  
import javax.naming.NamingEnumeration;  
import javax.naming.NamingException;  
import javax.naming.directory.Attributes;  
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;  
import javax.naming.directory.SearchResult;  
import javax.naming.ldap.InitialLdapContext;  
import javax.naming.ldap.LdapContext;  
import javax.net.ssl.SSLSocketFactory;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.security.auth.UserPrincipal;

public class testOD {

public static void main (String[] args) {

try {
	Hashtable env = new Hashtable();
	//This works without DC and MD5hash
	String username="tester5";
	String password = "pass5";
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://10.11.0.50:389");
	env.put(Context.SECURITY_AUTHENTICATION, "simple");
	env.put(Context.SECURITY_PRINCIPAL, "uid=" + username + ",cn=users,dc=lima,dc=gap,dc=ca");
	env.put(Context.SECURITY_CREDENTIALS, "pass5");

	DirContext ctx = new InitialDirContext(env);
	if(ctx != null){
			System.out.println("NOT empty");
			//This is where they connect			
			writeToAD(username, password);
		}
			ctx.close();

	} 
	catch(NamingException ne) {
    System.out.println("Error authenticating user:");
    System.out.println(ne.getMessage());
    return;
}

	//if no exception, the user is already authenticated.
	System.out.println("OK, successfully authenticating user");
}


private static void writeToAD(String username, String password) {

	// TODO Auto-generated method stub
	String user = username;
	String pass = password;
	System.out.println(user);
	
	try {  	
		Hashtable env = new Hashtable();
		String username1="CN=Password Update User, OU=ServiceAccounts, DC=gadventures, DC=internal";
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL,"LDAPS://10.11.0.37:636"); 
		env.put(Context.SECURITY_AUTHENTICATION,"simple"); 
		String keystore = "/System/Library/Java/Support/CoreDeploy.bundle/Contents/Home/Lib/security/cacerts";
		System.setProperty("javax.net.ssl.trustStore",keystore);
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_PRINCIPAL, username1); 
		env.put(Context.SECURITY_CREDENTIALS, "wink584;cool");
		//env.put(Context.SECURITY_PROTOCOL, "ssl");
		DirContext ctx = new InitialDirContext(env);
     
   	 if(ctx != null){
   		 System.out.println("NOT empty AD");
      	 
   		 SearchControls constraints = new SearchControls();  
         constraints.setSearchScope(SearchControls.SUBTREE_SCOPE); 
         constraints.setTimeLimit(100000);
         String[] attrIDs = { "distinguishedName",  
                 "sn",  
                 "givenname",  
                 "mail",  
                 "telephonenumber",
                 "employeeID",
                 "employeeNumber" };  
         
         //constraints.setReturningAttributes(attrIDs);  
         constraints.setReturningAttributes(new String[]{"mail", "facsimileTelephoneNumber", "company","userPassword", "distinguishedName"});
         NamingEnumeration<SearchResult> answer = ctx.search("DC=gadventures,DC=internal","sAMAccountName="+ user, constraints);  
         if (answer.hasMore()) {  
             
        	 Attributes attrs = ((SearchResult) answer.next()).getAttributes();  
             System.out.println(attrs.toString());
             
             String[] dn = attrs.get("distinguishedName").toString().split(":");
             System.out.println(dn.length);
             String distinguishedName = dn[1].substring(1);
             //Sleep for 5 seconds
             Thread.sleep(5000);
             try {
                 System.out.println("updating password...\n");
                
                 	System.out.println();
                 	byte[] quotedPasswordByte;
                 	final String quotedPass = '"' + pass + '"';
                 	quotedPasswordByte = quotedPass.getBytes("UTF-16LE");
                 	
                 	System.out.println("Password Converted to Bytes Utf-16:"+quotedPasswordByte);
                 	ModificationItem[] mods = new ModificationItem[1];
                 	mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("UnicodePwd", quotedPasswordByte));
                 	
                 	//Update happens here
                 	ctx.modifyAttributes(distinguishedName, mods);
                 	
                 	//Call a method to write to text file
                 	WriteSuccessToTextFile(distinguishedName);
                }
                 catch (Exception e) {
                   System.out.println("update password error: " + e);
                   System.exit(-1);
                 }
             
         }
         else{  
        	 WriteFailureToTextFile(user);
        	 throw new Exception("Invalid User"); 
             //Write to another text file
             
         }  
     }
   	 //close the connection
   	 ctx.close();
   	 
	}catch (Exception ex) { 
		WriteFailureToTextFile(user);
        ex.printStackTrace();  
    }  
	
}

 private static void WriteFailureToTextFile(String user) {
	// TODO Auto-generated method stub
	 try {
		 
			String content = user;
			File file = new File("/users/Subodhb/ADUsersNotFound.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\n"+content);
			bw.close();
			System.out.println("Done");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
}


//This method will write to a text file
  private static void WriteSuccessToTextFile(String distinguishedName) {
	// TODO Auto-generated method stub
	try {
		 
		String content = distinguishedName;
		File file = new File("/users/Subodhb/ADUsersMigrated.txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("\n"+content);
		bw.close();
		System.out.println("Done");
		
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
}