package com.introcode.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public abstract class Palabra {

	@Getter
	@Setter
	private String valor;

	@Override
	public String toString() {
		return this.valor;
	}

}
