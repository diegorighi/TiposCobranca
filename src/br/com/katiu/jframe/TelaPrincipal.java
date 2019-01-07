package br.com.katiu.jframe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.com.katiu.model.OnCorretor;
import br.com.katiu.model.Sics;
import br.com.katiu.model.Utils;

public class TelaPrincipal extends JFrame {

	private static final long serialVersionUID = -282462181900736709L;

	private JPanel panel = new JPanel();
	private Utils utils = new Utils(this);
	private Label label_arquivoBase = new Label("Atualizar Sucursais");
	private String label_intervaloDatas_DE = null;
	private String label_intervaloDatas_ATE = null;
	
	private Label label_tipoServico = new Label("Selecione o serviço");
	private JLabel label_arquivoSelecionado = new JLabel("", JLabel.LEFT);
	private JCheckBox cb_atualizaBase = new JCheckBox("Utilizar arquivo interno de Sucursais");
	private JCheckBox cb_lancamento = new JCheckBox("Gerar Lançamentos e Consulta dos Lançamentos Gerados");
	private JCheckBox cb_consulta = new JCheckBox("Gerar Consulta de SUSEP por período");

	private Integer opcao = 0;

	public TelaPrincipal() {
		JButton bt_sics = new JButton("SICS");
		JButton bt_onCorretor = new JButton("ONCORRETOR");
		JButton bt_carregarSusepSucursal = new JButton("Procurar");
		cb_atualizaBase.setSelected(false);
		cb_lancamento.setSelected(true);
		
		// PRIMEIRA LINHA DO PAINEL
		label_arquivoBase.setBounds(1, 9, 150, 30);
		cb_atualizaBase.setBounds(14, 40, 250, 15);
		bt_carregarSusepSucursal.setBounds(200, 4, 130, 33);
		label_arquivoSelecionado.setBounds(380, 9, 200, 30);
		cb_lancamento.setBounds(14, 120, 400, 15);
		cb_consulta.setBounds(14, 140, 350, 15);

		label_tipoServico.setBounds(1, 90, 190, 30);
		bt_sics.setBounds(200, 200, 80, 33);
		bt_onCorretor.setBounds(283, 200, 130, 33);

		onClickCarregarBase(bt_carregarSusepSucursal);
		onClickSics(bt_sics);
		onClickOnCorretor(bt_onCorretor);

		panel.setBounds(0, 0, 700, 400);
		panel.setLayout(null);
		
		panel.add(cb_atualizaBase);
		panel.add(cb_consulta);
		panel.add(cb_lancamento);
		panel.add(label_arquivoBase);
		panel.add(label_tipoServico);
		panel.add(label_arquivoSelecionado);
		panel.add(bt_carregarSusepSucursal);
		panel.add(bt_sics);
		panel.add(bt_onCorretor);
		add(panel);

		// JFrame properties
		setSize(700, 400);
		setBackground(Color.BLACK);
		setTitle("Portocom - Tipo de Cálculos");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}
	
	
	public void verifyCheckedBoxes(Utils u) throws Exception {
		u.setCheckedBaseSelecionada(cb_atualizaBase.isSelected());
		u.setCheckedConsulta(cb_consulta.isSelected());
		u.setCheckedLancamento(cb_lancamento.isSelected());
		if(!cb_consulta.isSelected() && !cb_lancamento.isSelected()) {
			JOptionPane.showMessageDialog(null, "Você deve marcar um serviço pelo menos!", "Erro na Checkbox", 0);
			throw new Exception();
		}
		if(cb_consulta.isSelected()) {
			u.setLabel_intervaloDatas_DE(JOptionPane.showInputDialog("Entre com a data início no formato dd/MM/yyyy"));
			if(u.getLabel_intervaloDatas_DE().isEmpty())
				throw new Exception("Deve informar a data inicial!");
			else 
				u.setLabel_intervaloDatas_ATE(JOptionPane.showInputDialog("Entre com a data fim no formato dd/MM/yyyy"));
			if(u.getLabel_intervaloDatas_ATE().isEmpty())
				throw new Exception("Deve informar a data fim!");
		}
	}

	private void onClickOnCorretor(JButton bt_onCorretor) {
		bt_onCorretor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Utils u = new Utils(new OnCorretor());
					verifyCheckedBoxes(u);
					u.readCSV();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
		});
	}

	private void onClickSics(JButton bt_sics) {
		bt_sics.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Utils u = new Utils(new Sics());
					verifyCheckedBoxes(u);
					u.readCSV();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void onClickCarregarBase(JButton bt_carregarSusepSucursal) {
		bt_carregarSusepSucursal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Utils u = new Utils();
					verifyCheckedBoxes(u);
					u.selectBase();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public Integer getOpcao() {
		return opcao;
	}

	public void setOpcao(Integer opcao) {
		this.opcao = opcao;
	}

	public void setLabel_arquivoBase(String arquivoSelecionado) {
		this.label_arquivoSelecionado.setText(arquivoSelecionado);
		this.label_arquivoSelecionado.setBounds(380, 7, 200, 30);
		this.label_arquivoSelecionado.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
	}

}
