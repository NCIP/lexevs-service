package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.command.restriction.ResolvedValueSetQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetQuery;
@Component
public class CommonResolvedValueSetUtils {

	@Resource
	private VersionNameConverter nameConverter;


	public  List<CodingScheme> restrictByQuery(List<CodingScheme> csList,
			ResolvedValueSetQuery query) throws Exception {
		List<CodingScheme> temp = new ArrayList<CodingScheme>();
		List<AbsoluteCodingSchemeVersionReference> inCodingSchemes=new ArrayList<AbsoluteCodingSchemeVersionReference>();
		ResolvedValueSetQueryServiceRestrictions restrictions = query
				.getResolvedValueSetQueryServiceRestrictions();
		inCodingSchemes.addAll(getAbsoluteCodingSchemeVersionReferenceList(restrictions.getCodeSystemVersions()));
		inCodingSchemes.addAll(getAbsoluteCodingSchemeVersionReferenceList(restrictions.getCodeSystems()));
		
		for (CodingScheme cs : csList) {
			boolean keep = true;
			keep = matchesValueSetDefinitions(cs,
					restrictions.getValueSetDefinitions());

			if (keep) {
				temp.add(cs);
			}
		}
		return temp;
	}

	public static boolean matchesValueSetDefinitions(CodingScheme cs,
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

	public List<AbsoluteCodingSchemeVersionReference> getAbsoluteCodingSchemeVersionReferenceList(
			Set<NameOrURI> codingSchemes) throws LBException {
		List<AbsoluteCodingSchemeVersionReference> list = new ArrayList<AbsoluteCodingSchemeVersionReference>();
		if (codingSchemes== null || codingSchemes.isEmpty())
			return list;
		for (NameOrURI nameURI : codingSchemes) {
			NameVersionPair nameVersion = null;
			AbsoluteCodingSchemeVersionReference ref = null;
			if (StringUtils.isNotBlank(nameURI.getName())) {
				nameVersion = nameConverter
						.fromCts2VersionName(nameURI.getName());
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

	public VersionNameConverter getCodeSystemVersionNameConverter() {
		return nameConverter;
	}

	public void setCodeSystemVersionNameConverter(
			VersionNameConverter converter) {
		this.nameConverter = converter;
	}

	
	
	public String getName(CodingScheme codingScheme){
		return this.getName(codingScheme.getCodingSchemeName(), 
					codingScheme.getRepresentsVersion());
	}
	
	
	private String getName(String name, String version){
		return this.nameConverter.
				toCts2VersionName(name, version);
	}	

}
