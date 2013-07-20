package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.apache.commons.lang.StringUtils;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.plugin.service.lexevs.event.LexEvsChangeEventObserver;

@Component
public class DefaultValueSetNameTranslator 
	implements ValueSetNameTranslator, LexEvsChangeEventObserver, InitializingBean {

	@Resource
	private LexEVSValueSetDefinitionServices lexEVSValueSetDefinitionServices;
	
	@Resource
	private LexBIGService lexBigService;
	
	private Map<ValueSetNamePair,String> nameToUriMap = new HashMap<ValueSetNamePair,String>();
	private Map<String,ValueSetNamePair> uriToNameMap = new HashMap<String,ValueSetNamePair>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.buildCache();
	}
	
	public void buildCache() throws Exception {
		this.nameToUriMap.clear();
		this.uriToNameMap.clear();
	
		for(String definitionUri : this.lexEVSValueSetDefinitionServices.listValueSetDefinitions(null)){
			ValueSetDefinition definition = 
					this.lexEVSValueSetDefinitionServices.getValueSetDefinition(new URI(definitionUri), null);
			
			String name = definition.getValueSetDefinitionName();
			
			if(StringUtils.isBlank(name)){
				name = UNNAMED_VALUESET;
			}

			ValueSetNamePair pair = new ValueSetNamePair(name, ValueSetDefinitionUtils.getValueSetDefinitionLocalId(definitionUri));
			
			this.nameToUriMap.put(pair, definitionUri);
			this.uriToNameMap.put(definitionUri, pair);
		}
	}
	
	@Override
	public String getDefinitionUri(String valueSetName, String definitionLocalId){
		return this.nameToUriMap.get(new ValueSetNamePair(valueSetName, definitionLocalId));
	}
	
	@Override
	public ValueSetNamePair getDefinitionNameAndVersion(String uri){
		return this.uriToNameMap.get(uri);
	}

	@Override
	public ValueSetNamePair getCurrentDefinition(String valueSetName) {
		CodingScheme cs = null;
		try {
			cs = this.lexBigService.resolveCodingScheme(valueSetName, null);
		} catch (LBException e) {
			//didn't find it
		}
		
		if(cs != null){
			String id = ValueSetDefinitionUtils.getValueSetDefinitionLocalId(cs.getCodingSchemeURI());
			return new ValueSetNamePair(valueSetName, id);
		} else {
			for(ValueSetNamePair key : this.nameToUriMap.keySet()){
				if(StringUtils.equals(key.getValueSetName(), valueSetName)){
					return key;
				}
			}
			
			return null;
		}
	}

	@Override
	public void onChange() {
		try {
			this.buildCache();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
