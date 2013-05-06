package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.apache.commons.beanutils.BeanUtils;

import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;

public class MapResolvedConceptReference extends ResolvedConceptReference {
	
	private static final long serialVersionUID = -5484934808068948895L;

	private NameVersionPair mapName;
	
	public MapResolvedConceptReference(NameVersionPair mapName, ResolvedConceptReference resolvedConceptReference){
		super();
		this.mapName = mapName;
		try {
			BeanUtils.copyProperties(this, resolvedConceptReference);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public NameVersionPair getMapName() {
		return mapName;
	}

	public void setMapName(NameVersionPair mapName) {
		this.mapName = mapName;
	}

}
