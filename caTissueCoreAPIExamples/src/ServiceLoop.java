import edu.wustl.catissuecore.domain.*;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import keychain.CaTissueInstance;
import keychain.KeyChainLoader;

public class ServiceLoop 
{
	private static FileWriter resFile = null;
	private static Map<String,CaTissueInstance> instances = null;

		
    public static void main(String[] args) 
	{
	
		String instanceName = args[0];
		String resFileName = args[1];
		String keyChainFileName = args[2];
		
		instances = KeyChainLoader.loadKeyChain(keyChainFileName);

		try {
			resFile = new FileWriter(resFileName, true);
			resFile.write("<tr><td>" + instanceName + "</td>");
			resFile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		List<CaTissueInstance> qInstances = new ArrayList<CaTissueInstance>();

		
		qInstances.add (instances.get(instanceName));
		
		System.out.println("*** ServiceLoop...");
		try
		{

				ServiceLoop testClient = new ServiceLoop();
				Iterator it = qInstances.iterator();
				/* Note that the following loop is known not to work when you 
				 * try to run a query against more than one instance of caTissue during a single execution
				 * of this class. The workaround is to call this program multiple times from an ant
				 * build script. This problem arises from a limitation of the caCORE SDK. 
				 * Perhaps the loop should be removed from the code - but for now it's been left in because that 
				 * was the original goal of this program. The problem is purported to have been fixed in  
				 * caCORE SDK v 4.0 but until the caTissue API is using that version this problem will continue. */
				while (it.hasNext())
				{
					try 
					{
						CaTissueInstance instance = (CaTissueInstance) it.next();
						testClient.testInstance(instance);				
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
	
    public void testInstance(CaTissueInstance instance) 
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
	// TODO Modify the output so that the query is reported
    // This wasn't done yet becuase the result file header is produced outside this program.
    // Once the queries can be run against multiple caTissue instances from within a single execution
    // this problem will be more tractable. Am resistant to putting in a solution which magnifies the kludge
    // we are currently working with! But probably need to put aside such high minded principles! Ho Hum ;-)

    
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

           	// TODO Pass the query in via some appropriate data structure so it is not embedded in the code
        	// This begs the (potentially big) question what such a data structure might be 
        	// and what standards it might use. Suggest we workaround that big question for now 
        	// and do something expedient. The big question should really be handled by the Architecture workspace
        	// and the caGrid team.
        	
        	Specimen specimen = new Specimen();
        	
          	//specimen.setPathologicalStatus("Malignant");
          	
         	//specimen.setType("*Block");
         	//specimen.setType("Fixed Tissue Block");
         	
         	//specimen.setType("Serum");
          	//specimen.setLabel("BTB-711*");
         	
         	Map<String, String> clauses = new HashMap<String, String>();
          	clauses.put("Type", "*Block");
        	
         	//clauses.put("Type", "Fixed Tissue Block");       	
         	//clauses.put("Type", "Serum");     	
         	//clauses.put("PathologicalStatus", "Malignant");
        	specimen = this.getSearchSpecimen(clauses);
         	
      	
                try {
        	 List resultList = appService.search(Specimen.class,specimen);
        	 
        	 // Haven't got the following line to work yet
        	 // According to the caCORE SDK documentation it should work
        	 // It's benefit would be that we could get query counts back without having to 
        	 // return all the matching objects and then count them!
        	 //int c = appService.getQueryRowCount(specimen, /Specimen.class.getName());
        	 
           	 System.out.println("No of specimens found: " + resultList.size());
           	 //System.out.println("No of specimens found: " + c);
           	    			try {
           	    				resFile.write("<td>" + resultList.size() + "</td></tr>");
           	    				//resFile.write("<td>" + c + "</td></tr>");
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



