package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;

public class PrintUtility {
	public static String createTabs(int tabCount){
		StringBuffer results = new StringBuffer();
		for(int i=0; i < tabCount; i++){
			results.append("\t");
		}
		return results.toString();
	}
	
	public static String createStringFromResolvedConceptReferenceIterator(ResolvedConceptReferencesIterator iterator) throws LBResourceUnavailableException, LBInvocationException{
		StringBuffer results = new StringBuffer();
	
		while(iterator.hasNext()){
			String objectData = createStringFromResolvedConceptReferenceObject(iterator.next(), 0); 
			if(objectData != null){
				results.append(objectData + "\n=============\n\n");
			}
		}
		
		return results.toString();
	}
	
	
	public static String createStringFromResolvedConceptReferenceObject(ResolvedConceptReference reference, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		
		results.append(tabs + " Code: " + reference.getCode() + "\n");
		results.append(tabs + " CodeNamespace: " + reference.getCodeNamespace() + "\n");
		results.append(tabs + " CodingSchemeName: " + reference.getCodingSchemeName() + "\n");
		results.append(tabs + " CodingSchemeURI: " + reference.getCodingSchemeURI() + "\n");
		results.append(tabs + " CodingSchemeVersion: " + reference.getCodingSchemeVersion() + "\n");
		results.append(tabs + " ConceptCode: " + reference.getConceptCode() + "\n");

		results.append(tabs + " EntityDescription: " + reference.getEntityDescription().getContent() + "\n");
		results.append(tabs + " Entities: \n");
		results.append(createStringFromEntityObject(reference.getEntity(), tabCount + 1) + "\n");
		results.append(tabs + " SourceOf: " + reference.getSourceOf() + "\n");
		results.append(tabs + " TargetOf: " + reference.getTargetOf() + "\n");
		
		return results.toString();
	}
	
	public static String createStringFromEntityObject(Entity entity, int tabCount){
		String tabs = createTabs(tabCount);
		StringBuffer results = new StringBuffer();

		if(entity == null){
			return "";
		}
		
		results.append(tabs + " EntityCode = " + entity.getEntityCode() + "\n");
		
		results.append(tabs + " EntityDescription = " + entity.getEntityDescription().getContent() + "\n");;
		results.append(tabs + " EntityCodeNamespace = " + entity.getEntityCodeNamespace() + "\n");;
		results.append(tabs + " Owner = " + entity.getOwner() + "\n");
		results.append(tabs + " Status = " + entity.getStatus() + "\n");

		results.append(tabs + " EntityTypeCount = " + entity.getEntityTypeCount() + "\n");
		results.append(tabs + " EntityTypes:\n");
		results.append(createStringFromEntityTypesInEntityObject(entity, tabCount + 1));
		
		results.append(tabs + " CommentCount = " + entity.getCommentCount() + "\n");
		results.append(tabs + " Comments:\n");
		results.append(createStringFromCommentsInEntityObject(entity, tabCount + 1));
		
		results.append(tabs + " DefinitionCount = " + entity.getDefinitionCount() + "\n");
		results.append(tabs + " Definitions:\n");
		results.append(createStringFromDefinitionsInEntityObject(entity, tabCount + 1));
		
		results.append(tabs + " PresentationCount = " + entity.getPresentationCount() + "\n");
		results.append(tabs + " Presentations:\n");
		results.append(createStringFromPresentationsInEntityObject(entity, tabCount + 1));
		
		results.append(tabs + " PropertyCount = " + entity.getPropertyCount() + "\n");
		results.append(tabs + " Properties:\n");
		results.append(createStringFromPropertiesInEntityObject(entity, tabCount + 1));
		
		results.append(tabs + " PropertyLinkCount = " + entity.getPropertyLinkCount() + "\n");
		results.append(tabs + " PropertyLinks:\n");
		results.append(createStringFromPropertyLinksInEntityObject(entity, tabCount + 1));
		
		
		return results.toString();
	}
	
	public static String createStringFromPresentationsInEntityObject(Entity entity, int tabCount){
		Presentation presentation;
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getPresentationCount();
		for(int i=0; i < count; i++){
			presentation = entity.getPresentation(i);
			results.append(tabs + "Value = " + presentation.getValue().getContent() + "\n");
			results.append(tabs + "--DegreeOfFidelity = " + presentation.getDegreeOfFidelity() + "\n");
			results.append(tabs + "--Language = " + presentation.getLanguage() + "\n");
			results.append(tabs + "--Owner = " + presentation.getOwner() + "\n");
			results.append(tabs + "--PropertyID = " + presentation.getPropertyId() + "\n");
			results.append(tabs + "--PropertyName = " + presentation.getPropertyName() + "\n");
			results.append(tabs + "--PropertyType = " + presentation.getPropertyType() + "\n");
			results.append(tabs + "--RepresentationalForm = " + presentation.getRepresentationalForm() + "\n");
			results.append(tabs + "--Status = " + presentation.getStatus() + "\n");
			
			results.append(tabs + "--SourceCount = " + presentation.getSourceCount() + "\n");
			results.append(tabs + "--Sources:\n");
			results.append(createStringFromSourceInPresentationObject(presentation, tabCount + 1));
			
			results.append(tabs + "--PropertyQualifierCount = " + presentation.getPropertyQualifierCount() + "\n");
			results.append(tabs + "--PropertyQualifiers:\n");
			results.append(createStringFromPropertyQualifiersInPresentationObject(presentation, tabCount + 1));
			
			results.append(tabs + "--UsageContextCount = " + presentation.getUsageContextCount() + "\n");
			results.append(tabs + "--UsageContexts:\n");
			results.append(createStringFromUsageContextsInPresentationObject(presentation, tabCount + 1));			
		}
		return results.toString();
	}
	
	public static String createStringFromSourceInPresentationObject(Presentation presentation, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = presentation.getSourceCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + presentation.getSource(i).getContent() + "\n");
		}
		return results.toString();
	}
	
	public static String createStringFromPropertyQualifiersInPresentationObject(Presentation presentation, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = presentation.getPropertyQualifierCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + presentation.getPropertyQualifier(i) + "\n");
		}
		return results.toString();
	}
	
	public static String createStringFromUsageContextsInPresentationObject(Presentation presentation, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = presentation.getUsageContextCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + presentation.getUsageContext(i) + "\n");
		}
		return results.toString();
	}
	
	public static String createStringFromPropertiesInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getPropertyCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + entity.getProperty(i) + "\n");
		}
		return results.toString();
	}
	
	public static String createStringFromPropertyLinksInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getPropertyLinkCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + entity.getPropertyLink(i) + "\n");
		}
		return results.toString();
	}
	
	public static String createStringFromDefinitionsInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getDefinitionCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + entity.getDefinition(i) + "\n");
		}
		return results.toString();
	}
	
	public static String createStringFromCommentsInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getCommentCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + entity.getComment(i) + "\n");
		}
		return results.toString();
	}

	public static String createStringFromEntityTypesInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getEntityTypeCount();
		for(int i=0; i < count; i++){
			results.append(tabs + " " + entity.getEntityType(i) + "\n");
		}
		return results.toString();
	}

	public static String codingSchemeSummary(CodingSchemeSummary codingSchemeSummary, int tabCount) {
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);

		results.append(tabs + "CodingSchemeURI = " + codingSchemeSummary.getCodingSchemeURI() + "\n");
		results.append(tabs + "FormalName = " + codingSchemeSummary.getFormalName() + "\n");
		results.append(tabs + "LocalName = " + codingSchemeSummary.getLocalName() + "\n");
		results.append(tabs + "RepresentsVersion = " + codingSchemeSummary.getRepresentsVersion() + "\n");

		return results.toString();
	}

}
