/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

package org.cagrid;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential; //caGrid-authentication-service-stubs-1.2.jar
import gov.nih.nci.cagrid.authentication.bean.Credential; //caGrid-authentication-service-stubs-1.2.jar
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient; //caGrid-authentication-service-client-1.2.jar
import gov.nih.nci.cagrid.common.security.ProxyUtil; //caGrid-core-1.2.jar
import gov.nih.nci.cagrid.dorian.client.IFSUserClient; //caGrid-dorian-client-1.2.jar
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime; //caGrid-dorian-stubs-1.2.jar
import gov.nih.nci.cagrid.opensaml.SAMLAssertion; //caGrid-opensaml-1.2.jar

import org.globus.gsi.GlobusCredential; //cog_jglobus.jar

/** 
 *  gridLoginClient
 *  Command line grid login example for client developers. 
 *  
 * @author William Stephens
 * 
 */
public class gridLoginClient {

	/**
	 * Authenticate the provided username and password against the Dorian
	 * grid service.
	 * 
	 * @param url	The url of the Dorian grid service.
	 * @param username	A caGrid username from the caGrid Training Grid.
	 * @param password	The caGrid password.
	 * @return SAMLAssertion A SAML assertion verifying that hte user has authenticated.
	 */
	public static SAMLAssertion authenticate (String url, String username, String password) 
	{
		Credential credential = new Credential ();
		BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
		bac.setUserId(username);
		bac.setPassword(password);
		credential.setBasicAuthenticationCredential(bac);
		
		SAMLAssertion saml = null;
		try {
			System.out.println("authenticating against : " + url);
			AuthenticationClient client = new AuthenticationClient(url, credential);
			saml = client.authenticate();
		}
		catch (Throwable e){
			// TODO: Display appropriate client error 
			System.out.println("Authentication Exception " + e.getMessage());
			e.printStackTrace();
		}

		return saml; 
	}

	/**
	 * Authorize the user against the Dorian grid service
	 * 
	 * @param url	The url of the Dorian grid service.
	 * @param samlAssertion A SAML assertion verifying that the user has authenticated.
	 * @return GlobusCredential The grid credential that can be used to invoke grid services.
	 */
	public static GlobusCredential authorize (String url, SAMLAssertion saml) 
	{
		GlobusCredential cred = null;
		
		System.out.println("authorizing against : " + url);
		
		// Create a IFS Client for authorization
		IFSUserClient c2 = null;
		try {
			c2 = new IFSUserClient(url);
		}
		catch (Exception e) {
			// TODO: Display appropriate client error 
			System.out.println("IFSUserClient " + e.getMessage());
			e.printStackTrace();
		}
		
		// Create a lifetime for the proxy cert, 12 hours in this case
		ProxyLifetime lifetime = new ProxyLifetime();
		lifetime.setHours(12);
		lifetime.setMinutes(0);
		lifetime.setSeconds(0);
		
		// specify delegation, use 0 for now
		int delegation = 0;
		try {
			delegation = Integer.valueOf(1);
		} catch (Exception e) {
			// TODO: Display appropriate client error 
			System.out.println("Delegation " + e.getMessage());
			e.printStackTrace();
		}
		
		try {
		    cred = c2.createProxy(saml, lifetime, delegation);
		}
		catch (Throwable e) {
			// TODO: Display appropriate client error 
			System.out.println("Authorization Exception " + e.getMessage());
			e.printStackTrace();
		} 
		
		return cred;
	}
	
	
	/**
	 * Logs a caGrid user onto the Training Grid via authenticate and authorize
	 * steps against the Dorian grid service.
	 * 
	 * @param username	A caGrid username from the caGrid Training Grid
	 * @param password	The caGrid password
	 */
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("usage: <username> <password>");
			System.exit(-1);
		}

		//String url = "https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian";
		String url = "https://cagrid-dorian.nci.nih.gov:8443/wsrf/services/cagrid/Dorian";

		SAMLAssertion saml = authenticate (url, args[0], args[1]);
		
		if (saml == null ) {
			// TODO: Display appropriate client error 
			System.out.println("Failed to retrieve SAML assertion for : " + args[0]);
			System.exit(-1);
		}
		
		GlobusCredential cred = authorize (url, saml);
		
		try {
			ProxyUtil.saveProxyAsDefault(cred);
		}
		catch (Exception e) {
			// TODO: Display appropriate client error 
			System.out.println("Failed to save proxy in ProxyUtil: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("logged in. saved proxy as default");

	}

}
