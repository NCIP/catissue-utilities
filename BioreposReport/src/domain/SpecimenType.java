/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

package domain;

//import domain.*;
//import gov.nih.nci.system.applicationservice.*;
//import gov.nih.nci.common.util.HQLCriteria;
import java.util.*;
//import org.apache.log4j.Logger;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
 
  /**

   */

public class SpecimenType 	implements java.io.Serializable 
{
	private static final long serialVersionUID = 1234567890L;
	
	
	public SpecimenType() {
		
		
	}
	
	public java.lang.String name;
	public java.lang.String getName()
	{
		return name;
	}
	public void setName(java.lang.String name)
	{
		this.name = name;
	}
	
		
	public java.lang.Long id;
	public java.lang.Long getId()
	{
		return id;
	}
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
	
	
		
		
	private java.util.Collection periodShipment = new java.util.HashSet();
	public java.util.Collection getPeriodShipment()
	{
		if (periodShipment==null || periodShipment.getClass().getName().indexOf("PersistentSet")>0)		
		{
	      try 
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.NumberShipped as child, domain.SpecimenType as parent  where child in elements(parent.periodShipment) and parent.id="+idString;
				//HQLCriteria hqlCriteria = new HQLCriteria(hql);
				//ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				//java.util.List resultList = applicationService.query(hqlCriteria,"domain.NumberShipped");				 
				//periodShipment = resultList;	 
			}
			catch(Exception ex) 
			{
				//Logger log = Logger.getLogger(SpecimenType.class.getName());
				//log.error("SpecimenType:getPeriodShipment throws exception ... ...",ex);
			}
		}	
		return periodShipment;
	}
	
	public void setPeriodShipment(java.util.Collection periodShipment)
	{
		this.periodShipment = periodShipment;
	}	
		
	
	
	
		
		
	private java.util.Collection weHave = new java.util.HashSet();
	public java.util.Collection getWeHave()
	{
		if (weHave==null || weHave.getClass().getName().indexOf("PersistentSet")>0)		
		{
	      try 
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.SpecimenInventory as child, domain.SpecimenType as parent  where child in elements(parent.weHave) and parent.id="+idString;
				//HQLCriteria hqlCriteria = new HQLCriteria(hql);
				//ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				//java.util.List resultList = applicationService.query(hqlCriteria,"domain.SpecimenInventory");				 
				//weHave = resultList;	 
			}
			catch(Exception ex) 
			{
				//Logger log = Logger.getLogger(SpecimenType.class.getName());
				//log.error("SpecimenType:getWeHave throws exception ... ...",ex);
			}
		}	
		return weHave;
	}
	
	public void setWeHave(java.util.Collection weHave)
	{
		this.weHave = weHave;
	}	
		
	

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if(obj instanceof SpecimenType) 
		{
			SpecimenType c =(SpecimenType)obj; 			 
			Long thisId = getId();		
			
			if(thisId != null && thisId.equals(c.getId()))
				eq = true;
			
			}
			return eq;
		}
		
	public int hashCode()
	{
		int h = 0;
		
		if(getId() != null)
			h += getId().hashCode();
		
		return h;
	}
}