package br.com.katiu.model;

public class OnCorretorSUSEP {

	private final String empresa = "1";
	private final String tipoLanctoComl = "3";
	private final String trioContabil = "7";
	private final String tipoHistComissao = "67";
	private String susep = null;
	private String sucursal = "0";
	private final String ramo = "531";
	private final String origemProposta = "0";
	private final String proposta = "0";
	private final String parcela = "0";
	private String dataDebito;
	private String valorServico = "";
	
	public OnCorretorSUSEP(String susep, String sucursal, String dataDebito, String valorServico) {
		this.susep = susep;
		this.sucursal = sucursal;
		this.dataDebito = dataDebito;
		this.valorServico = valorServico.replace("R$ ", "").replace(",", ".").trim();
	}
	
	public String getSusep() {
		return susep;
	}
	public void setSusep(String susep) {
		this.susep = susep;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getDataDebito() {
		return dataDebito;
	}
	public void setDataDebito(String dataDebito) {
		this.dataDebito = dataDebito;
	}
	public String getValorServico() {
		return valorServico;
	}
	public void setValorServico(String valorServico) {
		this.valorServico = valorServico;
	}
	public String getEmpresa() {
		return empresa;
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
	public String getRamo() {
		return ramo;
	}
	public String getOrigemProposta() {
		return origemProposta;
	}
	public String getProposta() {
		return proposta;
	}
	public String getParcela() {
		return parcela;
	}
	
	
	
	@Override
	public String toString() {
		return  empresa + "|" + tipoLanctoComl + "|"
				+ trioContabil + "|" + tipoHistComissao + "|" + susep + "|"
				+ sucursal + "|" + ramo + "|" + origemProposta + "|" + proposta
				+ "|" + parcela + "|" + dataDebito + "|" + valorServico;
	}
	
	
	
}
