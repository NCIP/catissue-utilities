/*L
   Copyright Washington University in St. Louis
   Copyright SemanticBits
   Copyright Persistent Systems
   Copyright Krishagni

   Distributed under the OSI-approved BSD 3-Clause License.
   See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
L*/

insert into reposReportCT.specimen_type
(id, name)
SELECT identifier ,
 value FROM caTissueCore11.CATISSUE_PERMISSIBLE_VALUE 
where parent_identifier in (1,2,3,4);


insert into reposReportCT.collection_protocol
(       id,
        name,
        identifier,
	   patients_planned,
        patients_enrolled
)
select p.identifier, p.title, p.irb_identifier, enrollment, count(*)
from caTissueCore11.catissue_specimen_protocol p,
caTissueCore11.catissue_coll_prot_reg r
where p.identifier = r.collection_protocol_id
group by p.identifier;


insert into reposReportCT.collection_protocol
(       id,
        name,
        identifier
)
select p.identifier, p.title, p.irb_identifier
from caTissueCore11.catissue_specimen_protocol p,
caTissueCore11.catissue_distribution_protocol d
where p.identifier = d.identifier;

insert into reposReportCT.specimen_inventory
(
        specimen_type_id,
        collection_protocol_id,
        specimen_count
)
SELECT p.identifier
  	, cpr.collection_protocol_id
  	, count(*)
  FROM caTissueCore11.catissue_specimen c, 
  	caTissueCore11.catissue_specimen_coll_group cc, 
  	caTissueCore11.catissue_coll_prot_reg cpr,
caTissueCore11.catissue_permissible_value p
  WHERE 
cc.IDENTIFIER=c.specimen_collection_group_id 
    AND cpr.IDENTIFIER=cc.collection_protocol_reg_id
and p.value = c.type
  group by type, collection_protocol_id;
  
  
insert into reposreportct.reporting_period
(id, start, end)
values
(1,'','');

insert into reposreportct.number_shipped
(report_period_id,
collection_protocol_id,
specimen_type_id,
number_shipped)
SELECT 
concat(date_format(event_timestamp, "%Y"), ceiling(date_format(event_timestamp, "%c")/3)) report_period_id,
dp.identifier protocol_id,
st.id specimen_type_id,
count(*) number_shipped
FROM catissuecore11.CATISSUE_DISTRIBUTION d,
catissuecore11.catissue_specimen_protocol dp,
catissuecore11.CATISSUE_DISTRIBUTED_ITEM di,
catissuecore11.catissue_specimen s ,
reposReportCT.specimen_type st
WHERE d.distribution_protocol_id = dp.identifier
and di.distribution_id = d.identifier
and s.identifier = di.specimen_id
and s.type = st.name
group by event_timestamp, dp.title, s.type;