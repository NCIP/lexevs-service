package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Impl.helpers.CodeHolder;
import org.LexGrid.LexBIG.Impl.helpers.ResolvedConceptReferencesIteratorImpl;
import org.LexGrid.LexBIG.Impl.helpers.lazyloading.CodeHolderFactory;
import org.LexGrid.LexBIG.Impl.helpers.lazyloading.NonProxyCodeHolderFactory;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.lucene.search.Query;
import org.lexevs.system.utility.CodingSchemeReference;

public class FakeLexEvsCodedNodeSetImpl implements org.LexGrid.LexBIG.LexBIGService.CodedNodeSet {
    private static final long serialVersionUID = 6108466665548985484L;
    protected ArrayList<FakeRestrictToMatchingDesignations> pendingOperations_ = new ArrayList<FakeRestrictToMatchingDesignations>();
    protected CodeHolder codesToInclude_ = null;
    private List<Query> queries = new ArrayList<Query>();
    private List<org.apache.lucene.search.Filter> filters = new ArrayList<org.apache.lucene.search.Filter>();
    protected CodeHolderFactory codeHolderFactory = new NonProxyCodeHolderFactory();
    
    private Set<CodingSchemeReference> references = new HashSet<CodingSchemeReference>();
    
    //This can be very large an expensive to send remotely. Make this transient
    //for now and rely on Lucene matches to reconstruct the 'toNodeListCodes' matches.
    private transient CodeHolder toNodeListCodes = null;
    
    private ActiveOption currentActiveOption;
    
    private boolean shouldCodingSchemeSpecificRestriction = true;
    
    private boolean hasMatchAllDocsQueryBeenAdded = false;

	public FakeLexEvsCodedNodeSetImpl(){
		super();
	}
	
	@Override
	public CodedNodeSet difference(CodedNodeSet codesToRemove)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet intersect(CodedNodeSet codes)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isCodeInSet(ConceptReference code)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedConceptReferencesIterator resolve(
			SortOptionList sortOptions, LocalNameList propertyNames,
			PropertyType[] propertyTypes) throws LBInvocationException,
			LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedConceptReferencesIterator resolve(
			SortOptionList sortOptions, LocalNameList filterOptions,
			LocalNameList propertyNames, PropertyType[] propertyTypes)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return new ResolvedConceptReferencesIteratorImpl();
	}

	@Override
	public ResolvedConceptReferencesIterator resolve(
			SortOptionList sortOptions, LocalNameList filterOptions,
			LocalNameList propertyNames, PropertyType[] propertyTypes,
			boolean resolveObjects) throws LBInvocationException,
			LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedConceptReferenceList resolveToList(
			SortOptionList sortOptions, LocalNameList propertyNames,
			PropertyType[] propertyTypes, int maxToReturn)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedConceptReferenceList resolveToList(
			SortOptionList sortOptions, LocalNameList filterOptions,
			LocalNameList propertyNames, PropertyType[] propertyTypes,
			int maxToReturn) throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResolvedConceptReferenceList resolveToList(
			SortOptionList sortOptions, LocalNameList filterOptions,
			LocalNameList propertyNames, PropertyType[] propertyTypes,
			boolean resolveObjects, int maxToReturn)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToCodes(ConceptReferenceList codeList)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToMatchingDesignations(String matchText,
			boolean preferredOnly, String matchAlgorithm, String language)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToMatchingDesignations(String matchText,
			SearchDesignationOption option, String matchAlgorithm,
			String language) throws LBInvocationException, LBParameterException {
		this.toNodeListCodes = null;
		FakeRestrictToMatchingDesignations op = new FakeRestrictToMatchingDesignations(matchText, option, matchAlgorithm, language);
            
		if(!this.pendingOperations_.contains(op)) {
                this.pendingOperations_.add(op);
		}
                    
		return this;
	}

	@Override
	public CodedNodeSet restrictToMatchingProperties(
			LocalNameList propertyNames, PropertyType[] propertyTypes,
			String matchText, String matchAlgorithm, String language)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToMatchingProperties(
			LocalNameList propertyNames, PropertyType[] propertyTypes,
			LocalNameList sourceList, LocalNameList contextList,
			NameAndValueList qualifierList, String matchText,
			String matchAlgorithm, String language)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToProperties(LocalNameList propertyList,
			PropertyType[] propertyTypes) throws LBInvocationException,
			LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToProperties(LocalNameList propertyList,
			PropertyType[] propertyTypes, LocalNameList sourceList,
			LocalNameList contextList, NameAndValueList qualifierList)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToStatus(ActiveOption activeOption,
			String[] status) throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet restrictToAnonymous(AnonymousOption anonymousOption)
			throws LBInvocationException, LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet union(CodedNodeSet codes) throws LBInvocationException,
			LBParameterException {
		// TODO Auto-generated method stub
		return null;
	}

}
