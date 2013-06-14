/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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