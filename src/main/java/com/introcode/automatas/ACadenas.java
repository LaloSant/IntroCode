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

		Map<Character, Integer> mapaS1 = tabla.get(1);

		tabla.get(0).put('\"', 1);
		tabla.get(1).put('\"', 2);

		for (Character c : Alfabeto.ALFABETO) {
			if (c == '\"') {
				continue;
			}
			mapaS1.put(c, 1);
		}
		super(0, finales, tabla);
	}

}
