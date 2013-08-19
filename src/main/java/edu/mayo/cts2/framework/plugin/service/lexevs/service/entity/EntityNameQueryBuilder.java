/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.service.entity;

import javax.annotation.Resource;

import org.apache.lucene.queryParser.QueryParser;
import org.springframework.stereotype.Component;

import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.plugin.service.lexevs.naming.CodingSchemeNameTranslator;

@Component
public class EntityNameQueryBuilder {
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;

	public String buildQuery(ScopedEntityName name){
		String escapedName = QueryParser.escape(name.getName());
		StringBuilder sb = new StringBuilder(
			String.format("(code:%s OR (code:%s AND namespace:%s))", 
				escapedName,
				escapedName,
				QueryParser.escape(
					this.codingSchemeNameTranslator.translateToLexGrid(name.getNamespace()))));	
		
		return sb.toString();
	}
}
