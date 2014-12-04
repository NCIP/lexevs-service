/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.core.url.UrlConstructor;
import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.DescriptionInCodeSystem;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.core.NameAndMeaningReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetHeader;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ResolvedValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ResolvedValueSetNameTriple;
import edu.mayo.cts2.framework.plugin.service.lexevs.transform.TransformUtils;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;
import edu.mayo.cts2.framework.plugin.service.lexevs.utility.CommonResolvedValueSetUtils;

@Component
public class ResolvedCodingSchemeTransform {

	@Resource
	private UriHandler uriHandler;
	
	@Resource
	private CommonResolvedValueSetUtils resolvedValueSetUtils;
	
	@Resource
	private UrlConstructor urlConstructor;
	
	@Resource
	private ResolvedValueSetNameTranslator resolvedValueSetNameTranslator;
	
	@Resource
	private TransformUtils transformUtils;
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;

	List<ResolvedValueSetDirectoryEntry> transform(List<CodingScheme> listcs) {
		List<ResolvedValueSetDirectoryEntry> rvsde_list = new ArrayList<ResolvedValueSetDirectoryEntry>();
		if (listcs != null) {
			for (CodingScheme cs : listcs) {
				ResolvedValueSetDirectoryEntry rvsde = transform(cs);
				rvsde_list.add(rvsde);
			}
		}
		return rvsde_list;
	}

	ResolvedValueSetDirectoryEntry transform(CodingScheme cs) {
		ResolvedValueSetDirectoryEntry entry = new ResolvedValueSetDirectoryEntry();
		entry.setResolvedValueSetURI(cs.getCodingSchemeURI());

		ResolvedValueSetNameTriple name = 
			this.resolvedValueSetNameTranslator.getResolvedValueSetNameTriple(cs.getCodingSchemeURI());
		
		entry.setHref(
			this.urlConstructor.createResolvedValueSetUrl(
				name.getValueSetName(), 
				name.getDefinitionLocalId(), 
				name.getResolutionLocalId()));

		entry.setResolvedHeader(this.transformToResolvedValueSetHeader(cs));
		
		return entry;
	}

	ResolvedValueSetHeader transformToResolvedValueSetHeader(CodingScheme cs) {
		ResolvedValueSetHeader header = new ResolvedValueSetHeader();
		List<CodeSystemVersionReference> resolvedReferences = getResolvedUsingCodeSystemList(cs);
		header.setResolvedUsingCodeSystem(resolvedReferences);
		
		ValueSetDefinitionReference resolutionOf = 
			this.transformUtils.toValueSetDefinitionReference(
				cs.getFormalName(), 
				cs.getCodingSchemeURI());
		
		header.setResolutionOf(resolutionOf);
		return header;
	}

	List<CodeSystemVersionReference> getResolvedUsingCodeSystemList(
			CodingScheme cs) {
		List<CodeSystemVersionReference> list = new ArrayList<CodeSystemVersionReference>();
		for (Property prop : cs.getProperties().getProperty()) {
			if (prop.getPropertyName()
					.equalsIgnoreCase(
							LexEVSValueSetDefinitionServices.RESOLVED_AGAINST_CODING_SCHEME_VERSION)) {
				String uri = prop.getValue().getContent();
				String version = "";
				PropertyQualifier[] qualifiers = prop.getPropertyQualifier();
				for (PropertyQualifier pq : qualifiers) {
					if (pq.getPropertyQualifierName().equalsIgnoreCase(
							LexEVSValueSetDefinitionServices.VERSION)) {
						version = pq.getValue().getContent();
					}
				}

				String lexSchemeName = codingSchemeNameTranslator.translateLexGridURIToLexGrid(uri);
				CodeSystemVersionReference csvr = new CodeSystemVersionReference();
				CodeSystemReference csr = new CodeSystemReference();
				csr.setUri(uri);
				csr.setContent(lexSchemeName);
				csvr.setCodeSystem(csr);
				
				NameAndMeaningReference versionRef = new NameAndMeaningReference();
				versionRef.setContent(lexSchemeName + "-" + version);
				csvr.setVersion(versionRef);
				list.add(csvr);
			}
		}
		return list;
	}

	URIAndEntityName transform(EntityDirectoryEntry entry) {
		URIAndEntityName synopsis = new URIAndEntityName();
		ScopedEntityName scopedEntity = entry.getName();
		if (scopedEntity != null) {
			synopsis.setName(scopedEntity.getName());
			synopsis.setNamespace(scopedEntity.getNamespace());
		}
		
		DescriptionInCodeSystem description = entry.getKnownEntityDescription()[0];
		
		synopsis.setDesignation(description.getDesignation());
		synopsis.setUri(entry.getAbout());
		synopsis.setHref(description.getHref());
		
		return synopsis;
	}

	List<URIAndEntityName> transform(DirectoryResult<EntityDirectoryEntry> data) {
		List<URIAndEntityName> list = new ArrayList<URIAndEntityName>();
		if (data != null) {
			for (EntityDirectoryEntry entry : data.getEntries()) {
				URIAndEntityName synopsis = transform(entry);
				list.add(synopsis);
			}
		}
		return list;
	}

}
