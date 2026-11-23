package com.introcode.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Lexema {

	private String valor;

	@Override
	public String toString() {
		return this.valor;
	}
}
