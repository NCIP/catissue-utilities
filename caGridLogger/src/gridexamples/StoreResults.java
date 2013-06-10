/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

package gridexamples;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


public class StoreResults {

	private Connection con = null;
	private PreparedStatement probeInsert = null;
	private PreparedStatement queryInsert = null;
	private PreparedStatement classQuery = null;

	public StoreResults ()
	{
//		register the JDBC client driver
		try {
			Driver d =
				(Driver)Class.forName("com.ddtek.jdbc.sequelink.SequeLinkDriver").newInstance();
		} catch(Exception e) {
			System.out.println(e);
		}
//		establish a connection to FileMaker
		try {
			con =
				DriverManager.getConnection("jdbc:sequelink://localhost:2399;serverDataSource=caGridDiscovery","ian","ian");

			probeInsert = con.prepareStatement("INSERT INTO probe (serviceType, name, host, grid, success) "+ 
			"VALUES (?, ?, ?, ?, ?)");

			queryInsert = con.prepareStatement("INSERT INTO gridQuery (probeId, objectType, objectCount, queryTime, queryException) "+ 
			"VALUES (?, ?, ?, ?, ?)");

			classQuery = con.prepareStatement("select fully_qualified_class from classdecodes where model = ? and include_in_query = 'TRUE'"); 


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[ ] args)
	{
		StoreResults db = new StoreResults();
		Long pid = db.addProbe("caTissue", "test3","http://test3", "nci_prod-1.2", "false");
		db.addGridQuery(pid, "Specimen", new Long(101), new Double(12.34), "");
		db.addGridQuery(pid, "Experiment", new Long(4567), new Double(127.1), "");
		db.addGridQuery(pid, "Experiment", null, new Double(128.1), "java.lang.Exception");
	}
	
	public  Long addProbe(String serviceType, String name, String host, String grid, String success)
	{

		Long probeId = new Long(0);
		try {

			probeInsert.setString(1, serviceType);
			probeInsert.setString(2, name);
			probeInsert.setString(3, host);
			probeInsert.setString(4, grid);
			probeInsert.setString(5, success);
			probeInsert.execute();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("select max(id) maxProbe from probe");
			rs.next();
			probeId = rs.getLong(1);
			System.out.println("Added probe: " + probeId);

		} catch(Exception e) {
			System.out.println(e);
		}
		return probeId;
	}
	
	public  void addGridQuery(Long probeId, String objectType, Long count, Double queryTime, String exception)
	{

		try {
			queryInsert.setLong(1, probeId);
			queryInsert.setString(2, objectType);
			queryInsert.setLong(3, count);
			queryInsert.setDouble(4, queryTime);
			queryInsert.setString(5, exception);
			queryInsert.execute();

		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public Set<String> getQueryClasses(String model) {
		HashSet<String> queryClasses = new HashSet<String>();
		try {
			classQuery.setString(1,model);
			ResultSet classes = classQuery.executeQuery();
			while (classes.next()) {
				//System.out.println(classes.getString(1));
				queryClasses.add(classes.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryClasses;
	}
}
