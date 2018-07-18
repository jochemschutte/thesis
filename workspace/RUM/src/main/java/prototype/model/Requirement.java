package prototype.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Requirement{
	
	public enum RequirementType {EQ, GTE}
	
	RequirementType type;
	
	public Requirement(RequirementType type) {
		this.type = type;
	}
	
	public boolean isValid(int offered, int consumed) {
		switch(type) {
		case EQ:
			return offered >= consumed;
		case GTE:
			return offered == consumed;
		}
		String types = String.join(", ", Arrays.stream(RequirementType.values()).map(Object::toString).collect(Collectors.toList()));
		throw new IllegalStateException(String.format("Unknown requirement type. Actual: '%s', expected: {%s}.", type.toString(), types));
	}
}