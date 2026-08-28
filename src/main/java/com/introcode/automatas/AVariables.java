package com.introcode.automatas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AVariables extends AFD {

	public AVariables() {
		char[] numeros = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		Set<Integer> finales = new HashSet<>(Arrays.stream(new int[] { 1, 3 }).boxed().toList());
		Map<Integer, Map<Character, Integer>> tabla = new HashMap<>();

		tabla.put(0, new HashMap<>());
		tabla.put(1, new HashMap<>());

		Map<Character, Integer> mapaS0 = tabla.get(0);
		Map<Character, Integer> mapaS1 = tabla.get(1);

		for (int i = 'a'; i <= 'z'; i++) {
			char c = (char) i;
			char cUpper = Character.toUpperCase(c);
			mapaS0.put(c, 1);
			mapaS0.put(cUpper, 1);
			mapaS1.put(c, 1);
			mapaS1.put(cUpper, 1);
		}

		for (char c : numeros) {
			mapaS1.put(c, 1);
		}

		super(0, finales, tabla);

	}

}
