/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
 */

package gov.nih.nci.codegen.core.util;

import gov.nih.nci.codegen.framework.XMIPreprocessor;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.apache.log4j.*;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2001-2004 SAIC. Copyright 2001-2003 SAIC. This software was developed in conjunction with the National Cancer Institute,
 * and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
 * the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 * SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * FixEAXMI provides methods to convert the XMI produced by <I>Enterprise Architect</I> to <I>NetBean MDR XMI Reader </I>compatible format
 * @author caBIO Team
 * @version 1.0
 */
public class AutoXMIPreprocessor implements XMIPreprocessor {

	// Known XMI and the preprocessors to use
	private Map preprocessorMap = new HashMap();

	private static Logger log = Logger.getLogger(AutoXMIPreprocessor.class);

	/**
	 * Empty implementation of the XMIProcessor interface.
	 *
	 * @see gov.nih.nci.codegen.framework.XMIPreprocessor#fix(String xmiIn, String xmiOut)
	 *
	 * @param xmiIn The path and filename of the XMI file produced by a UML modeling tool
	 * @param xmiOut The path and filename of the fixed XMI file ready for consumption by the SDK tools
	 * @throws JDOMException
	 * @throws JaxenException
	 * @throws IOException
	 */
	/*AutoXMIPreprocessor() {
		// TO DO - load this from a file
		preprocessorMap.put(new XMIExporter("Enterprise Architect", "4.1RR"), "gov.nih.nci.codegen.core.util.EAXMIPreprocessor");
		preprocessorMap.put(new XMIExporter("MagicDraw UML", "9.5"), "gov.nih.nci.codegen.core.util.MagicDrawXMIPreprocessor");
	}*/


	public void fix(String xmiIn, String xmiOut)  throws JDOMException,
	JaxenException,IOException {


		Element model;
		Element root;
		Element xmiDocumentation;
		//XMIExporter xmiExporter;
		String xmiExporterName;
		String xmiExporterVersion;

		try {
			root = (new SAXBuilder(false)).build(new File(xmiIn))
			.getRootElement();

			model = (Element) (new JDOMXPath(
			"*[local-name()='XMI.content']/*[local-name()='Model']"))
			.selectSingleNode(root);

			xmiDocumentation = (Element) (new JDOMXPath(
			"*[local-name()='XMI.header']/*[local-name()='XMI.documentation']"))
			.selectSingleNode(root);

			xmiExporterName = (String) (new JDOMXPath(
			"./*[local-name()='XMI.exporter']"))
			.valueOf(xmiDocumentation);

			xmiExporterVersion = (String) (new JDOMXPath(
			"./*[local-name()='XMI.exporterVersion']"))
			.valueOf(xmiDocumentation);

			//xmiExporter = new XMIExporter(xmiExporterName, xmiExporterVersion);

		} catch (IOException ioex) {
			throw new IOException("Could not open XMI file for input");
		} catch (JDOMException jdomex) {
			throw new JDOMException("Could not extract nodes from XMI file");
		} catch (JaxenException ex) {
			throw new JaxenException("Could not perform XPath query on XMI file");
		}

		log.info("XMI is from: " + xmiExporterName + " Version:" + xmiExporterVersion);
		// Pass the model off to the appropriate Preprocessor

		//String className = (String) preprocessorMap.get(xmiExporter);
		//log.info("Preprocessor class is  " + className);

		XMIPreprocessor preprocessor = null;

		if (xmiExporterName.equals("Enterprise Architect" )
				&& xmiExporterVersion.equals("4.1RR"))				
			preprocessor = new EAXMIPreprocessor();
		else if (xmiExporterName.equals("MagicDraw UML")
				&& xmiExporterVersion.equals("9.5"))				
			preprocessor = new MagicDrawXMIPreprocessor();
		log.info("Preprocessor class is  " + preprocessor.getClass().getName());

		//preprocessor = (XMIPreprocessor)( Class.forName( className ).newInstance() );
		if (preprocessor != null)
			preprocessor.fix(xmiIn, xmiOut);
	}



	/*private class XMIExporter 
	{
		protected String xmiExporter;
		protected String xmiExporterVersion;

		XMIExporter(String xmiExporter, String xmiExporterVersion) {
			this.xmiExporter = xmiExporter;
			this.xmiExporterVersion = xmiExporterVersion;
		}

		public boolean equals(Object v) {

			log.info("Comparing " + v.getClass().getName());

			if (v.getClass().getName() != "gov.nih.nci.codegen.core.util.AutoXMIPreprocessor$XMIExporter")
				return false;

			XMIExporter e = (XMIExporter) v;
			return (this.xmiExporter.equals(e.xmiExporter) &&
					this.xmiExporterVersion.equals(e.xmiExporterVersion)
			);
		}*/
}

