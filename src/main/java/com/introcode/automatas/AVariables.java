package com.introcode.automatas;

import com.introcode.entity.Alfabeto;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AVariables extends AFD {

	public AVariables() {
		Set<Integer> finales = Set.of(1);
		Map<Integer, Map<Character, Integer>> tabla = new HashMap<>();

		tabla.put(0, new HashMap<>());
		tabla.put(1, new HashMap<>());

		Map<Character, Integer> mapaS0 = tabla.get(0);
		Map<Character, Integer> mapaS1 = tabla.get(1);

		for (Character c : Alfabeto.LETRAS) {
			mapaS0.put(c, 1);
			mapaS1.put(c, 1);
		}
		mapaS0.put('_', 1);
		mapaS1.put('_', 1);

		for(Character c : Alfabeto.DIGITOS){
			mapaS1.put(c, 1);
		}
		super(0, finales, tabla);
	}
}
