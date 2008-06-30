package domain;

import domain.*;
//import gov.nih.nci.system.applicationservice.*;
//import gov.nih.nci.common.util.HQLCriteria;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
 
  /**

   */

public  class CollectionProtocol 	implements java.io.Serializable 
{
	private static final long serialVersionUID = 1234567890L;
	
	
		
	public java.lang.String name;
	public java.lang.String getName()
	{
		return name;
	}
	public void setName(java.lang.String name)
	{
		this.name = name;
	}
	
		
	public java.lang.String Identifier;
	public java.lang.String getIdentifier()
	{
		return Identifier;
	}
	public void setIdentifier(java.lang.String Identifier)
	{
		this.Identifier = Identifier;
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
				String hql = "select child from domain.NumberShipped as child, domain.CollectionProtocol as parent  where child in elements(parent.periodShipment) and parent.id="+idString;
				//HQLCriteria hqlCriteria = new HQLCriteria(hql);
				//ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				//java.util.List resultList = applicationService.query(hqlCriteria,"domain.NumberShipped");				 
				//periodShipment = resultList;	 
			}
			catch(Exception ex) 
			{
				Logger log = Logger.getLogger(CollectionProtocol.class.getName());
				log.error("CollectionProtocol:getPeriodShipment throws exception ... ...",ex);
			}
		}	
		return periodShipment;
	}
	
	public void setPeriodShipment(java.util.Collection periodShipment)
	{
		this.periodShipment = periodShipment;
	}	
		
	
	
	
		
		
	private java.util.Collection available = new java.util.HashSet();
	public java.util.Collection getAvailable()
	{
		if (available==null || available.getClass().getName().indexOf("PersistentSet")>0)		
		{
	      try 
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.SpecimenInventory as child, domain.CollectionProtocol as parent  where child in elements(parent.available) and parent.id="+idString;
				//HQLCriteria hqlCriteria = new HQLCriteria(hql);
				//ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				//java.util.List resultList = applicationService.query(hqlCriteria,"domain.SpecimenInventory");				 
				//available = resultList;	 
			}
			catch(Exception ex) 
			{
				Logger log = Logger.getLogger(CollectionProtocol.class.getName());
				log.error("CollectionProtocol:getAvailable throws exception ... ...",ex);
			}
		}	
		return available;
	}
	
	public void setAvailable(java.util.Collection available)
	{
		this.available = available;
	}	
		
	

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if(obj instanceof CollectionProtocol) 
		{
			CollectionProtocol c =(CollectionProtocol)obj; 			 
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