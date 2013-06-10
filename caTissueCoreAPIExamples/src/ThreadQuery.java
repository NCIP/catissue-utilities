/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import keychain.CaTissueInstance;
import keychain.KeyChainLoader;


public class ThreadQuery extends Thread {

	//private ApplicationService appService = null;
	private static FileWriter resFile = null;
	private CaTissueInstance instance;
	private static Map<String, CaTissueInstance> instances = null;
		
 
	public ThreadQuery(CaTissueInstance instance) {
		
		this.instance = instance;
		
	}

public static void main(String[] args) 
	{
	
		

/*		try {
			resFile = new FileWriter(resFileName, true);
			resFile.write("<tr><td>" + instanceName + "</td>");
			resFile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
	
	String keyChainFileName = args[2];
	
	instances = KeyChainLoader.loadKeyChain(keyChainFileName);

	
		List<CaTissueInstance> qInstances = new ArrayList<CaTissueInstance>();

		qInstances.add (instances.get("Northwestern"));
		qInstances.add (instances.get("Baylor"));
		
		System.out.println("*** ServiceLoop...");
		try
		{

			Iterator<CaTissueInstance> it = qInstances.iterator();
			while (it.hasNext())
			{

				try 
				{
					ThreadQuery testClient = new ThreadQuery(it.next());
					testClient.start();				
				}
				catch (RuntimeException e2) 
				{
					e2.printStackTrace();
				}

			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Test client throws Exception = "+ ex);
		}
	}
	
    public void run() 
	{
	
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

			try 
			{
				testSearchSpecimen(appService);
				//testSearchCollectionProtocol(appService);
				//cs.terminateSession();
			}
			catch (RuntimeException e2) 
			{
				e2.printStackTrace();
			}
	}

    
    /*
     * This method will allow a query to be expressed in a data structure so that the output page
     * can be updated with the actual query run
     */
    
    private Specimen getSearchSpecimen(Map<String, String> clauses )
    {
    	Specimen specimen = new Specimen();
 
    	
    	Set<String> names = clauses.keySet();
    	Iterator<String> si = names.iterator();
    	while (si.hasNext()) {
      	
    		String field = si.next();
    		String methodName = "set" + field;
 
    	   	try {
				Method met = specimen.getClass().getMethod(methodName,
						new Class[] {
				        Class.forName("java.lang.String")});
				
				met.invoke(specimen, new Object[] {
						clauses.get(field)});
	      	
    	   	} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

     	}
    	
    	return specimen;
     	
    }
    
        private void testSearchSpecimen(ApplicationService appService )
        {
        	Specimen specimen = new Specimen();
          	//specimen.setPathologicalStatus("Malignant");
          	
         	specimen.setType("*Block");
         	//specimen.setType("Fixed Tissue Block");
         	//specimen.setType("Serum");
          	//specimen.setLabel("BTB-711*");
      	
/*      	SpecimenCollectionGroup group = new SpecimenCollectionGroup();
      	group.setClinicalDiagnosis("Prostat*");
      	specimen.setSpecimenCollectionGroup(group);
*/
                try {
        	 List resultList = appService.search(Specimen.class,specimen);
        	 System.out.println("No of specimens found: " + resultList.size());
    			try {
    				resFile.write("<td>" + resultList.size() + "</td></tr>");
    				resFile.flush();
     			} catch (IOException e) {
    				// TODO Auto-generated catch block
     				System.err.println(e.getMessage());
    				e.printStackTrace();
    			}
       	 for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) {
        		 Specimen returnedspecimen = (Specimen) resultsIterator.next();
        		 System.out.print("\t"+returnedspecimen.getLabel() + " " + returnedspecimen.getType() + " ");
        		 System.out.print(returnedspecimen.getQuantity().getValue() + " " );
        		 System.out.println(returnedspecimen.getPathologicalStatus() );

 
        	 }
          } 
          catch (Exception e) {
          	System.out.println(e.getMessage());
	 		e.printStackTrace();
          }
    }
    
    private void testSearchCollectionProtocol(ApplicationService appService)
    {
    	CollectionProtocol collectionProtocol = new CollectionProtocol();
    	collectionProtocol.setTitle("Woodward Colon Trial");
         try {
        	 List resultList = appService.search(CollectionProtocol.class,collectionProtocol);
        	 for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) {
        		 CollectionProtocol returnedcollectionprotocol = (CollectionProtocol) resultsIterator.next();
        		 System.out.println(" Domain Object is successfully Found ---->  :: " + returnedcollectionprotocol.getShortTitle());
             }
          } 
          catch (Exception e) {
//        	  writeFailureOperationsToReport("CollectionProtocol",searchOperation);  
        	Logger.out.error(e.getMessage(),e);
	 		e.printStackTrace();
          }
    }
}
