package br.com.katiu.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import br.com.katiu.api.TipoCobranca;
import br.com.katiu.jframe.TelaPrincipal;

public class Utils {
	
	private static final String PROPERTIES_SUSEP = "src/susep.properties";
	private static final String PROPERTIES_INFORMIX = "src/informix.properties";
	private static final String MENSAGEM_USUARIO = "Escolha uma opção \n\n 1 para SICS \n 2 para OnCorretor";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final LocalDate mesPassado = LocalDate.now().minusMonths(1);
	
	private Properties propSusep = new Properties();
	private FileInputStream fileSusep = null;
	private Properties propInformix = new Properties();
	private FileInputStream informixFile = null;
	
	private String[] linhaDoCSV = null;
	private String linhaAtual = null;
	private LocalDateTime dataDeHoje = LocalDateTime.now();
	private String resultadoSucursal = null;
	private String nomeArquivo = null;
	private TipoCobranca clazz = null;
	
	private File baseSelecionada = null;
	private TelaPrincipal telaPrincipal = null;
	
	private boolean isCheckedBaseSelecionada = false;
	private boolean isCheckedLancamento = false;
	private boolean isCheckedConsulta = false;
	
	private String label_intervaloDatas_DE = null;
	private String label_intervaloDatas_ATE = null;
	
	public Utils() {}
	
	public Utils(TelaPrincipal tela) {
		this.telaPrincipal = tela;
	}
	
	
	
	public Utils(TipoCobranca clazz) throws Exception {
		this.clazz = clazz;
		this.nomeArquivo = clazz.nomeDoArquivo();
		
		try {
			fileSusep = new FileInputStream(PROPERTIES_SUSEP);
			propSusep.load(fileSusep);
		} catch (Exception e) {
			throw new Exception("Arquivo não encontrado. ERRO: "+e.getMessage());
		}
	}
	
	public void selectBase() throws IOException {
		
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		
		BufferedReader br = new BufferedReader(new FileReader(jfc.getSelectedFile().getAbsolutePath()));
		
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			this.baseSelecionada = jfc.getSelectedFile();
			telaPrincipal.setLabel_arquivoBase(jfc.getSelectedFile().getName().toString());
			baseEscolhida(br);
			System.out.println("Arquivo selecionado "+jfc.getSelectedFile().getAbsolutePath());
		}
	}
	
	public HashMap<String, String> baseEscolhida(BufferedReader br) throws IOException{
		HashMap<String, String> mapaSusepSucursal = new HashMap<String, String>();
		String linhaAtualDaBase = null;
		String[] linhaFormatada = null;
		
		while ((linhaAtualDaBase = br.readLine()) != null) {
			linhaFormatada = linhaAtualDaBase.split(";");
			mapaSusepSucursal.put(linhaFormatada[0].replace("﻿",""), linhaFormatada[1]);
		}
		
		if(!mapaSusepSucursal.isEmpty())
			JOptionPane.showMessageDialog(null, "Base carregada com sucesso. Total de "+mapaSusepSucursal.size()+" registros", "BASE OK", 1);
		
		return mapaSusepSucursal;
	}
	
	public boolean selectFile() {
		boolean bypass = false;
		HashMap<String, String> mapaArquivo = new HashMap<>();
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			
			mapaArquivo.put("nomeArquivo", selectedFile.getName().toString());
			mapaArquivo.put("pathArquivo", selectedFile.getAbsolutePath().replace(this.nomeArquivo.toString(), ""));
			
			if(this.nomeArquivo.equals(mapaArquivo.get("nomeArquivo").toString()) &&
					selectedFile.exists())
				bypass = true;
			else
				JOptionPane.showMessageDialog(null, "O arquivo não está de acordo com a opção escolhida", "[PORTOCOM] - Arquivo divergente", 1);
			
		}
		
		return bypass;
	}
	
	public void readCSV() throws Exception {
		
		if(selectFile()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\" + getNomeArquivo()));
				clazz.start(br, this);
				br.close();
			} catch (FileNotFoundException ntfe) {
				throw new Exception("O arquivo não existe ou pode conter caracteres inválidos. Erro: "+ntfe.getMessage());
			} catch(Exception e) {
				throw new Exception("Ocorreu um erro inesperado. Erro: "+e.getMessage());
			}
		}else {
			throw new Exception("O arquivo selecionado não é compatível com a opção selecionada. Por favor, escolha o arquivo ou a opção correta!");
		}
		
		
	}
	
	public String getSucursal(String susep, TipoCobranca clazz) {
		String sucursal = "null";
		
		susep = ignoreRule(susep, clazz);
		
		if(propSusep.getProperty(susep.trim()) != null)
			sucursal = propSusep.getProperty(susep);
		
		return sucursal;
	}
	
	public String getInformix(String informix) {
		String retornoInformix = "null";

		try {
			informixFile = new FileInputStream(PROPERTIES_INFORMIX);
			propInformix.load(informixFile);
			if(propInformix.getProperty(informix.trim()) != null)
				retornoInformix = propInformix.getProperty(informix);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retornoInformix;
	}

	private String ignoreRule(String susep, TipoCobranca clazz) {
		if(susep.startsWith("ϻ�") || susep.startsWith("SUSEP") || susep.startsWith("TOTAL")) {
			susep = susep.substring(3);
			clazz.setCountIgnoredLines(clazz.getCountIgnoredLines()+1);
		}
		return susep;
	}
	
	public String getDataArquivo() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String formatDateTime = now.format(formatter);
		
		return formatDateTime;
	}

	public Properties getPropSusep() {
		return propSusep;
	}

	public void setPropSusep(Properties propSusep) {
		this.propSusep = propSusep;
	}

	public FileInputStream getFileSusep() {
		return fileSusep;
	}

	public void setFileSusep(FileInputStream fileSusep) {
		this.fileSusep = fileSusep;
	}

	public Properties getPropInformix() {
		return propInformix;
	}

	public void setPropInformix(Properties propInformix) {
		this.propInformix = propInformix;
	}

	public FileInputStream getInformixFile() {
		return informixFile;
	}

	public void setInformixFile(FileInputStream informixFile) {
		this.informixFile = informixFile;
	}

	public String[] getLinhaDoCSV() {
		return linhaDoCSV;
	}

	public void setLinhaDoCSV(String[] linhaDoCSV) {
		this.linhaDoCSV = linhaDoCSV;
	}

	public String getLinhaAtual() {
		return linhaAtual;
	}

	public void setLinhaAtual(String linhaAtual) {
		this.linhaAtual = linhaAtual;
	}

	public LocalDateTime getDataDeHoje() {
		return dataDeHoje;
	}

	public void setDataDeHoje(LocalDateTime dataDeHoje) {
		this.dataDeHoje = dataDeHoje;
	}

	public String getResultadoSucursal() {
		return resultadoSucursal;
	}

	public void setResultadoSucursal(String resultadoSucursal) {
		this.resultadoSucursal = resultadoSucursal;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public static String getPropertiesSusep() {
		return PROPERTIES_SUSEP;
	}

	public static String getPropertiesInformix() {
		return PROPERTIES_INFORMIX;
	}

	public static String getMensagemUsuario() {
		return MENSAGEM_USUARIO;
	}

	public static DateTimeFormatter getFormatter() {
		return formatter;
	}

	public static LocalDate getMespassado() {
		return mesPassado;
	}

	public File getBaseSelecionada() {
		return baseSelecionada;
	}

	public void setBaseSelecionada(File baseSelecionada) {
		this.baseSelecionada = baseSelecionada;
	}

	public boolean isCheckedBaseSelecionada() {
		return isCheckedBaseSelecionada;
	}

	public void setCheckedBaseSelecionada(boolean isCheckedBaseSelecionada) {
		this.isCheckedBaseSelecionada = isCheckedBaseSelecionada;
	}

	public boolean isCheckedLancamento() {
		return isCheckedLancamento;
	}

	public void setCheckedLancamento(boolean isCheckedLancamento) {
		this.isCheckedLancamento = isCheckedLancamento;
	}

	public boolean isCheckedConsulta() {
		return isCheckedConsulta;
	}

	public void setCheckedConsulta(boolean isCheckedConsulta) {
		this.isCheckedConsulta = isCheckedConsulta;
	}

	public String getLabel_intervaloDatas_DE() {
		return label_intervaloDatas_DE;
	}

	public void setLabel_intervaloDatas_DE(String label_intervaloDatas_DE) {
		this.label_intervaloDatas_DE = label_intervaloDatas_DE;
	}

	public String getLabel_intervaloDatas_ATE() {
		return label_intervaloDatas_ATE;
	}

	public void setLabel_intervaloDatas_ATE(String label_intervaloDatas_ATE) {
		this.label_intervaloDatas_ATE = label_intervaloDatas_ATE;
	}
	
}
