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

public  class Reportingperiod 	implements java.io.Serializable 
{
	private static final long serialVersionUID = 1234567890L;
	
	
		
	public java.util.Date start;
	public java.util.Date getStart()
	{
		return start;
	}
	public void setStart(java.util.Date start)
	{
		this.start = start;
	}
	
		
	public java.util.Date end;
	public java.util.Date getEnd()
	{
		return end;
	}
	public void setEnd(java.util.Date end)
	{
		this.end = end;
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
	
	
		
		
	private java.util.Collection numberShipped = new java.util.HashSet();
	public java.util.Collection getNumberShipped()
	{
		if (numberShipped==null || numberShipped.getClass().getName().indexOf("PersistentSet")>0)		
		{
	      try 
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.NumberShipped as child, domain.Reportingperiod as parent  where child in elements(parent.numberShipped) and parent.id="+idString;
/*				HQLCriteria hqlCriteria = new HQLCriteria(hql);
				ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				java.util.List resultList = applicationService.query(hqlCriteria,"domain.NumberShipped");				 
				numberShipped = resultList;	 
*/			}
			catch(Exception ex) 
			{
				Logger log = Logger.getLogger(Reportingperiod.class.getName());
				log.error("Reportingperiod:getNumberShipped throws exception ... ...",ex);
			}
		}	
		return numberShipped;
	}
	
	public void setNumberShipped(java.util.Collection numberShipped)
	{
		this.numberShipped = numberShipped;
	}	
		
	

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if(obj instanceof Reportingperiod) 
		{
			Reportingperiod c =(Reportingperiod)obj; 			 
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