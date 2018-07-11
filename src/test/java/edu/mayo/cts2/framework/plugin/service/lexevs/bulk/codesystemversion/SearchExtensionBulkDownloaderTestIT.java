/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.codesystemversion;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
public class SearchExtensionBulkDownloaderTestIT extends AbstractTestITBase {

	@Resource
	private SearchExtensionBulkDownloader downloader;
	
	@Test
	public void testDownload(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		CodingSchemeReference codingScheme = new CodingSchemeReference();
		codingScheme.setCodingScheme("Automobiles");
		
		this.downloader.download(out, new HashSet<CodingSchemeReference>(Arrays.asList(codingScheme)), null,
			Arrays.asList(
					SearchExtensionBulkDownloader.CODE_FIELD,
					SearchExtensionBulkDownloader.DESCRIPTION_FIELD
				),
			'|');
		
		String result = new String(out.toByteArray());
		
		assertTrue(result, result.contains("C0001|Car"));
		
		// By default, we are not returning inactive entities.  
		// Oldsmobile should not be there.
		assertTrue(result, !result.contains("73|Oldsmobile"));
		//... etc...
	}
}
