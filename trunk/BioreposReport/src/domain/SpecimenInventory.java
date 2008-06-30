package domain;

import domain.*;
/*import gov.nih.nci.system.applicationservice.*;
import gov.nih.nci.common.util.HQLCriteria;
*/import java.util.*;
import org.apache.log4j.Logger;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
 
  /**

   */

public  class SpecimenInventory 	implements java.io.Serializable 
{
	private static final long serialVersionUID = 1234567890L;
	
	
		
	public java.lang.Long numberofspecimens;
	public java.lang.Long getNumberofspecimens()
	{
		return numberofspecimens;
	}
	public void setNumberofspecimens(java.lang.Long numberofspecimens)
	{
		this.numberofspecimens = numberofspecimens;
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
	
	
		
		
	private domain.SpecimenType numberOf;
	public domain.SpecimenType getNumberOf()
	{
			
		if(numberOf==null ||  numberOf.getClass().getName().indexOf('$')>0)
		{			
			try
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.SpecimenType as child where child.id in (select parent.numberOf.id from domain.SpecimenInventory as parent where parent.id="+idString+")";
/*				HQLCriteria hqlCriteria = new HQLCriteria(hql);
				ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				java.util.List resultList = applicationService.query(hqlCriteria,"domain.SpecimenType");				 
				if (resultList!=null && resultList.size()>0) 
					numberOf = (domain.SpecimenType)resultList.get(0);
				else
					numberOf = null;
*/			}
			catch(Exception ex) 
			{ 
				Logger log = Logger.getLogger(SpecimenInventory.class.getName());
				log.error("SpecimenInventory:getNumberOf throws exception ... ...",ex);
			}
		}
		return numberOf;	
					
	}

	public void setNumberOf(domain.SpecimenType numberOf)
	{
		this.numberOf = numberOf;
	}
		
	
	
	
		
		
	private domain.CollectionProtocol collectedFor;
	public domain.CollectionProtocol getCollectedFor()
	{
			
		if(collectedFor==null ||  collectedFor.getClass().getName().indexOf('$')>0)
		{			
			try
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.CollectionProtocol as child where child.id in (select parent.collected for.id from domain.SpecimenInventory as parent where parent.id="+idString+")";
/*				HQLCriteria hqlCriteria = new HQLCriteria(hql);
				ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				java.util.List resultList = applicationService.query(hqlCriteria,"domain.CollectionProtocol");				 
				if (resultList!=null && resultList.size()>0) 
					collectedFor = (domain.CollectionProtocol)resultList.get(0);
				else
					collectedFor = null;
*/			}
			catch(Exception ex) 
			{ 
				Logger log = Logger.getLogger(SpecimenInventory.class.getName());
				log.error("SpecimenInventory:getCollected for throws exception ... ...",ex);
			}
		}
		return collectedFor;	
					
	}

	public void setCollectedFor(domain.CollectionProtocol collectedFor)
	{
		this.collectedFor = collectedFor;
	}
		
	

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if(obj instanceof SpecimenInventory) 
		{
			SpecimenInventory c =(SpecimenInventory)obj; 			 
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