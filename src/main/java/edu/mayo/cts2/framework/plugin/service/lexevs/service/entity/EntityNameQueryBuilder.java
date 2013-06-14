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
		StringBuilder sb = new StringBuilder(
			String.format("(code:%s AND namespace:%s)", 
				QueryParser.escape(name.getName()),
				QueryParser.escape(
					this.codingSchemeNameTranslator.translateToLexGrid(name.getNamespace()))));	
		
		return sb.toString();
	}
}
