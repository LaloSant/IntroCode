package com.introcode.automatas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ANumeros extends AFD {
	public ANumeros() {
		char[] numeros = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		Set<Integer> finales = new HashSet<>(Arrays.stream(new int[] { 1, 3 }).boxed().toList());
		Map<Integer, Map<Character, Integer>> tabla = new HashMap<>();

		tabla.put(0, new HashMap<>());
		tabla.put(1, new HashMap<>());
		tabla.put(2, new HashMap<>());
		tabla.put(3, new HashMap<>());

		Map<Character, Integer> mapaS0 = tabla.get(0);

		Map<Character, Integer> mapaS1 = tabla.get(1);
		tabla.get(1).put('.', 2);

		Map<Character, Integer> mapaS2 = tabla.get(2);

		Map<Character, Integer> mapaS3 = tabla.get(3);
		for (char c : numeros) {
			mapaS0.put(c, 1);
			mapaS1.put(c, 1);
			mapaS2.put(c, 3);
			mapaS3.put(c, 3);
		}
		super(0, finales, tabla);
	}

	/**
	 * 
	 * @param input
	 * @param flag
	 * @return -1 si no es valido, 1 si es entero y 3 si es real
	 */
	public int simulate(String input, boolean flag) {
		int current = this.getInitialState();

		for (char c : input.toCharArray()) {
			// Validar que exista transición
			if (!transitions.containsKey(current) ||
					!transitions.get(current).containsKey(c)) {
				return -1;
			}
			int next = transitions.get(current).get(c);
			current = next;
		}

		if (finalStates.contains(current)) {
			return current;
		} else {
			return -1;
		}
	}
}
