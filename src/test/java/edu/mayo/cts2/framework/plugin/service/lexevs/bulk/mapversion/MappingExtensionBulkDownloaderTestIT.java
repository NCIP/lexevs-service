/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.mapversion;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/German_Made_Parts.xml"),
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath="lexevs/test-content/testMapping.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")})
public class MappingExtensionBulkDownloaderTestIT extends AbstractTestITBase {

	@Resource
	private MappingExtensionBulkDownloader downloader;
	
	@Test
	public void testDownload(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		CodingSchemeReference codingScheme = new CodingSchemeReference();
		codingScheme.setCodingScheme("MappingSample");
		
		this.downloader.download(out, codingScheme, 
			Arrays.asList(
				MappingExtensionBulkDownloader.SOURCE_CODE_FIELD, 
				MappingExtensionBulkDownloader.TARGET_CODE_FIELD
				),
			'|');
		
		String result = new String(out.toByteArray());
		
		assertTrue(result, result.contains("Ford|E0001"));
		assertTrue(result, result.contains("C0002|P0001"));
		assertTrue(result, result.contains("005|P000"));
		assertTrue(result, result.contains("Jaguar|E0001"));
		assertTrue(result, result.contains("A0001|R0001"));
		assertTrue(result, result.contains("C0001|E0001"));
	}
}
