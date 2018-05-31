package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import edu.mayo.cts2.framework.core.xml.Cts2Marshaller;
import edu.mayo.cts2.framework.core.xml.DelegatingMarshaller;
import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.test.LexEvsTestRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lexevs.dao.index.service.search.SourceAssertedValueSetSearchIndexService;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@LexEvsTestRunner.LoadContents({
        @LexEvsTestRunner.LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
public class LexEVSAssertedResolvedValuesetQueryServiceTestIT extends
        AbstractTestITBase {

    private Cts2Marshaller marshaller = new DelegatingMarshaller();
    private static SourceAssertedValueSetSearchIndexService sourceAssertedValueSetSearchIndexService;

    @Resource
    private LexEvsResolvedValueSetQueryService service;

    @BeforeClass
    public static void createIndex() throws Exception {
        // index the owl2lexevs coding scheme
        sourceAssertedValueSetSearchIndexService =
                LexEvsServiceLocator.getInstance().getIndexServiceManager().getAssertedValueSetIndexService();
        sourceAssertedValueSetSearchIndexService.createIndex(Constructors.createAbsoluteCodingSchemeVersionReference(
                "http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl", "0.1.5"));
    }

    // ---- Test methods ----
    @Test
    public void testSetUp() {
        assertNotNull(this.service);
    }


    @Test
    public void testGetResourceSummaries() throws Exception {

        LexBIGService lbs = null;
        LexEVSResolvedValueSetServiceImpl lrvssi = new LexEVSResolvedValueSetServiceImpl(lbs);

        // test retrieving resolved value sets  (Asserted Value Sets) (indexed above)
        DirectoryResult<ResolvedValueSetDirectoryEntry> dirResult = service
                .getResourceSummaries(null, null, new Page());

        assertNotNull(dirResult);
        int expecting = 4;
        int actual = dirResult.getEntries().size();
        assertEquals("Expecting " + expecting + " but got " + actual,
                expecting, actual);
    }

}
