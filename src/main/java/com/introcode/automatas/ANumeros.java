package com.introcode.automatas;

import com.introcode.entity.Alfabeto;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ANumeros extends AFD {

	public ANumeros() {
		Set<Integer> finales = Set.of(1,3);
		Map<Integer, Map<Character, Integer>> tabla = new HashMap<>();

		tabla.put(0, new HashMap<>());
		tabla.put(1, new HashMap<>());
		tabla.put(2, new HashMap<>());
		tabla.put(3, new HashMap<>());
		tabla.put(4, new HashMap<>());

		Map<Character, Integer> mapaS0 = tabla.get(0);
		Map<Character, Integer> mapaS1 = tabla.get(1);
		Map<Character, Integer> mapaS2 = tabla.get(2);
		Map<Character, Integer> mapaS3 = tabla.get(3);
		Map<Character, Integer> mapaS4 = tabla.get(3);

		mapaS0.put('-', 4);
		mapaS1.put('.', 2);


		for (char digit : Alfabeto.DIGITOS) {
			mapaS0.put(digit, 1);
			mapaS1.put(digit, 1);
			mapaS2.put(digit, 3);
			mapaS3.put(digit, 3);
			mapaS4.put(digit, 1);
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
			if (
				!transitions.containsKey(current) ||
				!transitions.get(current).containsKey(c)
			) {
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
