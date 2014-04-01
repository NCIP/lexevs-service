/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetQuery;

/**
 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
 *
 */
@Component
public class CommonValueSetUtils implements InitializingBean,
		LexEvsChangeEventObserver {
	
	@Resource
	private LexEVSValueSetDefinitionServices definitionServices;
	
	private Set<UriNamePair> valueSets = new HashSet<UriNamePair>();
	
	private Object mutex = new Object();

	public CommonValueSetUtils() {
		super();
	}
	
	/**
	 * @param lexValueSets
	 * @param query
	 * @return
	 * @throws LBException
	 */
	public List<ValueSetDefinition> restrictByQuery(
			List<ValueSetDefinition> lexValueSets, ValueSetQuery query)
			throws LBException {
		List<ValueSetDefinition> temp = new ArrayList<ValueSetDefinition>();
		if (query == null
				|| query.getRestrictions() == null) {
			return lexValueSets;
		}
		temp= filterOnCodingSchemes(lexValueSets, query.getRestrictions().getCodesystem());
		return temp;
	}

	/**
	 * @param lexValueSets
	 * @param list
	 * @return
	 */
	private List<ValueSetDefinition> filterOnCodingSchemes(
			List<ValueSetDefinition> lexValueSets,
			List<String> list) {
		List<ValueSetDefinition> temp = new ArrayList<ValueSetDefinition>();
		for (ValueSetDefinition cs : lexValueSets ){
			if( matchesAbsoluteCodingSchemeVersionReferences(cs, list)){
				temp.add(cs);
			}
			
		}
		return temp;
	}

	/**
	 * @param vsd
	 * @param list
	 * @return
	 */
	private boolean matchesAbsoluteCodingSchemeVersionReferences(
			ValueSetDefinition vsd,
			List<String> list) {
	   	if (list== null || list.size()==0){
    		return true;
    	}
	   	for(String codeSystem: list){
	   		if(vsd.getDefaultCodingScheme().equalsIgnoreCase(codeSystem)){
	   			return true;
	   		}
	   	}
		
		return false;
	}


	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver#onChange()
	 */
	@Override
	public void onChange() {
		this.buildValueSetCache();

	}

	/**
	 * Prebuild a cache of value set identifiers.
	 */
	private void buildValueSetCache() {
		synchronized(this.mutex){
			this.valueSets.clear();

		for (String uri : this.definitionServices.listValueSetDefinitionURIs()) {

			org.LexGrid.valueSets.ValueSetDefinition vsd = null;
			try {
				vsd = this.definitionServices.getValueSetDefinition(
						new URI(uri), null);
				this.valueSets.add(new UriNamePair(vsd
						.getValueSetDefinitionURI(), vsd
						.getValueSetDefinitionName()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
		}
	}

	/**
	 * @author <a href="mailto:scott.bauer@mayo.edu">Scott Bauer</a>
	 *
	 */
	public static class UriNamePair {
		private String uri;
		private String name;
		
		public UriNamePair(String uri, String name) {
			super();
			this.uri = uri;
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((uri == null) ? 0 : uri.hashCode());
			result = prime * result
					+ ((name == null) ? 0 : name.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UriNamePair other = (UriNamePair) obj;
			if (uri == null) {
				if (other.uri != null)
					return false;
			} else if (!uri.equals(other.uri))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}	
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.buildValueSetCache();

	}

}
