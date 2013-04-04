package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodeSystemVersionNameConverter;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.profile.entitydescription.name.EntityDescriptionReadId;

public class CommonUtils {

	// Private constructor - case where every method in class is static
	private CommonUtils() {
		super();
	}
	
	public static ResolvedConceptReference getResolvedConceptReference(
			LexBIGService lexBigService, 
			CodeSystemVersionNameConverter nameConverter, 
			EntityDescriptionReadId identifier,
			ResolvedReadContext readContext) {

		NameVersionPair codingSchemeName;
		CodingSchemeVersionOrTag versionOrTag;
		ConceptReferenceList referenceList;
		CodedNodeSet codedNodeSet;
		String versionName;
		
		versionName = identifier.getCodeSystemVersion().getName();
		
		codingSchemeName = nameConverter.fromCts2CodeSystemVersionName(versionName);
		
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


	
	public static <T> Object[] getRenderingPage(List<T> list, Page page){
		Object [] renderedArray = list.toArray(new CodingScheme[0]);
		Object [] renderedPage = CommonUtils.getPageFromArray(renderedArray, page);		
		return renderedPage;
	}
	
	public static <T> Object[] getPageFromArray(T[] data, Page page) {
		int start = page.getStart();
		int end = page.getEnd();
		Object [] csPage = null;
		
		if(end > data.length){
			end = data.length;
		}
		
		if ((start == 0) && (end == data.length)) {
			csPage = data.clone();
		} 
		else if(start < end){
			
			int size = end - start;
			csPage = new Object [size];
			
			for (int i = 0; i < csPage.length; i++) {
				csPage[i] = data[start + i];
			}
		}
	
		return csPage;
	}
	
	public static ResolvedConceptReferenceResults getPageFromIterator(ResolvedConceptReferencesIterator iterator,
			Page page) {
		boolean atEnd = false;
		ResolvedConceptReference[] resolvedConceptReferences = null;
		ResolvedConceptReferenceList resolvedConceptReferenceList = null;
		int start = 0, end = 0;
		try {
			if(iterator != null){
				start = page.getStart();
				end = page.getEnd();
				if(end > iterator.numberRemaining()){
					end = iterator.numberRemaining();
					atEnd = true;				
				}
				resolvedConceptReferenceList = iterator.get(start, end);
				// Get array of resolved concept references
				
				if(resolvedConceptReferenceList != null){
					resolvedConceptReferences = resolvedConceptReferenceList.getResolvedConceptReference();
				}	
			}
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		} catch (LBResourceUnavailableException e) {
			throw new RuntimeException(e);
		}
		
		return new ResolvedConceptReferenceResults(resolvedConceptReferences, atEnd);
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
			CodeSystemVersionNameConverter nameConverter, 
			NameOrURI identifier,
			ResolvedReadContext readContext) {
		String name;
		NameVersionPair namePair;
		
		if (identifier.getName() != null) {
			name = identifier.getName();
			if (!nameConverter.isValidCodeSystemVersionName(name)) {
				namePair = null;
			}
			else{
				namePair = nameConverter.fromCts2CodeSystemVersionName(name);		
			}
		} else {
			throw new UnsupportedOperationException(
					"Cannot resolve by DocumentURI yet.");
		}

		return namePair;
	}
		
}
