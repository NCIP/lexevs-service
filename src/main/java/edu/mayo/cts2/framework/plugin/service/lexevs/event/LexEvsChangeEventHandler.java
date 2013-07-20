package edu.mayo.cts2.framework.plugin.service.lexevs.event;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LexEvsChangeEventHandler {

	private Set<LexEvsChangeEventObserver> observers = new HashSet<LexEvsChangeEventObserver>();
	
	@Autowired(required = false)
	public void register(Set<LexEvsChangeEventObserver> observers){
		this.observers.addAll(observers);
	}
	
	public void register(LexEvsChangeEventObserver observer){
		this.observers.add(observer);
	}
	
	public void unregister(LexEvsChangeEventObserver observer){
		this.observers.remove(observer);
	}
	
	public void fireChange(){
		for(LexEvsChangeEventObserver observer : this.observers){
			observer.onChange();
		}
	}
}
