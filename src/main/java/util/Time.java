package util;

public class Time {

	public static double secondsToHours(double seconds) {
		return (secondsToMinutes(seconds) / 60);
	}

	public static double secondsToMinutes(double seconds) {
		return (seconds / 60);
	}

	public static double hoursToMinutes(double hours) {
		return (hours * 60);
	}

	public static double hoursToSeconds(double hours) {
		return (hoursToMinutes(hours) * 60);
	}
}
