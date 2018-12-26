package br.com.katiu.tela;

import br.com.katiu.jframe.TelaPrincipal;

public class TelaInicial {
	
	private Integer opcao = 0;

	public static void main(String[] args) throws Exception {
		TelaPrincipal telaPrincipal = new TelaPrincipal();

		while(telaPrincipal.getOpcao() == 0) {
			Thread.sleep(200);
		}
		System.gc();

	}

	public Integer getOpcao() {
		return opcao;
	}

	public void setOpcao(Integer opcao) {
		this.opcao = opcao;
	}
	
	

}
