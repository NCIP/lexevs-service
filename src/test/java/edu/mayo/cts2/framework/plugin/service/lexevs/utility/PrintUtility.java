package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;

import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntry;
import edu.mayo.cts2.framework.model.codesystemversion.CodeSystemVersionCatalogEntrySummary;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;

public class PrintUtility {
	public static String createTabs(int tabCount){
		StringBuffer results = new StringBuffer();
		for(int i=0; i < tabCount; i++){
			appendLine(results, "", "\t");
		}
		return results.toString();
	}
	
	public static String createStringFromResolvedConceptReferenceIterator(ResolvedConceptReferencesIterator iterator) throws LBResourceUnavailableException, LBInvocationException{
		StringBuffer results = new StringBuffer();
	
		while(iterator.hasNext()){
			String objectData = createStringFromResolvedConceptReferenceObject(iterator.next(), 0); 
			if(objectData != null){
				appendLine(results, "", objectData + "\n=============\n\n");
			}
		}
		
		return results.toString();
	}
	
	public static void appendLine(StringBuffer results, String tabs, String value){
		results.append(tabs);
		results.append(value);
		results.append("\n");
	}
	
	public static String createStringFromResolvedConceptReferenceObject(ResolvedConceptReference reference, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		
		appendLine(results, tabs, " Code: " + reference.getCode());
		appendLine(results, tabs, " CodeNamespace: " + reference.getCodeNamespace());
		appendLine(results, tabs, " CodingSchemeName: " + reference.getCodingSchemeName());
		appendLine(results, tabs, " CodingSchemeURI: " + reference.getCodingSchemeURI());
		appendLine(results, tabs, " CodingSchemeVersion: " + reference.getCodingSchemeVersion());
		appendLine(results, tabs, " ConceptCode: " + reference.getConceptCode());

		appendLine(results, tabs, " EntityDescription: " + reference.getEntityDescription().getContent());
		appendLine(results, tabs, " Entities: \n");
		appendLine(results, "", createStringFromEntityObject(reference.getEntity(), tabCount + 1));
		appendLine(results, tabs, " SourceOf: " + reference.getSourceOf());
		appendLine(results, tabs, " TargetOf: " + reference.getTargetOf());
		
		return results.toString();
	}
	
	public static String createStringFromEntityObject(Entity entity, int tabCount){
		String tabs = createTabs(tabCount);
		StringBuffer results = new StringBuffer();

		if(entity == null){
			return "";
		}
		
		appendLine(results, tabs, " EntityCode = " + entity.getEntityCode());
		
		appendLine(results, tabs, " EntityDescription = " + entity.getEntityDescription().getContent());
		appendLine(results, tabs, " EntityCodeNamespace = " + entity.getEntityCodeNamespace());
		appendLine(results, tabs, " Owner = " + entity.getOwner());
		appendLine(results, tabs, " Status = " + entity.getStatus());

		appendLine(results, tabs, " EntityTypeCount = " + entity.getEntityTypeCount());
		appendLine(results, tabs, " EntityTypes:\n");
		appendLine(results, "", createStringFromEntityTypesInEntityObject(entity, tabCount + 1));
		
		appendLine(results, tabs, " CommentCount = " + entity.getCommentCount());
		appendLine(results, tabs, " Comments:\n");
		appendLine(results, "", createStringFromCommentsInEntityObject(entity, tabCount + 1));
		
		appendLine(results, tabs, " DefinitionCount = " + entity.getDefinitionCount());
		appendLine(results, tabs, " Definitions:\n");
		appendLine(results, "", createStringFromDefinitionsInEntityObject(entity, tabCount + 1));
		
		appendLine(results, tabs, " PresentationCount = " + entity.getPresentationCount());
		appendLine(results, tabs, " Presentations:\n");
		appendLine(results, "", createStringFromPresentationsInEntityObject(entity, tabCount + 1));
		
		appendLine(results, tabs, " PropertyCount = " + entity.getPropertyCount());
		appendLine(results, tabs, " Properties:\n");
		appendLine(results, "", createStringFromPropertiesInEntityObject(entity, tabCount + 1));
		
		appendLine(results, tabs, " PropertyLinkCount = " + entity.getPropertyLinkCount());
		appendLine(results, tabs, " PropertyLinks:\n");
		appendLine(results, "", createStringFromPropertyLinksInEntityObject(entity, tabCount + 1));
		
		
		return results.toString();
	}
	
	public static String createStringFromPresentationsInEntityObject(Entity entity, int tabCount){
		Presentation presentation;
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getPresentationCount();
		for(int i=0; i < count; i++){
			presentation = entity.getPresentation(i);
			appendLine(results, tabs, "Value = " + presentation.getValue().getContent());
			appendLine(results, tabs, "--DegreeOfFidelity = " + presentation.getDegreeOfFidelity());
			appendLine(results, tabs, "--Language = " + presentation.getLanguage());
			appendLine(results, tabs, "--Owner = " + presentation.getOwner());
			appendLine(results, tabs, "--PropertyID = " + presentation.getPropertyId());
			appendLine(results, tabs, "--PropertyName = " + presentation.getPropertyName());
			appendLine(results, tabs, "--PropertyType = " + presentation.getPropertyType());
			appendLine(results, tabs, "--RepresentationalForm = " + presentation.getRepresentationalForm());
			appendLine(results, tabs, "--Status = " + presentation.getStatus());
			
			appendLine(results, tabs, "--SourceCount = " + presentation.getSourceCount());
			appendLine(results, tabs, "--Sources:\n");
			appendLine(results, "", createStringFromSourceInPresentationObject(presentation, tabCount + 1));
			
			appendLine(results, tabs, "--PropertyQualifierCount = " + presentation.getPropertyQualifierCount());
			appendLine(results, tabs, "--PropertyQualifiers:\n");
			appendLine(results, "", createStringFromPropertyQualifiersInPresentationObject(presentation, tabCount + 1));
			
			appendLine(results, tabs, "--UsageContextCount = " + presentation.getUsageContextCount());
			appendLine(results, tabs, "--UsageContexts:\n");
			appendLine(results, "", createStringFromUsageContextsInPresentationObject(presentation, tabCount + 1));			
		}
		return results.toString();
	}
	
	public static String createStringFromSourceInPresentationObject(Presentation presentation, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = presentation.getSourceCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + presentation.getSource(i).getContent());
		}
		return results.toString();
	}
	
	public static String createStringFromPropertyQualifiersInPresentationObject(Presentation presentation, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = presentation.getPropertyQualifierCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + presentation.getPropertyQualifier(i));
		}
		return results.toString();
	}
	
	public static String createStringFromUsageContextsInPresentationObject(Presentation presentation, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = presentation.getUsageContextCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + presentation.getUsageContext(i));
		}
		return results.toString();
	}
	
	public static String createStringFromPropertiesInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getPropertyCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + entity.getProperty(i));
		}
		return results.toString();
	}
	
	public static String createStringFromPropertyLinksInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getPropertyLinkCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + entity.getPropertyLink(i));
		}
		return results.toString();
	}
	
	public static String createStringFromDefinitionsInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getDefinitionCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + entity.getDefinition(i));
		}
		return results.toString();
	}
	
	public static String createStringFromCommentsInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getCommentCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + entity.getComment(i));
		}
		return results.toString();
	}

	public static String createStringFromEntityTypesInEntityObject(Entity entity, int tabCount){
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);
		int count = entity.getEntityTypeCount();
		for(int i=0; i < count; i++){
			appendLine(results, tabs, " " + entity.getEntityType(i));
		}
		return results.toString();
	}

	public static String codingSchemeSummary(CodingSchemeSummary codingSchemeSummary, int tabCount) {
		StringBuffer results = new StringBuffer();
		String tabs = createTabs(tabCount);

		appendLine(results, tabs, "CodingSchemeURI = " + codingSchemeSummary.getCodingSchemeURI());
		appendLine(results, tabs, "FormalName = " + codingSchemeSummary.getFormalName());
		appendLine(results, tabs, "LocalName = " + codingSchemeSummary.getLocalName());
		appendLine(results, tabs, "RepresentsVersion = " + codingSchemeSummary.getRepresentsVersion());

		return results.toString();
	}

	/**
	 * @param dirResult
	 * @return
	 */
	public static String createStringFromDirectoryResultWithEntrySummary(
			DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult) {
		StringBuffer results = new StringBuffer();
		
		appendLine(results, "", "CodeSystemVersionCatalogEntrySummary Results: \n");

		List<CodeSystemVersionCatalogEntrySummary> entries = dirResult.getEntries();
		for(CodeSystemVersionCatalogEntrySummary summary : entries){
			appendLine(results, "", "About = " + summary.getAbout());
			appendLine(results, "", "CodeSystemVersionName = " + summary.getCodeSystemVersionName());
			appendLine(results, "", "DocumentURI = " + summary.getDocumentURI());
			appendLine(results, "", "FormalName = " + summary.getFormalName());
			appendLine(results, "", "HREF = " + summary.getHref());
			appendLine(results, "", "OfficialResourceVersionId = " + summary.getOfficialResourceVersionId());
			appendLine(results, "", "ResourceName = " + summary.getResourceName());
			appendLine(results, "", "MatchStrength = " + summary.getMatchStrength());
			appendLine(results, "", "OfficialReleaseDate = " + summary.getOfficialReleaseDate());
			appendLine(results, "", "CodeSystemVersionTagCount = " + summary.getCodeSystemVersionTagCount());
			for(int i=0; i < summary.getCodeSystemVersionTagCount(); i++){
				appendLine(results, "", "\tCodeSystemVersionTag = " + summary.getCodeSystemVersionTag(i));
			}
			appendLine(results, "", "ResourceSynopsis.Schema = " + summary.getResourceSynopsis().getSchema());
			appendLine(results, "", "VersionOf.Uri = " + summary.getVersionOf().getUri() + "\n\n");
		}
		
		return results.toString();
	}

	/**
	 * @param dirResult
	 * @return
	 */
	public static String createStringFromDirectoryResultWithEntry(
			DirectoryResult<CodeSystemVersionCatalogEntry> dirResult) {
		StringBuffer results = new StringBuffer();
		
		appendLine(results, "", "CodeSystemVersionCatalogEntry Results: \n");

		List<CodeSystemVersionCatalogEntry> entries = dirResult.getEntries();
		for(CodeSystemVersionCatalogEntry summary : entries){
			appendLine(results, "", "About = " + summary.getAbout());
			appendLine(results, "", "Associations = " + summary.getAssociations());
			appendLine(results, "", "CodeSystemVersionName = " + summary.getCodeSystemVersionName());
			appendLine(results, "", "DocumentURI = " + summary.getDocumentURI());
			appendLine(results, "", "EntityDescriptions = " + summary.getEntityDescriptions());
			appendLine(results, "", "FormalName = " + summary.getFormalName());

			appendLine(results, "", "Individuals = " + summary.getIndividuals());
			appendLine(results, "", "OfficialResourceVersionId = " + summary.getOfficialResourceVersionId());
			appendLine(results, "", "Roles = " + summary.getRoles());
			appendLine(results, "", "SourceStatements = " + summary.getSourceStatements());
			appendLine(results, "", "DefaultLanguage = " + summary.getDefaultLanguage());
			appendLine(results, "", "EntryState = " + summary.getEntryState().value());
			appendLine(results, "", "State = " + summary.getState());
			appendLine(results, "", "OfficialReleaseDate = " + summary.getOfficialReleaseDate());
			appendLine(results, "", "NoteCount = " + summary.getNoteCount());
			appendLine(results, "", "AdditionalDocumentationCount = " + summary.getAdditionalDocumentationCount());
			appendLine(results, "", "AlternateIDCount = " + summary.getAlternateIDCount());
			appendLine(results, "", "ImportsCount = " + summary.getImportsCount());
			appendLine(results, "", "KeywordCount = " + summary.getKeywordCount());
			for(int i=0; i < summary.getKeywordCount(); i++){
				appendLine(results, "", "\tKeyword = " + summary.getKeyword(i));
			}
			appendLine(results, "", "PropertyCount = " + summary.getPropertyCount());
			for(int i=0; i < summary.getPropertyCount(); i++){
				appendLine(results, "", "\tProperty = " + summary.getProperty(i));
			}
			appendLine(results, "", "ResourceTypeCount = " + summary.getResourceTypeCount());
			appendLine(results, "", "SourceAndRoleCount = " + summary.getSourceAndRoleCount());			
			appendLine(results, "", "ResourceSynopsis.Schema = " + summary.getResourceSynopsis().getSchema());
			appendLine(results, "", "VersionOf.Uri = " + summary.getVersionOf().getUri() + "\n\n");
		}
		
		return results.toString();
	}

	/**
	 * @param csRenderingPage
	 */
	public static String createStringFromCodingSchemeRendering(
			CodingSchemeRendering[] csRenderingPage) {
		StringBuffer results = new StringBuffer();
		
		appendLine(results, "", "CodeSystemVersionCatalogEntry Results: \n");
		
		return results.toString();
	}

}
