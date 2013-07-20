package edu.mayo.cts2.framework.plugin.service.lexevs.naming;

public class ResolvedValueSetNameTriple extends ValueSetNamePair {
	
	private String resolutionLocalId;

	public ResolvedValueSetNameTriple(String valueSetName, String definitionLocalId, String resolutionLocalId) {
		super(valueSetName, definitionLocalId);
		this.resolutionLocalId = resolutionLocalId;
	}

	public String getResolutionLocalId() {
		return resolutionLocalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((resolutionLocalId == null) ? 0 : resolutionLocalId
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResolvedValueSetNameTriple other = (ResolvedValueSetNameTriple) obj;
		if (resolutionLocalId == null) {
			if (other.resolutionLocalId != null)
				return false;
		} else if (!resolutionLocalId.equals(other.resolutionLocalId))
			return false;
		return true;
	}
		
}