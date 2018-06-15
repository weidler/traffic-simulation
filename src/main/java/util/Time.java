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
	
	public static double secondsToNanoseconds(double seconds) {
		return seconds * 1000000000;
	}

	public static double nanosecondsToSeconds(double nanoseconds) {
		return nanoseconds / 1000000000;
	}

	public static double millisecondsToSeconds(long milliseconds) {
		return milliseconds / 1000000;
	}


	// REPRESENTATION
	
	public static String toSixtyMinuteFormat(double hours) {
		int h = (int) Math.floor(hours);
		int m = (int) Math.round((hours - Math.floor(hours)) * 60 * 100) / 100;
		
		String hours_string, minutes_string;
		hours_string = Integer.toString(h);
		minutes_string = Integer.toString(m);
		
		if (h < 10) hours_string = "0" + hours_string;
		if (m < 10) minutes_string = "0" + minutes_string;
		
		return hours_string + ":" + minutes_string; 
	}
}
