package com.introcode.automatas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ACadenas extends AFD {
	public ACadenas() {
		Character[] alfabeto = {
				// dígitos
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				// letras mayúsculas
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
				// letras minúsculas
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
				'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
				// símbolos
				'+', '-', '*', '/', '=', '^', '>', '<', '!', '(', ')', '\"',
				',', ' ', ';', '#', '.', '\n', '\t'
		};
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
