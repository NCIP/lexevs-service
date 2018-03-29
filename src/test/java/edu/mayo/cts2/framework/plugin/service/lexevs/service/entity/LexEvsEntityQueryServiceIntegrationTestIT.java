package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContents;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import edu.mayo.cts2.framework.core.config.ServerContext;
import edu.mayo.cts2.framework.core.config.TestServerContext;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.entity.EntityDescriptionMsg;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractTestITBase;
import edu.mayo.cts2.framework.service.profile.codesystemversion.CodeSystemVersionReadService;
import edu.mayo.cts2.framework.webapp.naming.CodeSystemVersionNameResolver;
import edu.mayo.cts2.framework.webapp.rest.command.RestReadContext;
import edu.mayo.cts2.framework.webapp.rest.controller.EntityDescriptionController;

@LoadContents({
	@LoadContent(contentPath="lexevs/test-content/Automobiles.xml"),
	@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader") })
public class LexEvsEntityQueryServiceIntegrationTestIT extends AbstractTestITBase {

    @Resource
    private LexEvsEntityReadService lexEvsEntityReadService;

    @Test
    public void testGetEntityWithColon(){
        EntityDescriptionController controller = new EntityDescriptionController() {
            {
                ServerContext context = new TestServerContext();

                super.setServerContext(context);
            }
        };

        CodeSystemVersionNameResolver resolver = Mockito.mock(CodeSystemVersionNameResolver.class);
        Mockito.when(resolver.getCodeSystemVersionNameFromVersionId(
                any(CodeSystemVersionReadService.class),
                anyString(),
                anyString(),
                any(ResolvedReadContext.class))).thenReturn("Automobiles-1.0");

        controller.setCodeSystemVersionNameResolver(resolver);
        controller.setEntityDescriptionReadService(this.lexEvsEntityReadService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<EntityDescriptionMsg> result = (ResponseEntity) controller.getEntityDescriptionOfCodeSystemVersionByName(request, new RestReadContext(), "Automobiles", "Automobiles-1.0", "Has%3AColon");

        assertEquals("Has:Colon", ModelUtils.getEntity(result.getBody().getEntityDescription()).getEntityID().getName());
    }

}
