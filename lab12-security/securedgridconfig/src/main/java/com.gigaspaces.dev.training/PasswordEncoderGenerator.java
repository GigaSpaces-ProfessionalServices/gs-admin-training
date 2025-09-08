package com.gigaspaces.dev.training;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {

	public static void main(String[] args) {

		System.out.println("This is a utility program that will create an encoded password.");
		System.out.println("It takes as an argument a string to be encoded.");
		System.out.println("It will print the encoded password.");
		System.out.println();

		String password = args[0];
		int i = 0;
		while (i < 10) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);

			System.out.println(hashedPassword);
			i++;
		}
	}
}