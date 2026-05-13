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
	private Lexema lexema;
	private Token token;
	private int id;
	private int row;
	private int column;

	public RegistroLexico(String lexema, int row, int column){
		this.lexema = new Lexema(lexema);
		this.row = row;
		this.column = column;
	}
}
