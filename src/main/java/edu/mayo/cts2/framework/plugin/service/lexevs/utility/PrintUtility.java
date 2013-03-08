package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;

public class PrintUtility {
	public static String createTabs(int tabCount){
		String results = "";
		for(int i=0; i < tabCount; i++){
			results += "\t";
		}
		return results;
	}
	
	public static String resolvedConceptReference_toString(ResolvedConceptReference reference, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		
		results += tabs + " Code: " + reference.getCode() + "\n";
		results += tabs + " CodeNamespace: " + reference.getCodeNamespace() + "\n";
		results += tabs + " CodingSchemeName: " + reference.getCodingSchemeName() + "\n";
		results += tabs + " CodingSchemeURI: " + reference.getCodingSchemeURI() + "\n";
		results += tabs + " CodingSchemeVersion: " + reference.getCodingSchemeVersion() + "\n";
		results += tabs + " ConceptCode: " + reference.getConceptCode() + "\n";

		results += tabs + " EntityDescription: " + reference.getEntityDescription().getContent() + "\n";
		results += tabs + " Entities: \n";
		results += entity_toString(reference.getEntity(), tabCount + 1) + "\n";
		results += tabs + " SourceOf: " + reference.getSourceOf() + "\n";
		results += tabs + " TargetOf: " + reference.getTargetOf() + "\n";
		
		return results;
	}
	
	public static String entity_toString(Entity entity, int tabCount){
		String tabs = createTabs(tabCount);
		String results = tabs + " EntityCode = " + entity.getEntityCode() + "\n";
		
		results += tabs + " EntityDescription = " + entity.getEntityDescription().getContent() + "\n";;
		results += tabs + " EntityCodeNamespace = " + entity.getEntityCodeNamespace() + "\n";;
		results += tabs + " Owner = " + entity.getOwner() + "\n";
		results += tabs + " Status = " + entity.getStatus() + "\n";

		results += tabs + " EntityTypeCount = " + entity.getEntityTypeCount() + "\n";
		results += tabs + " EntityTypes:\n ";
		results += entityTypes_toString(entity, tabCount + 1);
		
		results += tabs + " CommentCount = " + entity.getCommentCount() + "\n";
		results += tabs + " Comments:\n";
		results += comments_toString(entity, tabCount + 1);
		
		results += tabs + " DefinitionCount = " + entity.getDefinitionCount() + "\n";
		results += tabs + " Definitions:\n";
		results += definitions_toString(entity, tabCount + 1);
		
		results += tabs + " PresentationCount = " + entity.getPresentationCount() + "\n";
		results += tabs + " Presentations:\n";
		results += presentations_toString(entity, tabCount + 1);
		
		results += tabs + " PropertyCount = " + entity.getPropertyCount() + "\n";
		results += tabs + " Properties:\n";
		results += properties_toString(entity, tabCount + 1);
		
		results += tabs + " PropertyLinkCount = " + entity.getPropertyLinkCount() + "\n";
		results += tabs + " PropertyLinks:\n";
		results += propertyLinks_toString(entity, tabCount + 1);
		
		
		return results;
	}
	
	public static String presentations_toString(Entity entity, int tabCount){
		Presentation presentation;
		String results = "";
		String tabs = createTabs(tabCount);
		int count = entity.getPresentationCount();
		for(int i=0; i < count; i++){
			presentation = entity.getPresentation(i);
			results += tabs + " Value = " + presentation.getValue().getContent() + ", ";
			results += "DegreeOfFidelity = " + presentation.getDegreeOfFidelity() + ", ";
			results += "Language = " + presentation.getLanguage() + ", ";
			results += "Owner = " + presentation.getOwner() + ", ";
			results += "PropertyID = " + presentation.getPropertyId() + ", ";
			results += "PropertyName = " + presentation.getPropertyName() + ", ";
			results += "PropertyType = " + presentation.getPropertyType() + ", ";
			results += "RepresentationalForm = " + presentation.getRepresentationalForm() + ", ";
			results += "Status = " + presentation.getStatus() + "\n";
			
			results += tabs + " SourceCount = " + presentation.getSourceCount() + "\n";
			results += tabs + " Sources:\n";
			results += source_toString(presentation, tabCount + 1);
			
			results += tabs + " PropertyQualifierCount = " + presentation.getPropertyQualifierCount() + "\n";
			results += tabs + " PropertyQualifiers:\n";
			results += propertyQualifiers_toString(presentation, tabCount + 1);
			
			results += tabs + " UsageContextCount = " + presentation.getUsageContextCount() + "\n";
			results += tabs + " UsageContexts:\n";
			results += usageContexts_toString(presentation, tabCount + 1);			
		}
		return results;
	}
	
	public static String source_toString(Presentation presentation, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = presentation.getSourceCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + presentation.getSource(i).getContent() + "\n";
		}
		return results;
	}
	
	public static String propertyQualifiers_toString(Presentation presentation, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = presentation.getPropertyQualifierCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + presentation.getPropertyQualifier(i) + "\n";
		}
		return results;
	}
	
	public static String usageContexts_toString(Presentation presentation, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = presentation.getUsageContextCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + presentation.getUsageContext(i) + "\n";
		}
		return results;
	}
	
	public static String properties_toString(Entity entity, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = entity.getPropertyCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + entity.getProperty(i) + "\n";
		}
		return results;
	}
	
	public static String propertyLinks_toString(Entity entity, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = entity.getPropertyLinkCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + entity.getPropertyLink(i) + "\n";
		}
		return results;
	}
	
	public static String definitions_toString(Entity entity, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = entity.getDefinitionCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + entity.getDefinition(i) + "\n";
		}
		return results;
	}
	
	public static String comments_toString(Entity entity, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = entity.getCommentCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + entity.getComment(i) + "\n";
		}
		return results;
	}

	public static String entityTypes_toString(Entity entity, int tabCount){
		String results = "";
		String tabs = createTabs(tabCount);
		int count = entity.getEntityTypeCount();
		for(int i=0; i < count; i++){
			results += tabs + " " + entity.getEntityType(i) + "\n";
		}
		return results;
	}

}
