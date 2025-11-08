package com.testpaper.demo.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class IdGenerator {
	
	private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz0123456789";
	private static final Random random = new Random();
	
	public static String generateRandomId(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(ALPHANUMERIC.length());
			sb.append(ALPHANUMERIC.charAt(index));
		}
		return sb.toString();
	}
	
	public static String generateHashBasedId(String input) {
		return Hashing.murmur3_128().hashString(input, StandardCharsets.UTF_8).toString();
	}
}

