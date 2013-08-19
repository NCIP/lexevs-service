/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * An Abstract BulkDownloader that will write String values to an OutputStream.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractBulkDownloader {

	/**
	 * Write the output.
	 *
	 * @param outputStream the output stream
	 * @param itr the itr
	 * @param separator the separator
	 * @param fields the fields
	 */
	protected void doWrite(OutputStream outputStream,
			Iterator<ResolvedConceptReference> itr, char separator,
			List<String> fields) {

		CsvPreference preferences = new CsvPreference.Builder('"', separator, "\r\n").build();

		ICsvListWriter listWriter = new CsvListWriter(new PrintWriter(
				outputStream), preferences);
		try {

			while (itr.hasNext()) {
				ResolvedConceptReference ref = itr.next();

				String[] fieldValues = new String[fields.size()];
				for (int i = 0; i < fields.size(); i++) {
					fieldValues[i] = this.getExtractorMap().get(fields.get(i))
							.extract(ref);
				}

				listWriter.write(fieldValues);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			try {
				listWriter.flush();
				listWriter.close();
			} catch (IOException e) {
				//
			}
		}
	}

	/**
	 * Gets the extractor map.
	 *
	 * @return the extractor map
	 */
	protected abstract Map<String, Extractor> getExtractorMap();
}
