/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

package caTissueUtils;

import java.util.Map;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;
import keychain.CaTissueInstance;
import keychain.KeyChainLoader;

public class caTissueSession {
	
	public static ApplicationService logon(String instanceName, String keyChainName) {


		Map<String,CaTissueInstance> instances = KeyChainLoader.loadKeyChain(keyChainName);

		CaTissueInstance instance = instances.get(instanceName);

		String serviceURL = instance.getHost() + "/catissuecore/http/remoteService";
		System.out.println("Accessing: " + instance.getName());
		System.out.println("at: " + serviceURL);

		ApplicationService appService = ApplicationServiceProvider.getRemoteInstance(serviceURL);

		ClientSession cs = ClientSession.getInstance();
		try
		{ 
			cs.startSession(instance.getUsername(), instance.getPassword());
		} 
		catch (Exception ex) 
		{ 
			System.out.println(ex.getMessage()); 
			ex.printStackTrace();
		}
		return appService;
	}

}
