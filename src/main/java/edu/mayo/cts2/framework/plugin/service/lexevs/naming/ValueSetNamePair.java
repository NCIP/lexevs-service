/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public class ValueSetNamePair {
	private String valueSetName;
	private String definitionLocalId;

	protected ValueSetNamePair(String valueSetName, String definitionLocalId) {
		super();
		this.valueSetName = valueSetName;
		this.definitionLocalId = definitionLocalId;
	}

	public String getValueSetName() {
		return valueSetName;
	}

	public String getDefinitionLocalId() {
		return definitionLocalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((definitionLocalId == null) ? 0 : definitionLocalId
						.hashCode());
		result = prime * result
				+ ((valueSetName == null) ? 0 : valueSetName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueSetNamePair other = (ValueSetNamePair) obj;
		if (definitionLocalId == null) {
			if (other.definitionLocalId != null)
				return false;
		} else if (!definitionLocalId.equals(other.definitionLocalId))
			return false;
		if (valueSetName == null) {
			if (other.valueSetName != null)
				return false;
		} else if (!valueSetName.equals(other.valueSetName))
			return false;
		return true;
	}	
}