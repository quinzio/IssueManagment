package ticketing;

import java.util.Set;
import java.util.TreeSet;

public class Component {
	private String componentName;
	private Set<Component> subComponents = new TreeSet<>();

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
	
	
	

}
