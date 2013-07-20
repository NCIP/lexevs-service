package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.apache.commons.lang.StringUtils;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.ResolvedValueSetNameTranslator;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQuery;

@Component
public final class CommonResolvedValueSetUtils {

	@Resource
	private VersionNameConverter nameConverter;
	@Resource
	private LexBIGService lexBIGService;
	
	@Resource
	private ResolvedValueSetNameTranslator resolvedValueSetNameTranslator;

	private CommonResolvedValueSetUtils(){
		super();
	}

	public List<CodingScheme> restrictByQuery(
			List<CodingScheme> lexCodingSchemes, ResolvedValueSetQuery query)
			throws LBException {
		List<CodingScheme> temp = new ArrayList<CodingScheme>();
		List<AbsoluteCodingSchemeVersionReference> inCodingSchemes = new ArrayList<AbsoluteCodingSchemeVersionReference>();
		if (query == null
				|| query.getResolvedValueSetQueryServiceRestrictions() == null) {
			return lexCodingSchemes;
		}
		ResolvedValueSetQueryServiceRestrictions restrictions = query
				.getResolvedValueSetQueryServiceRestrictions();
		inCodingSchemes
				.addAll(getAbsoluteCodingSchemeVersionReferenceList(restrictions
						.getCodeSystemVersions()));
		inCodingSchemes
				.addAll(getAbsoluteCodingSchemeVersionReferenceList(restrictions
						.getCodeSystems()));
		
		temp= filterOnCodingSchemes(lexCodingSchemes, inCodingSchemes);
		temp= filterOnDefinitions(temp, restrictions.getValueSetDefinitions());
		temp= filterOnEntities(temp, restrictions.getEntities());
		return temp;
	}

	
	public List<CodingScheme> filterOnCodingSchemes(List<CodingScheme> csList,
			List<AbsoluteCodingSchemeVersionReference> codingSchemeVersionList)  {
		List<CodingScheme> temp = new ArrayList<CodingScheme>();
		for (CodingScheme cs : csList ){
			if( matchesAbsoluteCodingSchemeVersionReferences(cs, codingSchemeVersionList)){
				temp.add(cs);
			}
			
		}
		return temp;	
	}
		
	private boolean matchesAbsoluteCodingSchemeVersionReferences(
			CodingScheme cs,
			List<AbsoluteCodingSchemeVersionReference> codingSchemeVersionList) {
	   	if (codingSchemeVersionList== null || codingSchemeVersionList.size()==0){
    		return true;
    	}
 
		for (Property prop: cs.getProperties().getProperty()) {
			String csURI=null;
			String version=null;
			if (prop.getPropertyName().equalsIgnoreCase(LexEVSValueSetDefinitionServices.RESOLVED_AGAINST_CODING_SCHEME_VERSION)) {
				if (prop.getValue() != null) {
					csURI= prop.getValue().getContent();
				}
				if (prop.getPropertyQualifier() != null && prop.getPropertyQualifier().length > 0) {
					PropertyQualifier pq= prop.getPropertyQualifier()[0];
					version= pq.getValue().getContent();
				}
				
				if (matchFoundInAbsoluteCodingSchemeVersionReferenceList(csURI, version, codingSchemeVersionList)) {
					return true;
				}
			}
			

		}
		
		return false;
	}

    private boolean matchFoundInAbsoluteCodingSchemeVersionReferenceList(String csURI, String version, List<AbsoluteCodingSchemeVersionReference> codingSchemeVersionList) {
    	for (AbsoluteCodingSchemeVersionReference acsvr: codingSchemeVersionList) {
    		if (csURI != null && csURI.equalsIgnoreCase(acsvr.getCodingSchemeURN())) {
    			if (version != null &&  version.equalsIgnoreCase(acsvr.getCodingSchemeVersion())) {
    				return true;
    			} else  if (acsvr.getCodingSchemeVersion()== null) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    
    
	public List<CodingScheme> filterOnDefinitions(List<CodingScheme> csList,
			Set<NameOrURI> definitions)  {
		List<CodingScheme> temp = new ArrayList<CodingScheme>();
		for (CodingScheme cs : csList) {
			if( matchesValueSetDefinitions(cs, definitions)){
				temp.add(cs);
			}
			
		}
		return temp;	
	}
	
	
	public List<CodingScheme> filterOnEntities(List<CodingScheme> csList,
			Set<EntityNameOrURI> entities)  throws LBException {
		List<CodingScheme> temp = new ArrayList<CodingScheme>();
		if (csList != null) {
			for (CodingScheme cs : csList) {
				if (matchesEntities(cs, entities)) {
					temp.add(cs);
				}
			}
		}
		return temp;
	}

	public boolean matchesValueSetDefinitions(CodingScheme cs,
			Set<NameOrURI> definitions) {
		if (definitions == null || definitions.isEmpty()) {
			return true;
		}
		if (cs != null) {
			for (NameOrURI def : definitions) {
				if (cs.getCodingSchemeURI().equalsIgnoreCase(def.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean matchesEntities(CodingScheme cs,
			Set<EntityNameOrURI> entities) throws LBException {
		if (entities == null || entities.isEmpty()) {
			return true;
		}
		ConceptReferenceList conceptReferenceList = new ConceptReferenceList();
		for (EntityNameOrURI entity : entities) {
			if (entity.getEntityName() != null) {
				ConceptReference conceptRef = new ConceptReference();
				conceptRef.setCode(entity.getEntityName().getName());
				conceptRef.setCodeNamespace(entity.getEntityName()
						.getNamespace());
				conceptReferenceList.addConceptReference(conceptRef);
			}

		}

		CodingSchemeVersionOrTag version = Constructors
				.createCodingSchemeVersionOrTagFromVersion(cs
						.getRepresentsVersion());
		CodedNodeSet cns = lexBIGService.getNodeSet(cs.getCodingSchemeURI(),
				version, null).restrictToCodes(conceptReferenceList);
		ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null,
				null, false, -1);
		if (rcrl.getResolvedConceptReferenceCount() < 1) {
			return false;
		} else {
			return true;
		}
	}

	public List<AbsoluteCodingSchemeVersionReference> getAbsoluteCodingSchemeVersionReferenceList(
			Set<NameOrURI> codingSchemes) throws LBException {
		List<AbsoluteCodingSchemeVersionReference> list = new ArrayList<AbsoluteCodingSchemeVersionReference>();
		if (codingSchemes == null || codingSchemes.isEmpty()) {
			return list;
		}
		for (NameOrURI nameURI : codingSchemes) {
			NameVersionPair nameVersion = null;
			AbsoluteCodingSchemeVersionReference ref = null;
			if (StringUtils.isNotBlank(nameURI.getName())) {
				nameVersion = nameConverter.fromCts2VersionName(nameURI
						.getName());
				CodingSchemeVersionOrTag versionTag = Constructors
						.createCodingSchemeVersionOrTagFromVersion(nameVersion
								.getVersion());
				ref = ServiceUtility.getAbsoluteCodingSchemeVersionReference(
						nameVersion.getName(), versionTag, true);
			} else if (StringUtils.isNotBlank(nameURI.getUri())) {
				ref = Constructors.createAbsoluteCodingSchemeVersionReference(
						nameURI.getUri(), null);
			}
			if (ref != null) {
				list.add(ref);
			}
		}
		return list;
	}
	
	public Property getResolvedValueSetCodingSchemeProperty(CodingScheme cs) {
		if(cs == null) {
			return null;
		}
		
		for (Property prop: cs.getProperties().getProperty()) {
			if (prop.getPropertyName().equalsIgnoreCase(LexEVSValueSetDefinitionServices.RESOLVED_AGAINST_CODING_SCHEME_VERSION)) {
				return prop;
			}

		}
		return null;
	}

	public VersionNameConverter getCodeSystemVersionNameConverter() {
		return nameConverter;
	}

	public void setCodeSystemVersionNameConverter(VersionNameConverter converter) {
		this.nameConverter = converter;
	}

}
