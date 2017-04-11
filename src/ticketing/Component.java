package ticketing;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Component {
	private String componentName;
	private Set<Component> subComponents = new HashSet<>();

	public Component(String componentName) {
		super();
		this.componentName = componentName;
	}

	public String getComponentName() {
		return componentName;
	}

	public Set<Component> getSubComponents() {
		return subComponents;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return componentName;
	}
	
	
	

}
