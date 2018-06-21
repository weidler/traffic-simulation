package type;

public enum Strategy {
	BASIC_CYCLING,
	PRIORITY_CYCLING,
	COORDINATED,
	INFORMED_CYCLING,
	WEIGHTED_CYCLING;

	public static Strategy stringToType(String s) {
		switch (s) {
			case "BASIC_CYCLING":
				return BASIC_CYCLING;
			case "PRIORITY_CYCLING":
				return PRIORITY_CYCLING;
			case "COORDINATED":
				return COORDINATED;
			case "INFORMED_CYCLING":
				return INFORMED_CYCLING;
			case "WEIGHTED_CYCLING":
				return WEIGHTED_CYCLING;
			default:
				return BASIC_CYCLING;
		}
	}
}
