/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.uri;

import java.util.Set;

/**
 * Service Interface for resolvinig identifiers to URIs.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface UriResolver {

	/**
	 * The Enum IdType.
	 *
	 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
	 */
	public enum IdType {
		CODE_SYSTEM, VALUE_SET, CODE_SYSTEM_VERSION
	}

	/**
	 * Id to uri.
	 *
	 * @param id the id
	 * @param idType the id type
	 * @return the string
	 */
	public String idToUri(String id, IdType idType);
	
	public Set<String> idToIds(String id);

	/**
	 * Id to name.
	 *
	 * @param id the id
	 * @param idType the id type
	 * @return the string
	 */
	public String idToName(String id, IdType idType);

	/**
	 * Id to base uri.
	 *
	 * @param id the id
	 * @return the string
	 */
	public String idToBaseUri(String id);
	
	/**
	 * Id and version to version uri.
	 *
	 * @param id the id
	 * @param versionId the version id
	 * @param itType the it type
	 * @return the string
	 */
	public String idAndVersionToVersionUri(String id, String versionId,
			IdType itType);

	/**
	 * Id and version to version name.
	 *
	 * @param id the id
	 * @param versionId the version id
	 * @param itType the it type
	 * @return the string
	 */
	public String idAndVersionToVersionName(String id, String versionId,
			IdType itType);

}