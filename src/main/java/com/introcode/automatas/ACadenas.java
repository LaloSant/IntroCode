package com.introcode.automatas;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.introcode.entity.Alfabeto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ACadenas extends AFD {
	public ACadenas() {
		Set<Integer> finales = Set.of(2);//new HashSet<>(Arrays.stream(new int[] { 2 }).boxed().toList());
		Map<Integer, Map<Character, Integer>> tabla = new HashMap<>();

		tabla.put(0, new HashMap<>());
		tabla.put(1, new HashMap<>());
		tabla.put(2, new HashMap<>());
		tabla.put(3, new HashMap<>());
		tabla.put(4, new HashMap<>());
		tabla.put(5, new HashMap<>());
		tabla.put(6, new HashMap<>());

		Map<Character, Integer> mapaS1 = tabla.get(1);
		Map<Character, Integer> mapaS3 = tabla.get(3);
		Map<Character, Integer> mapaS5 = tabla.get(5);

		tabla.get(0).put('\"', 1);
		tabla.get(1).put('\"', 2);

		tabla.get(0).put('\'', 3);
		tabla.get(3).put('\'', 2);

		tabla.get(0).put('[', 4);
		tabla.get(4).put('[', 5);
		tabla.get(5).put(']', 6);
		tabla.get(6).put(']', 2);

		for (Character c : Alfabeto.ALFABETO) {
			if (c == '\"' || c == '\'') {
				continue;
			}
			mapaS1.put(c, 1);
			mapaS3.put(c, 3);
			if (c == '[' || c == ']') {
				continue;
			}
			mapaS5.put(c, 5);
		}
		super(0, finales, tabla);
	}

}
