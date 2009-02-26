package gridexamples;


import gov.nih.nci.cagrid.discovery.client.*; 
import gov.nih.nci.cagrid.metadata.exceptions.QueryInvalidException;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.metadata.*; 
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery; 
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient; 
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ListServices {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		StoreResults db = new StoreResults();
		ListServices l = new ListServices();
		String grid = args[2];
		
		l.SetProxy(args[0], args[1], grid);
		
		for (int s = 3 ; s < args.length; s++) {
			System.out.println("Querying " + args[s] + " services");
			queryServices( grid,  args[s],  db);
		}	
	}
	
	public static void queryServices(String grid, String serviceType, StoreResults db) {
		
		DiscoveryClient client = null;
		EndpointReferenceType[] services = null;
		Set<String> queryClasses = db.getQueryClasses(serviceType);
		//Set<String> queryClasses = GetModelClasses.getClasses(serviceType);
		//excludes.add("H.Lee. Moffitt Cancer Center");
		//excludes.add("The Jackson Laboratory");
		
		//excludes.add("Johns Hopkins Sidney Kimmel Cancer Center");
		//excludes.add("Fox Chase Cancer Center");

		HashSet<String> excludes = new HashSet<String>();

		try {
			// Production 
			client = new DiscoveryClient();
			// Training grid
			if (grid.equals("training-1.2"))
				client = new DiscoveryClient("http://index.training.cagrid.org:8080/wsrf/services/DefaultIndexService");
			
			//services = client.getAllDataServices();
			services = client.discoverServicesByName(serviceType);
			//services = client.discoverServicesByResearchCenter("Washington University in St.Louis");
			//services = client.discoverServicesByResearchCenter("Washington University");
			//services = client.discoverServicesByResearchCenter("Masonic Cancer Center, University of Minnesota");
			//services = client.discoverServicesByResearchCenter("Yale Cancer Center");
			
			//UMLClass umlClass = new UMLClass();
			//umlClass.setClassName("Specimen");
			//services = client.discoverServicesByOperationInput(umlClass);
			
			System.out.println("___________________________");

						
			if (services != null ) {
				System.out.println(services.length + " services");
				for (int i=0; i<services.length; i++) {
					String address = services[i].getAddress().getHost();
					System.out.println(services[i].getServiceName());
					//services[i].
					ServiceMetadata md = MetadataUtils.getServiceMetadata(services[i]);
					String serviceName = md.getHostingResearchCenter().getResearchCenter().getDisplayName();
					
					
					System.out.println(serviceName);
					
					//Address a = md.getHostingResearchCenter().getResearchCenter().getAddress();
					//System.out.println(a.getStreet1()+","+a.getStreet2()+","+a.getLocality()+","+
					//		a.getStateProvince()+","+a.getPostalCode());
					
				
					//md.getServiceDescription().
					//DomainModel model = MetadataUtils.getDomainModel(services[i]);
					//String modelShortName = model.getProjectShortName();
					//System.out.println(modelShortName);
					//System.out.println(a.getStreet1());
					
					Long probeId = db.addProbe(serviceType, serviceName, address, grid, "in index");

					//if (serviceName.length() < 1 || excludes.contains(serviceName)) {
					if (excludes.contains(serviceName)) {
						System.out.println("excluded");
					}
					else {
						DataServiceClient dataClient = new DataServiceClient(services[i]); 

						Iterator<String> queryIt = queryClasses.iterator();
						boolean isFast = true;
						
						//while (isFast && queryIt.hasNext()) {
						while (queryIt.hasNext()) {
							String queryClass = queryIt.next();
							CQLQuery query = new CQLQuery();
							// build up the query 
							gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object(); 
							target.setName(queryClass); 

							query.setTarget(target);
							QueryModifier qm = new QueryModifier();
							qm.setCountOnly(true);
							query.setQueryModifier(qm);
							System.out.println("Querying " + queryClass);
							CQLQueryResults results;
							Long count = new Long(-99);
							long start = System.currentTimeMillis();
							long end = 0;
							Double duration = null;
							String exception = "none";
							
							try {
								results = dataClient.query(query);
								duration = new Double( (System.currentTimeMillis() - start)/ 1000.00);
								Iterator iter = new CQLQueryResultsIterator(results);
								while (iter.hasNext()) { 
									java.lang.Object result = iter.next(); 
									if(result.getClass().getCanonicalName().equals("java.lang.Long")) {
										count = (Long)result;
										System.out.println("Found\t" + count.toString());
									}
								}
							} catch (org.apache.axis.AxisFault e) {
								// TODO Auto-generated catch block
								System.err.println("Axis error class: " +e.getClass().getName());
								//exception = e.getClass().getName();
								//exception = e.getMessage();
								
								StringWriter w = new StringWriter();
								PrintWriter pw = new PrintWriter(w);
								e.printStackTrace(pw);
								pw.flush();
								w.flush();
								exception = w.toString();
								//Throwable cause = e.getCause();
								//System.err.println("Cause class: " + cause.getClass().getName());
								//e.printStackTrace();
								//System.exit(-1);
							}
							catch (Exception e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
								exception = e.getClass().getName();
							}
							finally {
								if (duration == null)
									duration = new Double( (System.currentTimeMillis() - start)/ 1000.00);
							}
							System.out.println("Query time: " + duration + "s");
							db.addGridQuery(probeId, queryClass, count, duration, exception);
							if (duration > 599)
								isFast = false;
						}
					}
					System.out.println("________");						
				}
				System.out.println("___________________________");
				System.out.println(services.length + " services");
			}
		}
		catch (MalformedURIException e) {
			System.err.println("Invalid URI specified for index service: " + e);
			e.printStackTrace();
			System.exit(-1);
		} catch (RemoteResourcePropertyRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourcePropertyRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  void SetProxy(String username, String password, String grid) {
		
/*		SyncDescription description = new SyncDescription();
		try {
			SyncGTS.getInstance().syncOnce(description);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		
		// Default is production 1.2 at NCI
		String url = "https://cagrid-dorian.nci.nih.gov:8443/wsrf/services/cagrid/Dorian";
		// Training grid
		if (grid.equals("training-1.2"))
				url = "https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian";

		gridLoginClient client = new gridLoginClient();
		SAMLAssertion saml = client.authenticate (url, username, password);
		
		if (saml == null ) {
			// TODO: Display appropriate client error 
			System.out.println("Failed to retrieve SAML assertion for : " + username);
			System.exit(-1);
		}
		
		GlobusCredential cred = client.authorize (url, saml);
		
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
