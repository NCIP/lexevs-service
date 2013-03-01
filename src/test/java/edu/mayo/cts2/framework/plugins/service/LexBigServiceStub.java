package edu.mayo.cts2.framework.plugins.service;

import java.util.Date;

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
}
