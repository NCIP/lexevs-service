package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.test.LexEvsTestRunner.LoadContent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.service.core.types.ActiveOrAll;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.EntityDescriptionQueryImpl;
import edu.mayo.cts2.framework.plugin.service.lexevs.service.entity.LexEvsEntityQueryService;
import edu.mayo.cts2.framework.plugin.service.lexevs.test.AbstractQueryServiceTest;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.QueryService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;

@LoadContent(contentPath = "lexevs/test-content/owl2/owl2-special-cases-Defined-Annotated.owl", loader = "OWL2Loader")
public class QueryDataTest 
	extends AbstractQueryServiceTest<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery>{
	
	@Resource
	private LexEvsEntityQueryService service;
	
	@Resource
	private VersionNameConverter nameConverter;
	

	// ---- Test methods ----	
	@Test
	public void testSetUp() {
		assertNotNull(this.service);
	}

	
	@Test
	public void testQueryData_queryNotNull() {
		final NameOrURI name = ModelUtils.nameOrUriFromName("Automobiles-1.0");
		
		// Test to see if query is set successfully
		
		// Create restriction for query
		// ----------------------------
		EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
		restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName(name.getName()));
		
		// Create query, no filters
		// -------------------------
		ResolvedReadContext resolvedReadContext = new ResolvedReadContext();
		resolvedReadContext.setActive(ActiveOrAll.ACTIVE_ONLY);
		
		EntityDescriptionQueryImpl query = new EntityDescriptionQueryImpl(null, null, restrictions);	
		query.setReadContext(resolvedReadContext);

		// Create a new EntityDescriptionQuery and verify that the read context is set
		// --------------------------------------
		
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		if (queryData.getReadContext() == null) {
			fail();
		}
	}
	
	@Override
	protected QueryService<EntityListEntry, EntityDirectoryEntry, EntityDescriptionQuery> getService() {
		return this.service;
	}
	
	@Test
	@Ignore
	@Override
	public void testCount(){
		//count not supported
	}
	
	@Override
	protected EntityDescriptionQuery getQuery() {
		return new EntityDescriptionQuery(){

			@Override
			public Query getQuery() {
				return null;
			}

			@Override
			public Set<ResolvedFilter> getFilterComponent() {
				return null;
			}

			@Override
			public ResolvedReadContext getReadContext() {
				return null;
			}

			@Override
			public EntitiesFromAssociationsQuery getEntitiesFromAssociationsQuery() {
				return null;
			}

			@Override
			public EntityDescriptionQueryServiceRestrictions getRestrictions() {
				EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();
				restrictions.getCodeSystemVersions().add(ModelUtils.nameOrUriFromName("Automobiles-1.0"));
				
				return restrictions;
			}
			
		};
	}

}
