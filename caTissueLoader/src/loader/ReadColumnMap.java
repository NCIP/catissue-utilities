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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.flatpack.util.ParserUtils;

import loader.Column;
import loader.ColumnMap;
import loader.ValueMap;


public class ReadColumnMap {

	private List data;
	//private String[] data;
	private ColumnMap cMap;
	private HashMap<String,Column> colMap = new HashMap<String,Column>();
	private DateFormat df;
	

	public void getColumnMapFromFile(String path) {
		
        // create a JAXBContext capable of handling classes generated into
        // the loader package
        JAXBContext jc;
		try {
			jc = JAXBContext.newInstance( "loader",
					Thread.currentThread().getContextClassLoader() );
			// create an Unmarshaller
	        Unmarshaller u = jc.createUnmarshaller();
	        
	        // unmarshal a instance document 
	        cMap = 
	            (ColumnMap)u.unmarshal( new FileInputStream( path ) );	        
	        List colList = cMap.getColumn();

            
	        System.out.println("\n------Mapping details-----\n");
	        // iterate over List
	        for( Iterator iter = colList.iterator(); iter.hasNext(); ) {
	            Column c = (Column) iter.next();
	            // Store in hash using an uppercase key so we can do
	            // a case insensitive search
	            colMap.put(c.getAttributeName().getValue().toUpperCase(), c);
	            //colMap.put(c.getAttributeName().toString(), c);
	            System.out.print(c.getAttributeName());
	            if (c.getColumnNo() != null)
	            	System.out.println(" In column: " + c.getColumnNo().intValue());
	            if (c.getFixedValue() != null)
	            	System.out.println(" Fixed Value:" + c.getFixedValue());
	        }
	        System.out.println("\n--------------------------\n");

			df = new SimpleDateFormat(cMap.getDateFormat());

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	}
	
	public ColumnMap getColumnMap() {
		return cMap;
	}
	
	public void setDataArray(List f) {
		data = f;
	}
/*	public void setDataArray(String f[]) {
		data = f;
	}
*/	
	public String getValue(String modelAttribute) {
		
		Column c = colMap.get(modelAttribute.toUpperCase());
		if (c == null)
			return null;
		if (c.getColumnNo() != null) {
			String fromValue = (String) data.get(c.getColumnNo().intValue()-1);
			if (c.getValueMapping() != null) {
				List valueMap = c.getValueMapping().getValueMap();
				for (Iterator values = valueMap.iterator(); values.hasNext();) {
					ValueMap v = (ValueMap)values.next();
					if ( (v.isIgnoreCase() && fromValue.equalsIgnoreCase(v.getFromValue()) )  
							|| fromValue.equals(v.getFromValue())
						) {
						System.out.println(modelAttribute + ":" + fromValue + ":" + v.getToValue());
						return v.getToValue();
					}
				}
			}
			//System.out.println(modelAttribute + ":" + fromValue);
			// if we can't find what was in the value map use what was in the file
			return fromValue;
		}
		else if (c.getFixedValue() != null) {
			System.out.println(modelAttribute + ":fixed:" + c.getFixedValue());
			return c.getFixedValue();
		}
		// we would drop through to here if 
		//  no fixed value or column was found
		return null;
	}
	
	public Date getDate(String modelAttribute) 
		throws caTissueLoadException {
		Date d = null;
		String value = getValue(modelAttribute);
		if (value == null)
			return null;
		if (getValue(modelAttribute).equals(""))
			return d;
		try {
			d =  df.parse(getValue(modelAttribute));
		} catch (ParseException e) {
			caTissueLoadException le = new caTissueLoadException(e, modelAttribute);
			throw(le);
		}
		return d;
	}
	
	public boolean getReadHeader() {
		return cMap.isHeaderRow();
	}
	
	public List<String> getRowClasses() {
		return (List<String>)cMap.getRowClass();
	}
	
	public char getColDelimiter() {
		if (cMap.getFileType().equalsIgnoreCase("xlText"))
			return '\t';
		if (cMap.getFileType().equalsIgnoreCase("excelCSV"))
			return ',';
		
		//default - better than nothing!
		return '\t';
	}

	public char getTextQualifier() {
		return '"' ;
	}
}
