package com.introcode.automatas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ACadenas extends AFD {
	public ACadenas(Character[] alfabeto) {
		Set<Integer> finales = new HashSet<>(Arrays.stream(new int[] { 2 }).boxed().toList());
		Map<Integer, Map<Character, Integer>> tabla = new HashMap<>();

		tabla.put(0, new HashMap<>());
		tabla.put(1, new HashMap<>());
		tabla.put(2, new HashMap<>());

		Map<Character, Integer> mapaS1 = tabla.get(1);

		tabla.get(0).put('\"', 1);
		tabla.get(1).put('\"', 2);

		for (Character c : alfabeto) {
			if (c == '\"') {
				continue;
			}
			mapaS1.put(c, 1);
		}
		super(0, finales, tabla);
	}

}
