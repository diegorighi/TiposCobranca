package br.com.katiu.model;

public class OnCorretorConsulta {

	private final String tipoLanctoComl = "3";
	private final String trioContabil = "7";
	private final String tipoHistComissao = "67";
	
	private String susep = null;
	private String primeiroDiaDoMes = null;
	private String ultimoDiaDoMes = null;
	
	public OnCorretorConsulta(String susep, String primeiroDiaDoMes, String ultimoDiaDoMes) {
		this.susep = susep;
		this.primeiroDiaDoMes = primeiroDiaDoMes;
		this.ultimoDiaDoMes = ultimoDiaDoMes;
	}

	public String getSusep() {
		return susep;
	}

	public void setSusep(String susep) {
		this.susep = susep;
	}

	public String getPrimeiroDiaDoMes() {
		return primeiroDiaDoMes;
	}

	public void setPrimeiroDiaDoMes(String primeiroDiaDoMes) {
		this.primeiroDiaDoMes = primeiroDiaDoMes;
	}

	public String getUltimoDiaDoMes() {
		return ultimoDiaDoMes;
	}

	public void setUltimoDiaDoMes(String ultimoDiaDoMes) {
		this.ultimoDiaDoMes = ultimoDiaDoMes;
	}

	public String getTipoLanctoComl() {
		return tipoLanctoComl;
	}

	public String getTrioContabil() {
		return trioContabil;
	}

	public String getTipoHistComissao() {
		return tipoHistComissao;
	}
	
	@Override
	public String toString() {
		return tipoLanctoComl + "|" + trioContabil + "|" + tipoHistComissao + "|" + susep + "|" + primeiroDiaDoMes + "|" + ultimoDiaDoMes;
	}
	
	
}
