/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

import edu.wustl.catissuecore.domain.*;
//import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class Example5
{
        
    public static void main(String[] args)
    {

        System.out.println("*** Example 5...");
        try
        {

            Example5 testClient = new Example5();
                   
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
    
            ApplicationService appService =
ApplicationServiceProvider.getRemoteInstance();
            
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
                testSearchSpecimen(appService);
                cs.terminateSession();
            }
            catch (RuntimeException e2)
            {
                e2.printStackTrace();
            }
    }

    
    private void testSearchSpecimen(ApplicationService appService )
    {
          
                           DetachedCriteria criteria = DetachedCriteria
                        .forClass(Specimen.class);
                          criteria.add(Restrictions.gt("quantity", new
Quantity("50")));
                   
        try {
            List resultList = appService.query(criteria, Specimen.class
                    .getName());
            
            System.out.println("No of specimens found: " +
resultList.size());
            for (Iterator resultsIterator = resultList.iterator();
resultsIterator.hasNext();) {
                Specimen returnedspecimen = (Specimen)
resultsIterator.next();
                System.out.println("Label: "+returnedspecimen.getLabel() + "Type: " + returnedspecimen.getType() + " ");
                System.out.println("\tQuantity: " +
returnedspecimen.getQuantity().getValue() + " " );
            }
        } 
        catch (Exception e) {
              System.out.println(e.getMessage());
             e.printStackTrace();
        }
    }
}
