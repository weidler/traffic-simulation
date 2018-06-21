package type;

public enum Distribution {
	GAUSSIAN, POISSON, EMPIRICAL;

	public static Distribution stringToType(String s) {
		switch (s) {
			case "GAUSSIAN":
				return GAUSSIAN;
			case "POISSON":
				return POISSON;
			case "EMPIRICAL":
				return EMPIRICAL;
			default:
				return EMPIRICAL;
		}
	}
}
