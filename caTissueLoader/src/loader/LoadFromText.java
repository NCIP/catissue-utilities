/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

/* THIS caBIGª SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 * WARRANTIES (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY, NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE
 * OR ITS AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS caBIGª
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loader;

import edu.wustl.catissuecore.domain.CellSpecimen;
import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.FluidSpecimen;
import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;
/*import edu.wustl.catissuecore.domain.Quantity;
import edu.wustl.catissuecore.domain.QuantityInCount;
import edu.wustl.catissuecore.domain.QuantityInGram;
import edu.wustl.catissuecore.domain.QuantityInMicrogram;
import edu.wustl.catissuecore.domain.QuantityInMilliliter;
*/
import edu.wustl.catissuecore.domain.ReceivedEventParameters;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.TissueSpecimen;
import edu.wustl.catissuecore.domain.User;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import caTissueUtils.caTissueSession;


import keychain.CaTissueInstance;
import keychain.KeyChainLoader;
import net.sf.flatpack.util.ParserUtils;

public class LoadFromText {


	private ApplicationService appService ;

	private ReadColumnMap colMap;
	private static String dataFileName;
	private static String badDataFileName = null;
	private static String mapFileName;
	private static String keyChainName;

	// These are used to cache objects that are likely to be reused for many rows of a data file
	// This saves having to get them from the API for every row
	private Map<String,User> userCache = new HashMap<String,User>();
	private Map<String,CollectionProtocolEvent> cpEventCache = new HashMap<String,CollectionProtocolEvent>();
	private Map<String,CollectionProtocol> cpCache = new HashMap<String,CollectionProtocol>();
	private Map<String,Site> siteCache = new HashMap<String,Site>();
	private Map<String,Specimen> specimenCache = new HashMap<String,Specimen>();
	private Map<String,SpecimenCollectionGroup> SCGCache = new HashMap<String,SpecimenCollectionGroup>();

	private Map<String,Class> specimenClassMap = new HashMap<String,Class>();

	private long participantsLoaded = 0;
	private long medicalIdentifiersLoaded = 0;
	private long collectionGroupsLoaded = 0;
	private long specimensLoaded = 0;


	public LoadFromText() {
		// This map specifies the order of the columns in the source data file
		// plus other parameters like dateformat
		colMap = new ReadColumnMap();
		loadSpecimenClassMap();
		//cMap = colMap.getColumnMap();

		String curDir = System.getProperty("user.dir");
		System.out.println("Working directory " + curDir);
	}

	public static void main(String[] args) 
	{
		// The file names and the protocol to which to register the participants
		// should be read from the command line
		String instanceName = args[0];
		keyChainName = args[1];
		dataFileName = args[2];
		mapFileName = args[3];
		if (args.length > 4)
			badDataFileName = args[4];

		LoadFromText test = new LoadFromText();
		System.out.println("*** " + test.getClass().getName() + " - load caTissue from a text file...");

		test.addObjects(instanceName);
	}

	private void addObjects(String instanceName)
	{
		appService = caTissueSession.logon(instanceName, keyChainName);
		
		colMap.getColumnMapFromFile(mapFileName);
		try
		{
			File df = new File(dataFileName);
			BufferedReader fh =
				new BufferedReader(new FileReader(df));
			if (badDataFileName == null) {
				// Must use forward slash on Windows
				String sep = "/";
				String d[] = dataFileName.split(sep);
				String fileName = d[d.length-1];
				badDataFileName = dataFileName.replaceFirst(fileName, "bad" + fileName);
			}
			System.out.println("Reading data from " + dataFileName);
			System.out.println("Writing bad data to " + badDataFileName);

			BufferedWriter bad = new BufferedWriter(new FileWriter(badDataFileName));

			long rowsRead = 0;
			long rowsLoaded = 0;
			long badRows = 0;

			long timeStarted = System.currentTimeMillis();

			String s;
			if (colMap.getReadHeader())
				s=fh.readLine(); // Header line
			while ((s=fh.readLine())!=null){
				String rowMessages = "";
				List l = ParserUtils.splitLine(s, colMap.getColDelimiter(), colMap.getTextQualifier(), 10);
				rowsRead++;
				colMap.setDataArray(l);
				try {
					if  (colMap.getRowClasses().contains("Participant"))
						addParticipant();
					if  (colMap.getRowClasses().contains("SpecimenCollectionGroup"))
						addGroup();
					if  (colMap.getRowClasses().contains("Specimen"))
						addSpecimen();
				}
				catch (caTissueLoadException e) {
					System.out.println(e.getMessage());
					if (rowMessages.length() > 0)
						rowMessages = rowMessages+";";
					rowMessages = rowMessages +e.getMessage();
				}
				if (rowMessages.equals(""))
					rowsLoaded++;
				else {
					badRows++;
					bad.write(s + String.valueOf(colMap.getColDelimiter() )
							+  String.valueOf(colMap.getTextQualifier() )
							+ rowMessages
							+ String.valueOf( colMap.getTextQualifier())
							+ "\n");
				}
				System.out.println("End of row\n");
			}
			fh.close();
			bad.close();

			long timeFinished = System.currentTimeMillis();
			long timeDiff = timeFinished - timeStarted;

			System.out.println("Done loading");
			System.out.println(rowsRead + " rows read");
			String timeMessage = "";
			float timePerRow = 0;
			if (timeDiff < 1000) {
				timeMessage = timeDiff + " milliseconds";
			} else {
				timeMessage = ((float) (timeDiff / 1000.0)) + " seconds...";
				timePerRow = (float) (timeDiff/rowsRead);
			}
			System.out.println("");
			System.out.println("Loaded in: " + timeMessage);
			System.out.println( timePerRow + " milliseconds per row");
			System.out.println(participantsLoaded + " participants loaded");
			System.out.println(medicalIdentifiersLoaded + " medical record numbers loaded");
			System.out.println(collectionGroupsLoaded + " collection groups loaded");
			System.out.println(specimensLoaded + " specimens loaded");
			System.out.println(badRows + " rows generated problems");
			if (badRows > 0)
				System.out.println("Bad rows written to " + badDataFileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {

		}
	}


	private void addParticipant()
	throws caTissueLoadException
	{

		try {
			Participant p = new edu.wustl.catissuecore.domain.Participant();

			p.setBirthDate(colMap.getDate("birthDate"));
			p.setGender(colMap.getValue("gender"));

			p.setFirstName(colMap.getValue("firstName"));
			p.setLastName(colMap.getValue("lastName"));
			p.setActivityStatus(colMap.getValue("participantActivityStatus"));

			p.setMiddleName(colMap.getValue("middleName"));
			p.setVitalStatus(colMap.getValue("vitalStatus"));
			p.setDeathDate(colMap.getDate("deathDate"));
			p.setSexGenotype(colMap.getValue("sexGenotype"));
			p.setSocialSecurityNumber(colMap.getValue("socialSecurityNumber"));
			p.setEthnicity(colMap.getValue("ethnicity"));

			Collection races = new HashSet();
			for (int r = 1 ; r<=4; r++) {
				String raceKey = "race"+ Integer.toString(r);
				if (colMap.getValue(raceKey) != null)
					races.add(colMap.getValue(raceKey));
			}
			if (colMap.getValue("race") != null)
				races.add(colMap.getValue("race"));
			if (races.size() > 0)
				p.setRaceCollection(races);

			try {
				p =  (Participant) appService.createObject(p);
			} catch (ApplicationException e) {
				caTissueLoadException le = new caTissueLoadException(e,"Participant");
				throw le;
			}
			participantsLoaded++;
			System.out.println("Patient added, lastname: " + p.getLastName());

			if (colMap.getValue("medicalRecordNumber") != null ) {
				ParticipantMedicalIdentifier i = new ParticipantMedicalIdentifier();
				i.setMedicalRecordNumber(colMap.getValue("medicalRecordNumber"));
				i.setParticipant(p);
				i.setSite(findSite(colMap.getValue("siteName")));
				try {
					i =  (ParticipantMedicalIdentifier) appService.createObject(i);
				} catch (ApplicationException e) {
					caTissueLoadException le = new caTissueLoadException(e,"Medical identifier");
					throw le;
				}
				medicalIdentifiersLoaded++;
				System.out.println("MRN added: " + i.getMedicalRecordNumber());
			}

			CollectionProtocolRegistration cpr = new CollectionProtocolRegistration();
			CollectionProtocol cp = findCP(colMap.getValue("protocolTitle"));
			cpr.setCollectionProtocol(cp);
			cpr.setParticipant(p);
			cpr.setProtocolParticipantIdentifier(colMap.getValue("PPI"));
			cpr.setActivityStatus(colMap.getValue("regActivityStatus"));
			cpr.setRegistrationDate(colMap.getDate("registrationDate"));
			try {
				cpr =  (CollectionProtocolRegistration) appService.createObject(cpr);
			} catch (ApplicationException e) {
				caTissueLoadException le = new caTissueLoadException(e,"CP registration");
				throw le;
			}
			System.out.println("Patient added to protocol: " + cp.getShortTitle());

		} 
		catch (caTissueLoadException e) {
			throw e;
		}
	}

	private SpecimenCollectionGroup addGroup()
	throws caTissueLoadException
	{
		try {

			CollectionProtocolRegistration reg = findCPR(colMap.getValue("protocolTitle"),
					colMap.getValue("PPI") );

			SpecimenCollectionGroup g = new SpecimenCollectionGroup();
			g.setCollectionProtocolRegistration(reg);

			g.setSpecimenCollectionSite(findSite(colMap.getValue("siteName")));
			//g.setSite(findSite(colMap.getValue("siteName")));

			g.setActivityStatus(colMap.getValue("groupActivityStatus"));
			g.setClinicalDiagnosis(colMap.getValue("clinicalDiagnosis"));
			g.setName(colMap.getValue("SCGName"));
			g.setClinicalStatus(colMap.getValue("clinicalStatus"));
			CollectionProtocolEvent ev = findCPEvent(colMap.getValue("protocolTitle"), 
					colMap.getValue("calendarPoint"));
			g.setCollectionProtocolEvent(ev);
			g.setCollectionStatus(colMap.getValue("groupCollectionStatus"));
			//g.setApplyEventsToSpecimens(true);


			User u = findUser(colMap.getValue("userEmail"));

			CollectionEventParameters coll = new CollectionEventParameters();
			coll.setUser(u);
			coll.setCollectionProcedure(colMap.getValue("collectionProcedure"));
			coll.setContainer(colMap.getValue("container"));
			coll.setTimestamp(colMap.getDate("groupCollTime"));
			System.out.println("Coll date: " + colMap.getDate("groupCollTime"));
			coll.setSpecimenCollectionGroup(g);

			ReceivedEventParameters rec = new ReceivedEventParameters();
			rec.setUser(u);
			rec.setReceivedQuality(colMap.getValue("receivedQuality"));
			rec.setTimestamp(colMap.getDate("groupRecTime"));
			System.out.println("Rec date: " + colMap.getDate("groupRecTime"));
			rec.setSpecimenCollectionGroup(g);

			Collection<SpecimenEventParameters> events = new HashSet<SpecimenEventParameters>();
			events.add(coll);
			events.add(rec);

			g.setSpecimenEventParametersCollection(events);
			



			SpecimenCollectionGroup gg;
			try {
				gg = (SpecimenCollectionGroup) appService.createObject(g);
			} catch (ApplicationException e) {
				caTissueLoadException le = new caTissueLoadException(e,"Specimen Collection Group");
				throw le;
			}
			collectionGroupsLoaded++;
			System.out.println("Group added label: " + gg.getName());

			return gg;

		} 
		catch (caTissueLoadException e) {
			throw e;
		}
	}

	private void addSpecimen() 
	throws caTissueLoadException {
		try {
			String specType = colMap.getValue("specimenType");
			// Instantiate the right subclass of specimen for this specimen type
			Specimen s = (Specimen) specimenClassMap.get(specType).newInstance();
			// Handle the Not Specified situation
			if (colMap.getValue("specimenType").endsWith("Not Specified")) {

				s.setSpecimenType("Not Specified");
			}
			else
				s.setSpecimenType(colMap.getValue("specimenType"));


			s.setIsAvailable(new Boolean(colMap.getValue("specimenAvailable")));
			s.setLabel(colMap.getValue("label"));
			s.setPathologicalStatus(colMap.getValue("pathologicalStatus"));
			SpecimenCharacteristics sc = new SpecimenCharacteristics();
			sc.setTissueSide(colMap.getValue("tissueSide"));
			sc.setTissueSite(colMap.getValue("tissueSite"));
			s.setSpecimenCharacteristics(sc);

			s.setLineage(colMap.getValue("lineage"));

			Double q = null;
/*			if (s.getClass() == TissueSpecimen.class)
				q = new QuantityInGram();
			if (s.getClass() == FluidSpecimen.class) 
				q = new QuantityInMilliliter();
			if (s.getClass() == CellSpecimen.class)
				q = new QuantityInCount();
			if (s.getClass() == MolecularSpecimen.class)
				q = new QuantityInMicrogram();
			if (q == null)
				q = new Quantity();
*/			
			q = new Double(colMap.getValue("quantity"));
			
			s.setInitialQuantity(q);
			//s.setAvailableQuantity(q);
			//s.setQuantity(q);
			// get another instance of the same class instead of going through our cascade again!
			//Quantity aq = q.getClass().newInstance();
			//aq.setValue(new Double(colMap.getValue("availableQuantity")));
			Double aq = new Double(colMap.getValue("availableQuantity"));
			s.setAvailableQuantity(aq);


			if (colMap.getValue("SCGName") != null)
				s.setSpecimenCollectionGroup(findSCG(colMap.getValue("SCGName")));
			if (colMap.getValue("parentSpecimenLabel") != null) {
				Specimen parent = findSpecimen(colMap.getValue("parentSpecimenLabel"));
				s.setParentSpecimen(parent);
				if (s.getSpecimenCollectionGroup() == null)
					s.setSpecimenCollectionGroup(parent.getSpecimenCollectionGroup());
			}

			s.setActivityStatus(colMap.getValue("specimenActivityStatus"));

			User u = findUser(colMap.getValue("userEmail"));

			CollectionEventParameters specColl = new CollectionEventParameters();
			specColl.setUser(u);
			specColl.setCollectionProcedure(colMap.getValue("collectionProcedure"));
			specColl.setContainer(colMap.getValue("container"));
			specColl.setTimestamp(colMap.getDate("specCollTime"));

			ReceivedEventParameters specRec = new ReceivedEventParameters();
			specRec.setUser(u);
			specRec.setReceivedQuality(colMap.getValue("receivedQuality"));
			specRec.setTimestamp(colMap.getDate("specRecTime"));

			Collection specEvents = new HashSet();
			specEvents.add(specColl);
			specEvents.add(specRec);
			


			s.setSpecimenEventCollection(specEvents);

			Specimen ss;
			try {
				ss = (Specimen) appService.createObject(s);
			} catch (ApplicationException e) {
				caTissueLoadException le = new caTissueLoadException(e,"Specimen");
				throw le;
			}
			specimensLoaded++;
			System.out.println("Specimen added label: " + ss.getLabel());
		} 
		catch (caTissueLoadException e) {
			throw e;
		}
		catch (InstantiationException e) {
			caTissueLoadException le = new caTissueLoadException(e,InstantiationException.class.getName());
			throw le;
		} catch (IllegalAccessException e) {
			caTissueLoadException le = new caTissueLoadException(e,IllegalAccessException.class.getName());
			throw le;
		}
	}


	private CollectionProtocolRegistration findCPR(String protocolTitle, String ppi){
		// So far have decided not to cache these because we don't expect there to be many repetitions 
		// within a file. It would be a long cache and would take longer to search - so may not save any time.


		// Search for the specified registration
		CollectionProtocolRegistration reg = new CollectionProtocolRegistration();
		CollectionProtocol c = new CollectionProtocol();
		c.setTitle(protocolTitle);
		reg.setCollectionProtocol(c);
		reg.setProtocolParticipantIdentifier(ppi);

		CollectionProtocolRegistration r = null;
		List resultList;
		try {
			resultList = appService.search(CollectionProtocolRegistration.class,reg);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) 
			{
				r = (CollectionProtocolRegistration) resultsIterator.next();
				System.out.println("Registration Found ---->  :: " + r.getProtocolParticipantIdentifier());
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	private CollectionProtocol findCP(String protocolTitle){

		if (cpCache.containsKey(protocolTitle))
			return cpCache.get(protocolTitle);

		// Search for the specified registration
		CollectionProtocol c = new CollectionProtocol();
		c.setTitle(protocolTitle);

		CollectionProtocol r = null;
		List resultList;
		try {
			resultList = appService.search(CollectionProtocol.class,c);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) 
			{
				r = (CollectionProtocol) resultsIterator.next();
				System.out.println("Protocol Found ---->  :: " + r.getTitle());
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cpCache.put(protocolTitle, r);
		return r;
	}

	private CollectionProtocolEvent findCPEvent(String protocolTitle, String label){

		if (cpEventCache.containsKey(protocolTitle+label))
			return cpEventCache.get(protocolTitle+label);

		// Search for the specified registration
		CollectionProtocolEvent ev = new CollectionProtocolEvent();
		CollectionProtocol c = new CollectionProtocol();
		c.setTitle(protocolTitle);
		ev.setCollectionProtocol(c);
		ev.setCollectionPointLabel(label);

		CollectionProtocolEvent r = null;
		List resultList;
		try {
			resultList = appService.search(CollectionProtocolEvent.class,ev);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) 
			{
				r = (CollectionProtocolEvent) resultsIterator.next();
				System.out.println("Protocol Event Found ---->  :: " + r.getCollectionPointLabel());
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cpEventCache.put(protocolTitle+label, r);
		return r;
	}

	private User findUser(String email){

		if (userCache.containsKey(email))
			return userCache.get(email);

		// Search for the specified registration
		User u = new User();
		u.setEmailAddress(email);

		User r = null;
		List resultList;
		try {
			resultList = appService.search(User.class,u);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) 
			{
				r = (User) resultsIterator.next();
				System.out.println("User Found ---->  :: " + r.getEmailAddress());
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userCache.put(email, r);
		return r;
	}

	private Specimen findSpecimen(String label){

		if (specimenCache.containsKey(label))
			return specimenCache.get(label);

		// Search for the specified registration
		Specimen s = new Specimen();
		s.setLabel(label);

		Specimen r = null;
		List resultList;
		try {
			resultList = appService.search(Specimen.class,s);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) 
			{
				r = (Specimen) resultsIterator.next();
				System.out.println("Specimen Found ---->  :: " + r.getLabel());
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		specimenCache.put(label, r);
		return r;
	}

	private SpecimenCollectionGroup findSCG(String name){

		if (SCGCache.containsKey(name))
			return SCGCache.get(name);

		// Search for the specified registration
		SpecimenCollectionGroup g = new SpecimenCollectionGroup();
		g.setName(name);

		SpecimenCollectionGroup r = null;
		List resultList;
		try {
			resultList = appService.search(SpecimenCollectionGroup.class,g);
			for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();) 
			{
				r = (SpecimenCollectionGroup) resultsIterator.next();
				System.out.println("Specimen Collection Group Found ---->  :: " + r.getName());
			}
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SCGCache.put(name, r);
		return r;
	}

	private Site findSite(String siteName){

		if (siteCache.containsKey(siteName))
			return siteCache.get(siteName);

		// Search for the specified registration
		Site r = null;

		try {
			Site siteQuery = new Site();
			siteQuery.setName(siteName);
			List siteList = appService.search(Site.class,siteQuery);
			for (Iterator siteIterator = siteList.iterator(); siteIterator.hasNext();) 
			{
				r = (Site) siteIterator.next();
				System.out.println("Site Found ---->  :: " + r.getName());
			}
		}
		catch (Exception ex) 
		{ 
			System.out.println(ex.getMessage()); 
			ex.printStackTrace();
		}
		siteCache.put(siteName, r);
		return r;
	}

	private void loadSpecimenClassMap(String fileName) {
		BufferedReader fh;
		try {
			//	TODO fix static file reference 
			fh = new BufferedReader(new FileReader(fileName));
			String s;
			while ((s=fh.readLine())!=null){
				String f[] = s.split("\t");
				try {
					Class specimenClass = Class.forName(f[1]);
					this.specimenClassMap.put(f[0], specimenClass);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			fh.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadSpecimenClassMap() {
		String[][] specimenClasses = {
				{"RNA, poly-A enriched","MolecularSpecimen"},
				{"RNA, nuclear","MolecularSpecimen"},
				{"protein","MolecularSpecimen"},
				{"cDNA","MolecularSpecimen"},
				{"RNA","MolecularSpecimen"},
				{"DNA","MolecularSpecimen"},
				{"RNA, cytoplasmic","MolecularSpecimen"},
				{"Total Nucleic Acid","MolecularSpecimen"},
				{"Whole Genome Amplified DNA","MolecularSpecimen"},
				{"Frozen Cell Pellet","CellSpecimen"},
				{"Fixed Cell Block","CellSpecimen"},
				{"Frozen Cell Block","CellSpecimen"},
				{"Cryopreserved Cells","CellSpecimen"},
				{"Slide","CellSpecimen"},
				{"Whole Bone Marrow","FluidSpecimen"},
				{"Saliva","FluidSpecimen"},
				{"Plasma","FluidSpecimen"},
				{"Body Cavity Fluid","FluidSpecimen"},
				{"Milk","FluidSpecimen"},
				{"Pericardial Fluid","FluidSpecimen"},
				{"Lavage","FluidSpecimen"},
				{"Whole Blood","FluidSpecimen"},
				{"Vitreous Fluid","FluidSpecimen"},
				{"Gastric Fluid","FluidSpecimen"},
				{"Bone Marrow Plasma","FluidSpecimen"},
				{"Urine","FluidSpecimen"},
				{"Serum","FluidSpecimen"},
				{"Amniotic Fluid","FluidSpecimen"},
				{"Cerebrospinal Fluid","FluidSpecimen"},
				{"Bile","FluidSpecimen"},
				{"Synovial Fluid","FluidSpecimen"},
				{"Sweat","FluidSpecimen"},
				{"Feces","FluidSpecimen"},
				{"Sputum","FluidSpecimen"},
				{"Fixed Tissue","TissueSpecimen"},
				{"Fixed Tissue Block","TissueSpecimen"},
				{"Frozen Tissue Block","TissueSpecimen"},
				{"Fixed Tissue Slide","TissueSpecimen"},
				{"Frozen Tissue Slide","TissueSpecimen"},
				{"Fresh Tissue","TissueSpecimen"},
				{"Microdissected","TissueSpecimen"},
				{"Frozen Tissue","TissueSpecimen"},
				{"Molecular Not Specified","MolecularSpecimen"},
				{"Cell Not Specified","CellSpecimen"},
				{"Fluid Not Specified","FluidSpecimen"},
				{"Tissue Not Specified","TissueSpecimen"}
		};
		for (int i=0; i <  specimenClasses.length; i++){
			try {
				Class specimenClass = Class.forName("edu.wustl.catissuecore.domain."+specimenClasses[i][1]);
				this.specimenClassMap.put(specimenClasses[i][0], specimenClass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
