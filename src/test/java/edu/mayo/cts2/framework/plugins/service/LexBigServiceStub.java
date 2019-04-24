/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugins.service;

import java.util.Date;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ExtensionDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.SortDescriptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.types.SortContext;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Extensions.Generic.GenericExtension;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.TerminologyServiceDesignation;
import org.LexGrid.LexBIG.Extensions.Query.Filter;
import org.LexGrid.LexBIG.Extensions.Query.Sort;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceManager;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceMetadata;
import org.LexGrid.codingSchemes.CodingScheme;

public class LexBigServiceStub implements LexBIGService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LexBigServiceStub(){
		
	}
	
	public CodingSchemeRenderingList getSupportedCodingSchemes(){
		CodingSchemeRenderingList list = null;
		
		return list;
	}

	@Override
	public CodedNodeSet getCodingSchemeConcepts(String arg0,
			CodingSchemeVersionOrTag arg1) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet getCodingSchemeConcepts(String arg0,
			CodingSchemeVersionOrTag arg1, boolean arg2) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Filter getFilter(String arg0) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtensionDescriptionList getFilterExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericExtension getGenericExtension(String arg0) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtensionDescriptionList getGenericExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HistoryService getHistoryService(String arg0) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastUpdateTime() throws LBInvocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleDescriptionList getMatchAlgorithms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeGraph getNodeGraph(String arg0,
			CodingSchemeVersionOrTag arg1, String arg2) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodedNodeSet getNodeSet(String arg0, CodingSchemeVersionOrTag arg1,
			LocalNameList arg2) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LexBIGServiceManager getServiceManager(Object arg0)
			throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LexBIGServiceMetadata getServiceMetadata() throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sort getSortAlgorithm(String arg0) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortDescriptionList getSortAlgorithms(SortContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CodingScheme resolveCodingScheme(String arg0,
			CodingSchemeVersionOrTag arg1) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resolveCodingSchemeCopyright(String arg0,
			CodingSchemeVersionOrTag arg1) throws LBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodingScheme> getMinimalResolvedVSCodingSchemes() throws LBInvocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodingScheme> getRegularResolvedVSCodingSchemes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CodingScheme> getSourceAssertedResolvedVSCodingSchemes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLexEVSBuildVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLexEVSBuildTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerminologyServiceDesignation getTerminologyServiceObjectType(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
