package com.introcode.entity;

import java.util.concurrent.Flow.Publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroLexico {
	public static int consecutivo = 0;

	private Lexema lexema;
	private TokenLexico token;
	private int id;
	private int row;
	private int column;
	private int consecutivoID;

	public RegistroLexico(String lexema, int row, int column) {
		this.lexema = new Lexema(lexema);
		this.row = row;
		this.column = column;
		this.consecutivoID = consecutivo++;
	}

	@Override
	public String toString(){
		return String.format("%s,%s,%s,%s", this.lexema, this.row, this.column, this.getToken());
	}
}
