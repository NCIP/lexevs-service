package edu.mayo.cts2.framework.plugin.service.lexevs.bulk.codesystemversion;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Test;

import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;

@LoadContent(contentPath="lexevs/test-content/Automobiles.xml")
public class SearchExtensionBulkDownloaderTestIT extends AbstractTestITBase {

	@Resource
	private SearchExtensionBulkDownloader downloader;
	
	@Test
	public void testDownload(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		CodingSchemeReference codingScheme = new CodingSchemeReference();
		codingScheme.setCodingScheme("Automobiles");
		
		this.downloader.download(out, new HashSet<CodingSchemeReference>(Arrays.asList(codingScheme)), 
			Arrays.asList(
					SearchExtensionBulkDownloader.CODE_FIELD,
					SearchExtensionBulkDownloader.DESCRIPTION_FIELD
				),
			'|');
		
		String result = new String(out.toByteArray());
		
		assertTrue(result, result.contains("C0001|Car"));
		assertTrue(result, result.contains("73|Oldsmobile"));
		//... etc...
	}
}
