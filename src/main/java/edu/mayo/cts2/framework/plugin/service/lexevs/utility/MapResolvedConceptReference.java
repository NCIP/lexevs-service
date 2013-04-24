package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.lang.reflect.InvocationTargetException;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.apache.commons.beanutils.BeanUtils;

import edu.mayo.cts2.framework.plugin.service.lexevs.naming.NameVersionPair;

public class MapResolvedConceptReference extends ResolvedConceptReference {
	
	private static final long serialVersionUID = -5484934808068948895L;

	private ResolvedConceptReference resolvedConceptReference;
	
	private NameVersionPair mapName;
	
	public MapResolvedConceptReference(NameVersionPair mapName, ResolvedConceptReference resolvedConceptReference){
		super();
		this.mapName = mapName;
		try {
			BeanUtils.copyProperties(resolvedConceptReference, this);
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

	public ResolvedConceptReference getResolvedConceptReference() {
		return resolvedConceptReference;
	}

	public void setResolvedConceptReference(ResolvedConceptReference resolvedConceptReference) {
		this.resolvedConceptReference = resolvedConceptReference;
	}

}
