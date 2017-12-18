import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class PErsdonal {

	public static void main(String[] args) {
		//Hashtable env = new Hashtable();
		//env.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		//env.put(DirContext.PROVIDER_URL, "ldap://192.168.100.170:389/dc=cbrs,dc=local");
		        Hashtable env = new Hashtable();
				env.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				env.put(DirContext.PROVIDER_URL, "ldap://192.168.100.170:389/dc=cbrs,dc=local");
				env.put(DirContext.SECURITY_AUTHENTICATION, "simple");
				env.put(DirContext.SECURITY_PRINCIPAL, "cn=Enrique Villar, cn=Users,dc=cbrs,dc=local");
				env.put(DirContext.SECURITY_CREDENTIALS, "0458");

		DirContext dirContext = null;
		try {
			dirContext = new InitialDirContext(env);
		System.out.println(dirContext.toString());
		} catch (NamingException ne) {
			ne.printStackTrace();
		}

	}

}
