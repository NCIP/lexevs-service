/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.annotations.LgClientSideSafe;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class FakeCodedNodeSetImpl implements org.LexGrid.LexBIG.LexBIGService.CodedNodeSet {
    private static final long serialVersionUID = 6108466665548985484L;
    
	private List<FakeCodedNode> codedNodeList;

	public FakeCodedNodeSetImpl(){
		super();
		codedNodeList = new ArrayList<FakeCodedNode>();
	}

	public void add(FakeCodedNode fakeCodedNode) {
		this.codedNodeList.add(fakeCodedNode);
	}

	public FakeCodedNode get(int index){
		if(codedNodeList != null){
			return codedNodeList.get(index);
		}
		return null;
	}

    /**
     * @throws LBResourceUnavailableException
     * 
     */
    public FakeCodedNodeSetImpl(String codingScheme, CodingSchemeVersionOrTag tagOrVersion, LocalNameList entityTypes){
		super();
		codedNodeList = new ArrayList<FakeCodedNode>();
    	if(entityTypes != null && entityTypes.getEntryCount() > 0 ) {
    	//	this.restrictToEntityTypes(entityTypes);
        }
            
        String version = tagOrVersion.getVersion();
        FakeCodedNode codedNode = new FakeCodedNode(codingScheme, version);
        codedNodeList.add(codedNode);
    }
    
    
    @Deprecated
    public CodedNodeSet restrictToMatchingDesignations(String matchText, boolean preferredOnly, String matchAlgorithm,
            String language) {
    	return restrictToMatchingDesignations(matchText, matchAlgorithm);
    }

    public CodedNodeSet restrictToMatchingDesignations(String matchText, SearchDesignationOption option,
            String matchAlgorithm, String language) {
    	return restrictToMatchingDesignations(matchText, matchAlgorithm);
    }

    public CodedNodeSet restrictToMatchingProperties(LocalNameList propertyList, PropertyType[] propertyTypes,
            String matchText, String matchAlgorithm, String language) throws LBInvocationException,
            LBParameterException {
    	return restrictToMatchingDesignations(matchText, matchAlgorithm);
    }
    
    private CodedNodeSet restrictToMatchingDesignations(String matchText, String matchAlgorithm){
    	FakeCodedNodeSetImpl nodeset = new FakeCodedNodeSetImpl();
    	FakeCodedNode fakeCodedNode = null;
    	for(int i=0; i < codedNodeList.size(); i++){
    		if(codedNodeList.get(i).getCodingScheme().toUpperCase().equals(matchText.toUpperCase())){
    			fakeCodedNode = new FakeCodedNode(matchText, codedNodeList.get(i).getVersion());
    			nodeset.add(fakeCodedNode);
    		}
    	}
                    
		return nodeset;
    }

	@Override
	public ResolvedConceptReferencesIterator resolve(
			SortOptionList sortOptions, LocalNameList propertyNames,
			PropertyType[] propertyTypes) throws LBInvocationException,
			LBParameterException {
		ResolvedConceptReferencesIterator iterator = null;
		iterator = new FakeResolvedConceptReferencesIteratorImpl(this.codedNodeList);		
		return iterator;
	}

	@Override
	public ResolvedConceptReferencesIterator resolve(
			SortOptionList sortOptions, LocalNameList filterOptions,
			LocalNameList propertyNames, PropertyType[] propertyTypes)
			throws LBInvocationException, LBParameterException {
		ResolvedConceptReferencesIterator iterator = null;
		iterator = new FakeResolvedConceptReferencesIteratorImpl(this.codedNodeList);		
		return iterator;
	}

	@Override
	public ResolvedConceptReferencesIterator resolve(
			SortOptionList sortOptions, LocalNameList filterOptions,
			LocalNameList propertyNames, PropertyType[] propertyTypes,
			boolean resolveObjects) throws LBInvocationException,
			LBParameterException {
		ResolvedConceptReferencesIterator iterator = null;
		iterator = new FakeResolvedConceptReferencesIteratorImpl(this.codedNodeList);		
		return iterator;
	}

    ////// NOT USING --------------------

    /**
     * 
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeSet#intersect(org.LexGrid.LexBIG.LexBIGService.CodedNodeSet)
     */
    @LgClientSideSafe
    public CodedNodeSet intersect(CodedNodeSet codes) throws LBInvocationException, LBParameterException {
    	return this;
    }

    /**
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeSet#union(org.LexGrid.LexBIG.LexBIGService.CodedNodeSet)
     */
    @LgClientSideSafe
    public CodedNodeSet union(CodedNodeSet codes) throws LBInvocationException, LBParameterException {
    	return this;
    }

    /**
     * 
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeSet#difference(org.LexGrid.LexBIG.LexBIGService.CodedNodeSet)
     */
    @LgClientSideSafe
    public CodedNodeSet difference(CodedNodeSet codesToRemove) throws LBInvocationException, LBParameterException {
    	return this;
    }

    @LgClientSideSafe
    public CodedNodeSet restrictToCodes(ConceptReferenceList codeList) throws LBInvocationException,
            LBParameterException {
    	return this;
    }

    @LgClientSideSafe
    public CodedNodeSet restrictToStatus(ActiveOption activeOption, String[] conceptStatus)
            throws LBInvocationException, LBParameterException {
    	return this;
    }
    
    @Override
    public CodedNodeSet restrictToAnonymous(AnonymousOption anonymousOption) 
        throws LBInvocationException, LBParameterException {
    	return this;
    }


    /*
     * make a clone of this CodedNodeSet - used before doing unions, joins, etc
     * since the optimize process may insert new operations.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    @LgClientSideSafe
    public CodedNodeSet clone() throws CloneNotSupportedException {
        FakeCodedNodeSetImpl cns = (FakeCodedNodeSetImpl) super.clone();
        return cns;
    }

	@Override
	public Boolean isCodeInSet(ConceptReference code)
			throws LBInvocationException, LBParameterException {
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
    

}
