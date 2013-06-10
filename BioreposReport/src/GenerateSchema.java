/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

import org.hibernate.tool.hbm2ddl.*;
import org.hibernate.cfg.*;

public class GenerateSchema {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Configuration cfg = new Configuration().configure();
		SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.setFormat(true);
		schemaExport.setOutputFile("test.sql");
		schemaExport.create(true, false);
	}

}
