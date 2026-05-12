package com.introcode.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroLexico {
	private Palabra lexema;
	private Token token;
	private int id;
	private int row;
	private int column;

	public RegistroLexico(int row, int column){
		this.row = row;
		this.column = column;
	}
}
