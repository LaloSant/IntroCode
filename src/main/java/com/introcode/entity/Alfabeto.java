package com.introcode.entity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Alfabeto {

	public static final Set<Character> DIGITOS = new LinkedHashSet<>(
		List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
	);
	public static final Set<Character> LETRAS = new LinkedHashSet<>(
		List.of(
			'A',
			'B',
			'C',
			'D',
			'E',
			'F',
			'G',
			'H',
			'I',
			'J',
			'K',
			'L',
			'M',
			'N',
			'O',
			'P',
			'Q',
			'R',
			'S',
			'T',
			'U',
			'V',
			'W',
			'X',
			'Y',
			'Z',
			// MINUSCULAS
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'o',
			'p',
			'q',
			'r',
			's',
			't',
			'u',
			'v',
			'w',
			'x',
			'y',
			'z'
		)
	);
	public static final Set<Character> SIMBOLOS = new LinkedHashSet<>(
		List.of(
			'+',
			'-',
			'*',
			'/',
			'%',
			'^',
			'=',
			'>',
			'<',
			'~',
			'(',
			')',
			'{',
			'}',
			'[',
			']',
			'#',
			'\"',
			'\'',
			',',
			'.',
			';',
			':',
			' ',
			'\n',
			'\t',
			'\r',
			'_'
		)
	);

	static {
		Set<Character> temp = new LinkedHashSet<>();
		temp.addAll(DIGITOS);
		temp.addAll(LETRAS);
		temp.addAll(SIMBOLOS);
		ALFABETO = Set.copyOf(temp);
	}

	public static final Set<Character> ALFABETO;

	public static final Set<String> PALABRAS_RESERVADAS = new LinkedHashSet<>(
		List.of(
			// DECLARACION DE VARIABLES
			"local",
			"true",
			"false",
			// CONDICIONALES
			"if",
			"then",
			"else",
			"elseif",
			"end",
			// CICLOS
			"while",
			"do",
			"for",
			"repeat",
			"until",
			// OPERADORES LOGICOS
			"and",
			"or",
			"not",
			"nil"
		)
	);

	public static final Set<String> OPERADORES_ARITMETICOS =
		new LinkedHashSet<>(List.of("+", "-", "*", "/", "^", "%"));

	public static final Set<String> OPERADORES_RELACIONALES =
		new LinkedHashSet<>(List.of("==", "~=", "<", ">", "<=", ">="));

	public static final Set<String> OPERADORES_LOGICOS = new LinkedHashSet<>(
		List.of("and", "or", "not")
	);

	public static final String OPERADOR_ASIGNACION = "=";

	public static final Set<String> SEPARADORES = new LinkedHashSet<>(
		List.of("(", ")", "{", "}", "[", "]", ",", ";", "\"", "\'")
	);
}
