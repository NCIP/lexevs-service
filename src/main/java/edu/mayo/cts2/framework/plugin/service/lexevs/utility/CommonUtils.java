package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.commons.lang.StringUtils;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

public final class CommonUtils {

	// Private constructor - case where every method in class is static
	private CommonUtils() {
		super();
	}

	public static ResolvedConceptReference getLexResolvedConceptReference(
			LexBIGService lexBigService, 
			VersionNameConverter nameConverter, 
			EntityDescriptionReadId cts2EntityDescriptionReadId,
			ResolvedReadContext readContext) {

		NameVersionPair codingSchemeName;
		CodingSchemeVersionOrTag versionOrTag;
		ConceptReferenceList referenceList;
		CodedNodeSet lexCodedNodeSet;
		String cts2VersionName;
		
		cts2VersionName = cts2EntityDescriptionReadId.getCodeSystemVersion().getName();
		
		codingSchemeName = nameConverter.fromCts2VersionName(cts2VersionName);
		
		ScopedEntityName entityName = cts2EntityDescriptionReadId.getEntityName();
		versionOrTag = Constructors.createCodingSchemeVersionOrTagFromVersion(codingSchemeName.getVersion());
		referenceList = Constructors.createConceptReferenceList(
			entityName.getName(), 
			nameConverter.getCodingSchemeNameTranslator().translateToLexGrid(entityName.getNamespace()), 
			codingSchemeName.getName());
		
		try {
			lexCodedNodeSet = lexBigService.getNodeSet(codingSchemeName.getName(), versionOrTag, null);
			lexCodedNodeSet = lexCodedNodeSet.restrictToCodes(referenceList);
			
			ResolvedConceptReferencesIterator lexResolvedConceptReferencesIterator = lexCodedNodeSet.resolve(null, null, null);
			
			if(! lexResolvedConceptReferencesIterator.hasNext()){
				return null;
			} else {
				return lexResolvedConceptReferencesIterator.next();
			}
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
	}

	public static ResolvedConceptReferencesIterator getLexResolvedConceptIterator(CodedNodeSet lexCodedNodeSet, SortCriteria cts2SortCriteria){
		ResolvedConceptReferencesIterator lexResolvedConceptReferencesIterator = null;
		if(lexCodedNodeSet != null){
			try {
				// With all null arguments the iterator will access the entire codeNodeSet
				// This call will execute the set of filters determined in loop above
				SortOptionList lexSortOptions = null;
				LocalNameList lexPropertyNames = null;
				PropertyType [] lexPropertyTypes = null; 
				
				lexResolvedConceptReferencesIterator = lexCodedNodeSet.resolve(lexSortOptions, lexPropertyNames, lexPropertyTypes);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
		}		
		return lexResolvedConceptReferencesIterator;
	}
	
	public static CodingSchemeVersionOrTag convertTag(VersionTagReference tag){
		CodingSchemeVersionOrTag lexVersionOrTag = Constants.CURRENT_LEXEVS_TAG;
		String tagValue = tag.getContent();
		
		if(!StringUtils.equals(tagValue, Constants.CURRENT_TAG.getContent())){
			lexVersionOrTag = Constructors.createCodingSchemeVersionOrTagFromTag(tagValue);
		}
		
		return lexVersionOrTag;
	}
		
}
