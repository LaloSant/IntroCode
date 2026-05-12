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
				System.out.printf("[Rechazo] No hay transición desde q%d con '%c'%n", current, c);
				return false;
			}
			int next = transitions.get(current).get(c);
			System.out.printf("q%d --%c--> q%d%n", current, c, next);
			current = next;
		}

		if (finalStates.contains(current)) {
			System.out.printf("[Aceptación] Estado q%d es final.%n", current);
			return true;
		} else {
			System.out.printf("[Rechazo] Estado q%d no es final.%n", current);
			return false;
		}
	}

}
