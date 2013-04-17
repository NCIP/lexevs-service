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

	/**
	 * @param dirResult
	 * @return
	 */
	public static String createStringFromDirectoryResultWithEntrySummary(
			DirectoryResult<CodeSystemVersionCatalogEntrySummary> dirResult) {
		StringBuffer results = new StringBuffer();
		
		results.append("CodeSystemVersionCatalogEntrySummary Results: \n");

		List<CodeSystemVersionCatalogEntrySummary> entries = dirResult.getEntries();
		for(CodeSystemVersionCatalogEntrySummary summary : entries){
			results.append("About = " + summary.getAbout() + "\n");
			results.append("CodeSystemVersionName = " + summary.getCodeSystemVersionName() + "\n");
			results.append("DocumentURI = " + summary.getDocumentURI() + "\n");
			results.append("FormalName = " + summary.getFormalName() + "\n");
			results.append("HREF = " + summary.getHref() + "\n");
			results.append("OfficialResourceVersionId = " + summary.getOfficialResourceVersionId() + "\n");
			results.append("ResourceName = " + summary.getResourceName() + "\n");
			results.append("MatchStrength = " + summary.getMatchStrength() + "\n");
			results.append("OfficialReleaseDate = " + summary.getOfficialReleaseDate() + "\n");
			results.append("CodeSystemVersionTagCount = " + summary.getCodeSystemVersionTagCount() + "\n");
			for(int i=0; i < summary.getCodeSystemVersionTagCount(); i++){
				results.append("\tCodeSystemVersionTag = " + summary.getCodeSystemVersionTag(i) + "\n");
			}
			results.append("ResourceSynopsis.Schema = " + summary.getResourceSynopsis().getSchema() + "\n");
			results.append("VersionOf.Uri = " + summary.getVersionOf().getUri() + "\n\n");
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
		
		results.append("CodeSystemVersionCatalogEntry Results: \n");

		List<CodeSystemVersionCatalogEntry> entries = dirResult.getEntries();
		for(CodeSystemVersionCatalogEntry summary : entries){
			results.append("About = " + summary.getAbout() + "\n");
			results.append("Associations = " + summary.getAssociations() + "\n");
			results.append("CodeSystemVersionName = " + summary.getCodeSystemVersionName() + "\n");
			results.append("DocumentURI = " + summary.getDocumentURI() + "\n");
			results.append("EntityDescriptions = " + summary.getEntityDescriptions() + "\n");
			results.append("FormalName = " + summary.getFormalName() + "\n");

			results.append("Individuals = " + summary.getIndividuals() + "\n");
			results.append("OfficialResourceVersionId = " + summary.getOfficialResourceVersionId() + "\n");
			results.append("Roles = " + summary.getRoles() + "\n");
			results.append("SourceStatements = " + summary.getSourceStatements() + "\n");
			results.append("DefaultLanguage = " + summary.getDefaultLanguage() + "\n");
			results.append("EntryState = " + summary.getEntryState().value() + "\n");
			results.append("State = " + summary.getState() + "\n");
			results.append("OfficialReleaseDate = " + summary.getOfficialReleaseDate() + "\n");
			results.append("NoteCount = " + summary.getNoteCount() + "\n");
			results.append("AdditionalDocumentationCount = " + summary.getAdditionalDocumentationCount() + "\n");
			results.append("AlternateIDCount = " + summary.getAlternateIDCount() + "\n");
			results.append("ImportsCount = " + summary.getImportsCount() + "\n");
			results.append("KeywordCount = " + summary.getKeywordCount() + "\n");
			for(int i=0; i < summary.getKeywordCount(); i++){
				results.append("\tKeyword = " + summary.getKeyword(i) + "\n");
			}
			results.append("PropertyCount = " + summary.getPropertyCount() + "\n");
			for(int i=0; i < summary.getPropertyCount(); i++){
				results.append("\tProperty = " + summary.getProperty(i) + "\n");
			}
			results.append("ResourceTypeCount = " + summary.getResourceTypeCount() + "\n");
			results.append("SourceAndRoleCount = " + summary.getSourceAndRoleCount() + "\n");			
			results.append("ResourceSynopsis.Schema = " + summary.getResourceSynopsis().getSchema() + "\n");
			results.append("VersionOf.Uri = " + summary.getVersionOf().getUri() + "\n\n");
		}
		
		return results.toString();
	}

	/**
	 * @param csRenderingPage
	 */
	public static String createStringFromCodingSchemeRendering(
			CodingSchemeRendering[] csRenderingPage) {
		StringBuffer results = new StringBuffer();
		
		results.append("CodeSystemVersionCatalogEntry Results: \n");
		
		return results.toString();
	}

}
