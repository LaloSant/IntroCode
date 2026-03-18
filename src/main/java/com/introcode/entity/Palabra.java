package com.introcode.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public abstract class Palabra {

	@Getter
	@Setter
	private String valor;

}
