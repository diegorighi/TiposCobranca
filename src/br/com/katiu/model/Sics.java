package br.com.katiu.model;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import br.com.katiu.api.TipoCobranca;

public class Sics implements TipoCobranca {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("01/MM/yyyy");
	private static final LocalDateTime DATA_ATUAL = LocalDateTime.now();

	private final String empresa = "1";
	private final String tipoLanctoComl = "3";
	private final String trioContabil = "7";
	private final String tipoHistComissao = "39";
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
	private final String nomePrograma = "SICS";
	
	private List<Sics>listaSics = new ArrayList<Sics>();
	private List<String>listaSucursalNula = new ArrayList<String>();
	private List<SicsConsulta>listaSicsConsulta = new ArrayList<SicsConsulta>();
	private List<SicsSUSEP>listaSicsSUSEP = new ArrayList<SicsSUSEP>();
	
	private int countLinhas = 0;
	private int countSubsidio = 0;
	private int countSucursalNulas = -3;
	private int countSemSubsidio = 0;
	private int countIgnoredLines = 0;
	
	private String linhaAtualDoCSV = null;
	private String[] linhaFormatada = null;
	
	private String descricao = null;
	private String subsidio = null;
	
	private String nomeArquivoGerado = null;
	
	private LocalDate primeiroDiaDoMes = null;
	private LocalDate ultimoDiaDoMes = null;

	public Sics(){
		
	}
	
	public Sics(String susep, LocalDate primeiroDiaDoMes, LocalDate ultimoDiaDoMes) {
		this.susep = susep;
		this.primeiroDiaDoMes = primeiroDiaDoMes;
		this.ultimoDiaDoMes = ultimoDiaDoMes;
	}
	
	public Sics(String susep, String sucursal, String dataDebito, String valorServico, String descricao) {
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
		if(utils.isCheckedLancamento())
			generateFile(utils);
		if(utils.isCheckedConsulta())
			generateFileSearch();
		if(getCountSucursalNulas() <= -3)
			generateAlertSucursal();
		getDetails();
	}

	private void extrairInsumos(Utils utils) {
		setCountLinhas(getCountLinhas()+1);
		setLinhaFormatada(this.linhaAtualDoCSV.split(";"));
		setSusep(getLinhaFormatada()[0].toUpperCase().trim());
		setSucursal(utils.getSucursal(getSusep(), this));
		
		if(getSucursal() == "" || getSucursal().equals(null) || getSucursal().equals("") || getSucursal() == null) {
			setCountSucursalNulas(getCountSucursalNulas()+1);
			this.listaSucursalNula.add(getSusep());
			System.out.println(getCountSucursalNulas());
		}
		
		generateFileContent();
	}
	
	public void generateFileContent() {
		if(getSucursal() != "null") {
			descriptionGenerate();
			subsidioGenerate();
			addToArray();
		}else {
			this.countSucursalNulas++;
		}
	}
	
	public void generateFile(Utils utils) {
		StringBuilder sb = new StringBuilder();
		sb.append("COBRANCASICS");
		sb.append(utils.getDataArquivo());
		sb.append(".txt");
		
		this.nomeArquivoGerado = sb.toString();
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

			for (Sics objeto : listaSics) {
				gravarArq.println(objeto.toString());
				gravarArq.flush();
			}
			gravarArq.close();
			
			generateSicsSusepFile(utils);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void generateSicsSusepFile(Utils utils) {
		StringBuilder sb = new StringBuilder();
		sb.append("CONSULTASICS");
		sb.append(utils.getDataArquivo());
		sb.append(".txt");
		
		this.nomeArquivoGerado = sb.toString();
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

			for (SicsSUSEP objeto : listaSicsSUSEP) {
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
		sb.append("CONSUSEPSICS");
		sb.append(".txt");
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

			for (SicsConsulta objeto : listaSicsConsulta) {
				gravarArq.println(objeto.toString());
				gravarArq.flush();
			}
			gravarArq.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void generateSearchFileContent(Utils utils) {
		addToArrayConsulta(utils);
	}
	
	public void generateAlertSucursal() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.nomeArquivoGerado.replace(".txt",""));
		sb.append("_ERROS");
		sb.append(".txt");
		
		try {
			FileWriter arq = new FileWriter(System.getProperty("user.dir") + "\\" + sb.toString());
			PrintWriter gravarArq = new PrintWriter(arq);

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

	private void getDetails() {
		if(getCountSucursalNulas() == 0) {
			JOptionPane.showMessageDialog(null, "O programa leu: "+countLinhas+" linhas. Sendo "+countIgnoredLines+" linha(s) ignoradas de cabeçalho e/ou rodapé \n"
					+ "Sem Subsídio: "+countSemSubsidio+"\n"
							+ "Com Subsídio: "+countSubsidio*2+"\n"
									+ "Sem ocorrências de Sucursal nula", "[PORTOCOM] - RESUMO SICS", 1);
		}else {
			JOptionPane.showMessageDialog(null, "O programa leu: "+countLinhas+" linhas. Sendo "+countIgnoredLines+" linha(s) ignoradas de cabeçalho e/ou rodapé \n"
					+ "Sem Subsídio: "+countSemSubsidio+"\n"
							+ "Com Subsídio: "+countSubsidio*2+"\n", "[PORTOCOM] - RESUMO SICS", 1);
			JOptionPane.showMessageDialog(null, "O programa identificou "+getCountSucursalNulas()+" ocorrências de Sucursai nulas", "[PORTOCOM] - RESUMO SICS", JOptionPane.WARNING_MESSAGE);
		}
		
	
	}

	private void addToArray() {
		if(getSubsidio().equals("0,00")) {
			this.countSemSubsidio++;
			addListaSics(new Sics(linhaFormatada[0].toUpperCase(), getSucursal().toUpperCase(), DATA_ATUAL.format(FORMATTER), 
					"-" + linhaFormatada[9].toUpperCase(), getDescricao()));
			
			// ADICIONA CONSULTA-SUSEP 
			
			addListaSicsSUSEP(new SicsSUSEP(linhaFormatada[0].toUpperCase(), getSucursal().toUpperCase(), DATA_ATUAL.format(FORMATTER),
					linhaFormatada[9].toUpperCase()));
		}else {
			this.countSubsidio++;
			addListaSics(new Sics(linhaFormatada[0].toUpperCase(), getSucursal().toUpperCase(), DATA_ATUAL.format(FORMATTER), 
					"-" + linhaFormatada[4].toUpperCase(), getDescricao()));
			addListaSics(new Sics(linhaFormatada[0].toUpperCase(), getSucursal().toUpperCase(), DATA_ATUAL.format(FORMATTER), 
					"-" + getSubsidio(), getDescricao()));
			
			addListaSicsSUSEP(new SicsSUSEP(linhaFormatada[0].toUpperCase(), getSucursal().toUpperCase(), DATA_ATUAL.format(FORMATTER),
					linhaFormatada[4].toUpperCase()));
			addListaSicsSUSEP(new SicsSUSEP(linhaFormatada[0].toUpperCase(), getSucursal().toUpperCase(), DATA_ATUAL.format(FORMATTER),
					getSubsidio()));
			
		}
		
		
		
		
	}
	
	
	private void addToArrayConsulta(Utils utils) {
		if(getSucursal() != "null") {
			addListaSicsConsulta(new SicsConsulta(linhaFormatada[0].toUpperCase(), 
					utils.getLabel_intervaloDatas_DE(),
					utils.getLabel_intervaloDatas_ATE()
			));
		}
	}

	private void subsidioGenerate() {
		setSubsidio(linhaFormatada[5].replace("R$", "").trim());
	}

	private void descriptionGenerate() {
		StringBuilder sb = new StringBuilder();
		sb.append(getLinhaFormatada()[3].toString());
		sb.append("-");
		sb.append(Utils.getMespassado().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-br")));
		sb.append("/");
		sb.append(DATA_ATUAL.getYear());
		
		setDescricao(sb.toString().toUpperCase());
	}
	
	@Override
	public String nomeDoArquivo() {
		return "arquivoSics.csv";
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

	public String getdescricaoHistorico() {
		return descricaoHistorico;
	}

	public void setdescricaoHistorico(String descricaoHistorico) {
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

	public String getDescricaoHistorico() {
		return descricaoHistorico;
	}

	public void setDescricaoHistorico(String descricaoHistorico) {
		this.descricaoHistorico = descricaoHistorico;
	}
	
	public void addListaSics(Sics objeto) {
		this.listaSics.add(objeto);
	}
	
	public void addListaSicsSUSEP(SicsSUSEP objeto) {
		this.listaSicsSUSEP.add(objeto);
	}
	
	public void addListaSicsConsulta(SicsConsulta objeto) {
		this.listaSicsConsulta.add(objeto);
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
	
	public int getCountIgnoredLines() {
		return countIgnoredLines;
	}

	public void setCountIgnoredLines(int countIgnoredLines) {
		this.countIgnoredLines = countIgnoredLines;
	}
	
	
	public List<Sics> getListaSics() {
		return listaSics;
	}
	
	public String getNomeArquivoGerado() {
		return nomeArquivoGerado;
	}

	public void setNomeArquivoGerado(String nomeArquivoGerado) {
		this.nomeArquivoGerado = nomeArquivoGerado;
	}

	public LocalDate getPrimeiroDiaDoMes() {
		return primeiroDiaDoMes;
	}

	public void setPrimeiroDiaDoMes(LocalDate primeiroDiaDoMes) {
		this.primeiroDiaDoMes = primeiroDiaDoMes;
	}

	public LocalDate getUltimoDiaDoMes() {
		return ultimoDiaDoMes;
	}

	public void setUltimoDiaDoMes(LocalDate ultimoDiaDoMes) {
		this.ultimoDiaDoMes = ultimoDiaDoMes;
	}

	@Override
	public String toString() {
		return empresa + "|" + tipoLanctoComl + "|" + trioContabil + "|" + tipoHistComissao + "|" + susep + "|"
				+ sucursal + "|" + ramo + "|" + subramo + "|" + apolice + "|" + tipoDocumento + "|" + endosso + "|"
				+ nroCarneVida + "|" + origemProposta + "|" + proposta + "|" + parcela + "|" + dataDebito + "|"
				+ valorServico + "|" + taxaComissao + "|" + moeda + "|" + descricaoHistorico + "|" + segurado + "|"
				+ participacaoCorretor + "|" + participacaoCia + "|" + modalidade + "|" + nomePrograma;
	}



}
