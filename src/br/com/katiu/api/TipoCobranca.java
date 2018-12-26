package br.com.katiu.api;

import java.io.BufferedReader;
import java.io.IOException;

import br.com.katiu.model.Utils;

public interface TipoCobranca {
	
	public String nomeDoArquivo();
	
	public void start(BufferedReader br, Utils utils) throws IOException;

	public int getCountIgnoredLines();

	public void setCountIgnoredLines(int i);
	
}
