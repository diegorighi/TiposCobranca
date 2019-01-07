package br.com.katiu.model;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import br.com.katiu.api.TipoCobranca;

public class OnCorretor implements TipoCobranca {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("05/MM/yyyy");
	private static final LocalDateTime DATA_ATUAL = LocalDateTime.now();

	private final String empresa = "1";
	private final String tipoLanctoComl = "3";
	private final String trioContabil = "7";
	private final String tipoHistComissao = "67";
	private String susep = null;
	private String sucursal = "0";
	private final String ramo = "531";
	private final String subramo = "0";
	private final String apolice = "0";
	private final String tipoDocumento = "NTR";
	private final String endosso = "0";
	private final String nroCarneVida = "0";
	private final String origemProposta = "0";
	private final String proposta = "0";
	private final String parcela = "0";
	private String dataDebito;
	private String valorServico;
	private final String taxaComissao = "100";
	private final String moeda = "R$";
	private String descricaoHistorico;
	private final String segurado = "";
	private final String participacaoCorretor = "100";
	private final String participacaoCia = "100";
	private final String modalidade = "0";
	private final String nomePrograma = "ONCORRETOR";

	private ArrayList<OnCorretor> listaOnCorretor = new ArrayList<OnCorretor>();
	private List<String>listaSucursalNula = new ArrayList<String>();
	private List<OnCorretorConsulta>listaOnCorretorConsulta = new ArrayList<OnCorretorConsulta>();
	private List<OnCorretorSUSEP>listaOnCorretorSUSEP = new ArrayList<OnCorretorSUSEP>();

	private int countLinhas = 0;
	private int countSubsidio = 0;
	private int countSucursalNulas = 0;
	private int countSemSubsidio = 0;
	private int countIgnoredLines = 0;

	private String linhaAtualDoCSV = null;
	private String[] linhaFormatada = null;

	private String descricao = null;
	private String subsidio = null;
	
	private String nomeArquivoGerado = null;

	public OnCorretor() {
	}

	public OnCorretor(String susep, String sucursal, String dataDebito, String valorServico, String descricao) {
		this.susep = susep;
		this.sucursal = sucursal;
		this.dataDebito = dataDebito;
		this.valorServico = valorServico;
		this.descricaoHistorico = descricao;
		this.valorServico = this.valorServico.replace("R$ ", "").replace(",", ".").trim();
	}
	
	@Override
	public void start(BufferedReader br, Utils utils) throws IOException {
		while ((this.linhaAtualDoCSV = br.readLine()) != null) {
			extrairInsumos(utils);
			if(utils.isCheckedConsulta())
				generateSearchFileContent(utils);
			
		}
		
		if(utils.isCheckedLancamento()) {
			generateFile(utils); // ARQUIVO DE LANÇAMENTO
			generateFileConsultaSUSEP(utils); // ESCREVE O ARQUIVO DE CONSULTA
		}
		if(utils.isCheckedConsulta())
			generateFileSearch();
		if(getCountSucursalNulas() >= 1)
			generateAlertSucursal();
		getDetails();
	}
	
	public void generateConsultaSUSEPContent(Utils utils){
		addArraySUSEP(utils);
	}
	
	private void generateFileConsultaSUSEP(Utils utils) {
		StringBuilder sb = new StringBuilder();
		sb.append("CONSULTAONCORRETOR");
		sb.append(utils.getDataArquivo());
		sb.append(".txt");
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

			for (OnCorretorSUSEP objeto : listaOnCorretorSUSEP) {
				gravarArq.println(objeto.toString());
				gravarArq.flush();
			}
			gravarArq.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		addToArrayConsulta(utils);
	}

	private void extrairInsumos(Utils utils) {
		setLinhaFormatada(this.linhaAtualDoCSV.split(";"));
		setCountLinhas(getCountLinhas() + 1);
		if (!getLinhaFormatada()[2].toUpperCase().trim().contains("SUSEP")) {
			setSusep(getLinhaFormatada()[2].toUpperCase().trim());
			setSucursal(utils.getSucursal(getSusep(), this));
			
			if(getSucursal() == "null") {
				setCountSucursalNulas(getCountSucursalNulas()+1);
				this.listaSucursalNula.add(getSusep());
			}
			
			generateFileContent(utils);
			
		}
	}
	
	public void generateAlertSucursal() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.nomeArquivoGerado.replace(".txt",""));
		sb.append("_ERROS");
		sb.append(".txt");
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);
			gravarArq.println("#################	REGISTROS NÃO ENCONTRADAS	#################");
			gravarArq.println("##			AS SUSEPS LISTADAS NÃO FORAM ENCONTRADAS		   ##");
			for (String objeto : this.listaSucursalNula) {
				gravarArq.println(objeto);
				gravarArq.flush();
			}
			gravarArq.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Override
	public String nomeDoArquivo() {
		return "arquivoOnCorretor.csv";
	}
	
	public void generateSearchFileContent(Utils utils){
		addToArrayConsulta(utils);
	}

	public void generateFileContent(Utils utils) {
		if (getSucursal() != "null") {
			descriptionGenerate();
			addToArray();
			addArraySUSEP(utils);
		} else {
			this.countSucursalNulas++;
		}
	}
	
	public void addToArrayConsulta(Utils utils) {
		if(!linhaFormatada[2].toUpperCase().contains("SUSEP")) {
			addListaOnCorretorConsulta(new OnCorretorConsulta(linhaFormatada[2].toUpperCase(), 
					utils.getLabel_intervaloDatas_DE(),
					utils.getLabel_intervaloDatas_ATE()
			));
		}
	}
	
	public void addArraySUSEP(Utils utils) {
		if(!linhaFormatada[2].toUpperCase().contains("SUSEP")) {
			addListaOnCorretorSUSEP(new OnCorretorSUSEP(linhaFormatada[2].toUpperCase(), getSucursal(),
					DATA_ATUAL.format(FORMATTER), linhaFormatada[1]
			));
		}
	}

	private void addToArray() {
		addListaOnCorretor(new OnCorretor(linhaFormatada[2].toUpperCase(), getSucursal(), DATA_ATUAL.format(FORMATTER),
					"-" + linhaFormatada[1], getDescricao()));
	}

	private void descriptionGenerate() {
		StringBuilder sb = new StringBuilder();
		sb.append(new Utils().getInformix(getLinhaFormatada()[0].toString()));
		
		sb.append(" - ");
		sb.append(Utils.getMespassado().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-br")));
		sb.append("/");
		sb.append(DATA_ATUAL.getYear());

		setDescricao(sb.toString().toUpperCase());
	}

	private void getDetails() {
		JOptionPane.showMessageDialog(null,
				"O programa leu: " + countLinhas + " linhas. Sendo " + countIgnoredLines
						+ " linha(s) ignoradas de cabeçalho e/ou rodapé \n" + "Sucursal Nulas: " + countSucursalNulas,
				"[PORTOCOM] - RESUMO OnCorretor", 1);
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

	public String getDescricaoHistorico() {
		return descricaoHistorico;
	}

	public void setDescricaoHistorico(String descricaoHistorico) {
		this.descricaoHistorico = descricaoHistorico;
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

	public String getSubramo() {
		return subramo;
	}

	public String getApolice() {
		return apolice;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public String getEndosso() {
		return endosso;
	}

	public String getNroCarneVida() {
		return nroCarneVida;
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

	public String getTaxaComissao() {
		return taxaComissao;
	}

	public String getMoeda() {
		return moeda;
	}

	public String getSegurado() {
		return segurado;
	}

	public String getParticipacaoCorretor() {
		return participacaoCorretor;
	}

	public String getParticipacaoCia() {
		return participacaoCia;
	}

	public String getModalidade() {
		return modalidade;
	}

	public String getNomePrograma() {
		return nomePrograma;
	}

	public int getCountLinhas() {
		return countLinhas;
	}

	public void setCountLinhas(int countLinhas) {
		this.countLinhas = countLinhas;
	}

	public int getCountSubsidio() {
		return countSubsidio;
	}

	public void setCountSubsidio(int countSubsidio) {
		this.countSubsidio = countSubsidio;
	}

	public int getCountSucursalNulas() {
		return countSucursalNulas;
	}

	public void setCountSucursalNulas(int countSucursalNulas) {
		this.countSucursalNulas = countSucursalNulas;
	}

	public int getCountSemSubsidio() {
		return countSemSubsidio;
	}

	public void setCountSemSubsidio(int countSemSubsidio) {
		this.countSemSubsidio = countSemSubsidio;
	}

	public int getCountIgnoredLines() {
		return countIgnoredLines;
	}

	public void setCountIgnoredLines(int countIgnoredLines) {
		this.countIgnoredLines = countIgnoredLines;
	}

	public String getLinhaAtualDoCSV() {
		return linhaAtualDoCSV;
	}

	public void setLinhaAtualDoCSV(String linhaAtualDoCSV) {
		this.linhaAtualDoCSV = linhaAtualDoCSV;
	}

	public String[] getLinhaFormatada() {
		return linhaFormatada;
	}

	public void setLinhaFormatada(String[] linhaFormatada) {
		this.linhaFormatada = linhaFormatada;
	}
	
	

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSubsidio() {
		return subsidio;
	}

	public void setSubsidio(String subsidio) {
		this.subsidio = subsidio;
	}
	
	public void addListaOnCorretorSUSEP(OnCorretorSUSEP objeto) {
		this.listaOnCorretorSUSEP.add(objeto);
	}
	
	public void addListaOnCorretorConsulta(OnCorretorConsulta objeto) {
		this.listaOnCorretorConsulta.add(objeto);
	}

	public void addListaOnCorretor(OnCorretor objeto) {
		this.listaOnCorretor.add(objeto);
	}

	@Override
	public String toString() {
		return empresa + "|" + tipoLanctoComl + "|" + trioContabil + "|" + tipoHistComissao + "|" + susep + "|"
				+ sucursal + "|" + ramo + "|" + subramo + "|" + apolice + "|" + tipoDocumento + "|" + endosso + "|"
				+ nroCarneVida + "|" + origemProposta + "|" + proposta + "|" + parcela + "|" + dataDebito + "|"
				+ valorServico + "|" + taxaComissao + "|" + moeda + "|" + descricaoHistorico + "|" + segurado + "|"
				+ participacaoCorretor + "|" + participacaoCia + "|" + modalidade + "|" + nomePrograma;
	}

	public void generateFile(Utils utils) {
		StringBuilder sb = new StringBuilder();
		sb.append("COBRANCAONCORRETOR");
		sb.append(utils.getDataArquivo());
		sb.append(".txt");
		
		this.nomeArquivoGerado = sb.toString();

		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

			for (OnCorretor objeto : listaOnCorretor) {
				gravarArq.println(objeto.toString());
				gravarArq.flush();
			}
			gravarArq.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void generateFileSearch() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONSUSEPONCORRETOR");
		sb.append(".txt");
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

			for (OnCorretorConsulta objeto : listaOnCorretorConsulta) {
				gravarArq.println(objeto.toString());
				gravarArq.flush();
			}
			gravarArq.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

}
