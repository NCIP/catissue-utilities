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

public  class NumberShipped 	implements java.io.Serializable 
{
	private static final long serialVersionUID = 1234567890L;
	
	
		
	public java.lang.Long numbershipped;
	public java.lang.Long getNumbershipped()
	{
		return numbershipped;
	}
	public void setNumbershipped(java.lang.Long numbershipped)
	{
		this.numbershipped = numbershipped;
		
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
	
	
		
		
	private domain.SpecimenType specimenType;
	public domain.SpecimenType getSpecimenType()
	{
			
		if(specimenType==null ||  specimenType.getClass().getName().indexOf('$')>0)
		{			
			try
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.SpecimenType as child where child.id in (select parent.specimenType.id from domain.NumberShipped as parent where parent.id="+idString+")";
/*				HQLCriteria hqlCriteria = new HQLCriteria(hql);
				ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				java.util.List resultList = applicationService.query(hqlCriteria,"domain.SpecimenType");				 
				if (resultList!=null && resultList.size()>0) 
					specimenType = (domain.SpecimenType)resultList.get(0);
				else
					specimenType = null;
*/			}
			catch(Exception ex) 
			{ 
				Logger log = Logger.getLogger(NumberShipped.class.getName());
				log.error("NumberShipped:getSpecimenType throws exception ... ...",ex);
			}
		}
		return specimenType;	
					
	}

	public void setSpecimenType(domain.SpecimenType specimenType)
	{
		this.specimenType = specimenType;
	}
		
	
	
	
		
		
	private domain.CollectionProtocol shippedFor;
	public domain.CollectionProtocol getShippedFor()
	{
			
		if(shippedFor==null ||  shippedFor.getClass().getName().indexOf('$')>0)
		{			
			try
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.CollectionProtocol as child where child.id in (select parent.shippedFor.id from domain.NumberShipped as parent where parent.id="+idString+")";
/*				HQLCriteria hqlCriteria = new HQLCriteria(hql);
				ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				java.util.List resultList = applicationService.query(hqlCriteria,"domain.CollectionProtocol");				 
				if (resultList!=null && resultList.size()>0) 
					shippedFor = (domain.CollectionProtocol)resultList.get(0);
				else
					shippedFor = null;
*/			}
			catch(Exception ex) 
			{ 
				Logger log = Logger.getLogger(NumberShipped.class.getName());
				log.error("NumberShipped:getShippedFor throws exception ... ...",ex);
			}
		}
		return shippedFor;	
					
	}

	public void setShippedFor(domain.CollectionProtocol shippedFor)
	{
		this.shippedFor = shippedFor;
	}
		
	
	
	
		
		
	private domain.Reportingperiod during;
	public domain.Reportingperiod getDuring()
	{
			
		if(during==null ||  during.getClass().getName().indexOf('$')>0)
		{			
			try
			{
				String idString = (Class.forName("java.lang.String").isInstance(getId()))? "'"+ getId() + "'" : ""+getId(); 
				String hql = "select child from domain.Reportingperiod as child where child.id in (select parent.during.id from domain.NumberShipped as parent where parent.id="+idString+")";
/*				HQLCriteria hqlCriteria = new HQLCriteria(hql);
				ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
				java.util.List resultList = applicationService.query(hqlCriteria,"domain.Reportingperiod");				 
				if (resultList!=null && resultList.size()>0) 
					during = (domain.Reportingperiod)resultList.get(0);
				else
					during = null;
*/			}
			catch(Exception ex) 
			{ 
				Logger log = Logger.getLogger(NumberShipped.class.getName());
				log.error("NumberShipped:getDuring throws exception ... ...",ex);
			}
		}
		return during;	
					
	}

	public void setDuring(domain.Reportingperiod during)
	{
		this.during = during;
	}
		
	

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if(obj instanceof NumberShipped) 
		{
			NumberShipped c =(NumberShipped)obj; 			 
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