package edu.mayo.cts2.framework.plugin.service.lexevs.service.resolvedvalueset;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.CodeSystemReference;
import edu.mayo.cts2.framework.model.core.CodeSystemVersionReference;
import edu.mayo.cts2.framework.model.core.EntitySynopsis;
import edu.mayo.cts2.framework.model.core.NameAndMeaningReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.ValueSetDefinitionReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetDirectoryEntry;
import edu.mayo.cts2.framework.model.valuesetdefinition.ResolvedValueSetHeader;
import edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler;

@Component
public class ResolvedCodingSchemeTransform {

	@Resource
	private UriHandler uriHandler;

	List<ResolvedValueSetDirectoryEntry> transform(List<CodingScheme> listcs) {
		List<ResolvedValueSetDirectoryEntry> rvsde_list = new ArrayList<ResolvedValueSetDirectoryEntry>();
		for (CodingScheme cs : listcs) {
			ResolvedValueSetDirectoryEntry rvsde = transform(cs);
			rvsde_list.add(rvsde);
		}
		return rvsde_list;
	}

	ResolvedValueSetDirectoryEntry transform(CodingScheme cs) {
		ResolvedValueSetDirectoryEntry entry = new ResolvedValueSetDirectoryEntry();
		entry.setResolvedValueSetURI(cs.getCodingSchemeURI());

		entry.setResourceName("");
		entry.setHref("");
		ResolvedValueSetHeader header = new ResolvedValueSetHeader();
		List<CodeSystemVersionReference> resolvedReferences = getResolvedUsingCodeSystemList(cs);
		header.setResolvedUsingCodeSystem(resolvedReferences);
		entry.setResolvedHeader(header);
		return entry;
	}

	ResolvedValueSetHeader transformToResolvedValueSetHeader(CodingScheme cs) {
		ResolvedValueSetHeader header = new ResolvedValueSetHeader();
		List<CodeSystemVersionReference> resolvedReferences = getResolvedUsingCodeSystemList(cs);
		header.setResolvedUsingCodeSystem(resolvedReferences);
		ValueSetDefinitionReference resolutionOf = new ValueSetDefinitionReference();

		NameAndMeaningReference valueSetDefinition = new NameAndMeaningReference(
				cs.getCodingSchemeURI());
		resolutionOf.setValueSetDefinition(valueSetDefinition);
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

				CodeSystemVersionReference csvr = new CodeSystemVersionReference();
				CodeSystemReference csr = new CodeSystemReference();
				csr.setUri(uri);
				csvr.setCodeSystem(csr);
				NameAndMeaningReference versionRef = new NameAndMeaningReference();
				versionRef.setContent(version);
				csvr.setVersion(versionRef);
			}
		}
		return list;
	}

	EntitySynopsis transform(EntityDirectoryEntry entry) {
		EntitySynopsis synopsis = new EntitySynopsis();
		ScopedEntityName scopedEntity = entry.getName();
		if (scopedEntity != null) {
			synopsis.setName(scopedEntity.getName());
			synopsis.setNamespace(scopedEntity.getNamespace());
		}
		synopsis.setDesignation(entry.getResourceName());
		synopsis.setUri(entry.getHref());
		synopsis.setHref(entry.getHref());
		return synopsis;

	}

	List<EntitySynopsis> transform(DirectoryResult<EntityDirectoryEntry> data) {
		List<EntitySynopsis> list = new ArrayList<EntitySynopsis>();
		if (data != null) {
			for (EntityDirectoryEntry entry : data.getEntries()) {
				EntitySynopsis synopsis = transform(entry);
				list.add(synopsis);
			}
		}
		return list;
	}

}
