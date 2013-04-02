package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.SupportedNamespace;
import org.springframework.stereotype.Component;

@Component
public class LexEvsSupportedPropertiesUriHandler implements UriHandler {

	@Resource
	private LexBIGService lexBigService;

	/* 
	 * This constructs an Entity URI based on the SupportedNamespace
	 * of LexEVS.
	 * 
	 * (non-Javadoc)
	 * @see edu.mayo.cts2.framework.plugin.service.lexevs.uri.UriHandler#getEntityUri(org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference)
	 */
	@Override
	public String getEntityUri(ResolvedCodedNodeReference reference) {
		String codingSchemeName = reference.getCodingSchemeName();
		String version = reference.getCodingSchemeVersion();
		
		String name = reference.getCode();
		String namespace = reference.getCodeNamespace();

		CodingScheme codingScheme;
		try {
			codingScheme = this.lexBigService.resolveCodingScheme(
					codingSchemeName, 
					Constructors.createCodingSchemeVersionOrTagFromVersion(version));
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
		SupportedNamespace sns = 
			this.findSupportedNamespace(
				namespace,
				codingScheme.getMappings().getSupportedNamespace());
		
		return sns.getUri() + name;
	}
	
	private SupportedNamespace findSupportedNamespace(String namespace, SupportedNamespace[] namespaces){
		for(SupportedNamespace sns : namespaces){
			if(sns.getLocalId().equals(namespace)){
				return sns;
			}
		}
		return null;
	}

	@Override
	public String getCodeSystemUri(CodingScheme codingScheme) {
		return codingScheme.getCodingSchemeURI();
	}

	@Override
	public String getCodeSystemVersionUri(CodingScheme codingScheme) {
		return codingScheme.getCodingSchemeURI() + "#" + codingScheme.getRepresentsVersion();
	}

}
