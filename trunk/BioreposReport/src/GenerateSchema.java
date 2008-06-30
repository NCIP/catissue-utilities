
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
