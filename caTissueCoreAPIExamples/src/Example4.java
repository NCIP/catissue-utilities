/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Example4
{
	private static FileWriter resFile = null;
		
    public static void main(String[] args) 
	{

		System.out.println("*** Example 4...");
		try
		{

			Example4 testClient = new Example4();
					
			try 
			{
				testClient.testInstance();				
			}
			catch (RuntimeException e2) 
			{
				e2.printStackTrace();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Test client throws Exception = "+ ex);
		}
	}
	
    public void testInstance() 
	{
	
			ApplicationService appService = ApplicationServiceProvider.getRemoteInstance();
			
			ClientSession cs = ClientSession.getInstance();
			try
			{ 
				cs.startSession("admin@admin.com", "Login123");
			} 
			catch (Exception ex) 
			{ 
				System.out.println(ex.getMessage()); 
				ex.printStackTrace();
			}

			try 
			{
				testSearchParticipant(appService);
				cs.terminateSession();
			}
			catch (RuntimeException e2) 
			{
				e2.printStackTrace();
			}
	}

    
    private void testSearchParticipant(ApplicationService appService )
    {
    	Participant participant = new Participant();
    	participant.setLastName("A*");
		try {
			List resultList = appService.search(Participant.class,participant);
			System.out.println("No of particpants found: " + resultList.size());
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) {
				Participant returnedParticipant = (Participant) resultsIterator.next();
				System.out.print("Lastname: "+returnedParticipant.getLastName());
				System.out.println(" Firstname: " + returnedParticipant.getFirstName());
				System.out.println("Gender: " + returnedParticipant.getGender());
			}
		} 
		catch (Exception e) {
          	System.out.println(e.getMessage());
	 		e.printStackTrace();
		}
    }
    
}



