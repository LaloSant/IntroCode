package com.introcode.automatas;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AFD {
	protected final int initialState;
	protected final Set<Integer> finalStates;

	//Estado, mapa de Transicion
	protected final Map<Integer, Map<Character, Integer>> transitions;

	public AFD(int initialState, Set<Integer> finalStates,
			Map<Integer, Map<Character, Integer>> transitions) {
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.transitions = transitions;
	}

	/**
	 * Simula la cadena y devuelve true si es aceptada.
	 */
	public boolean simulate(String input) {
		int current = initialState;

		for (char c : input.toCharArray()) {
			// Validar que exista transición
			if (!transitions.containsKey(current) ||
					!transitions.get(current).containsKey(c)) {
				return false;
			}
			int next = transitions.get(current).get(c);
			current = next;
		}

		if (finalStates.contains(current)) {
			return true;
		} else {
			return false;
		}
	}

}
