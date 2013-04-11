package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
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

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.profile.ResourceQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

public class CommonUtils {

	// Private constructor - case where every method in class is static
	private CommonUtils() {
		super();
	}

	public static <T extends ResourceQuery> boolean hasCodingSchemeRenderings(QueryData<T> queryData, CodingSchemeRenderingList csrFilteredList){
		boolean answer = false;
		if((queryData.getFilters() != null) && (csrFilteredList != null) && (csrFilteredList.getCodingSchemeRenderingCount() > 0)){
			answer = true;
		}
		return answer;
	}
	
	public static <T extends ResourceQuery> boolean queryReturnsData(
			QueryData<T> queryData,
			CodingSchemeRenderingList codingSchemeRenderingList){
		boolean found = false;
		String localName, version;
		CodingSchemeSummary codingSchemeSummary;
		
		int count = codingSchemeRenderingList.getCodingSchemeRenderingCount();
		
		for(int index=0; index < count; index++){
			codingSchemeSummary = codingSchemeRenderingList.getCodingSchemeRendering(index).getCodingSchemeSummary();
			localName = codingSchemeSummary.getLocalName();
			version = codingSchemeSummary.getRepresentsVersion();
	
			if(localName.equals(queryData.getNameVersionPairName()) && 
				version.equals(queryData.getVersionOrTag().getVersion())){
				found = true;
			}
		}		
			
		return found;
	}


	public static ResolvedConceptReference getResolvedConceptReference(
			LexBIGService lexBigService, 
			VersionNameConverter nameConverter, 
			EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {

		NameVersionPair codingSchemeName;
		CodingSchemeVersionOrTag versionOrTag;
		ConceptReferenceList referenceList;
		CodedNodeSet codedNodeSet;
		String versionName;
		
		versionName = identifier.getCodeSystemVersion().getName();
		
		codingSchemeName = nameConverter.fromCts2VersionName(versionName);
		
		ScopedEntityName entityName = identifier.getEntityName();
		versionOrTag = Constructors.createCodingSchemeVersionOrTagFromVersion(codingSchemeName.getVersion());
		referenceList = Constructors.createConceptReferenceList(entityName.getName(), entityName.getNamespace(), codingSchemeName.getName());
		
		try {
			codedNodeSet = lexBigService.getNodeSet(codingSchemeName.getName(), versionOrTag, null);
			codedNodeSet = codedNodeSet.restrictToCodes(referenceList);
			
			ResolvedConceptReferencesIterator iterator = codedNodeSet.resolve(null, null, null);
			
			if(! iterator.hasNext()){
				return null;
			} else {
				return iterator.next();
			}
		} catch (LBException e) {
			throw new RuntimeException();
		}
		
	}


	
	

	public static ResolvedConceptReferencesIterator getResolvedConceptReferencesIterator(CodedNodeSet codedNodeSet, SortCriteria sortCriteria){
		ResolvedConceptReferencesIterator iterator = null;
		if(codedNodeSet != null){
			try {
				// With all null arguments the iterator will access the entire codeNodeSet
				// This call will execute the set of filters determined in loop above
				SortOptionList sortOptions = null;
				LocalNameList propertyNames = null;
				PropertyType [] propertyTypes = null; 
				
				iterator = codedNodeSet.resolve(sortOptions, propertyNames, propertyTypes);
			} catch (LBInvocationException e) {
				throw new RuntimeException(e);
			} catch (LBParameterException e) {
				throw new RuntimeException(e);
			}
		}		
		return iterator;
	}


	public static NameVersionPair getNamePair(
			VersionNameConverter nameConverter, 
			NameOrURI identifier,
			ResolvedReadContext readContext) {
		if(identifier == null){
			return null;
		}
		String name;
		NameVersionPair namePair;
		
		if (identifier.getName() != null) {
			name = identifier.getName();
			if (!nameConverter.isValidVersionName(name)) {
				namePair = null;
			}
			else{
				namePair = nameConverter.fromCts2VersionName(name);		
			}
		} else {
			throw new UnsupportedOperationException(
					"Cannot resolve by DocumentURI yet.");
		}

		return namePair;
	}
		
}
