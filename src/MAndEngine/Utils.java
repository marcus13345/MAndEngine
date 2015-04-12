package MAndEngine;

public class Utils {
	public static int rand(int low, int high) {
		return (int)(Math.random() * (high - low + 1) + low);
	}
}
