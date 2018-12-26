package br.com.katiu.jframe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.katiu.model.OnCorretor;
import br.com.katiu.model.Sics;
import br.com.katiu.model.Utils;

public class TelaPrincipal extends JFrame {

	private static final long serialVersionUID = -282462181900736709L;

	private JPanel panel = new JPanel();
	private Utils utils = new Utils(this);
	private Label label_arquivoBase = new Label("Carregar arquivo base");
	private Label label_tipoCalculo = new Label("Informe o tipo de cáculo");
	private JLabel label_arquivoSelecionado = new JLabel("Selecionado: Base Interna", JLabel.LEFT);

	private Integer opcao = 0;

	public TelaPrincipal() {
		JButton bt_sics = new JButton("SICS");
		JButton bt_onCorretor = new JButton("ONCORRETOR");
		JButton bt_carregarSusepSucursal = new JButton("Procurar");

		// PRIMEIRA LINHA DO PAINEL
		label_arquivoBase.setBounds(1, 9, 200, 30);
		bt_carregarSusepSucursal.setBounds(200, 4, 130, 33);
		label_arquivoSelecionado.setBounds(380, 9, 200, 30);

		label_tipoCalculo.setBounds(8, 45, 190, 30);
		bt_sics.setBounds(200, 40, 80, 33);
		bt_onCorretor.setBounds(283, 40, 130, 33);

		onClickCarregarBase(bt_carregarSusepSucursal);
		onClickSics(bt_sics);
		onClickOnCorretor(bt_onCorretor);

		panel.setBounds(0, 0, 500, 600);
		panel.setLayout(null);

		panel.add(label_arquivoBase);
		panel.add(label_tipoCalculo);
		panel.add(label_arquivoSelecionado);
		panel.add(bt_carregarSusepSucursal);
		panel.add(bt_sics);
		panel.add(bt_onCorretor);
		add(panel);

		// JFrame properties
		setSize(600, 140);
		setBackground(Color.BLACK);
		setTitle("Portocom - Tipo de Cálculos");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void onClickOnCorretor(JButton bt_onCorretor) {
		bt_onCorretor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new Utils(new OnCorretor()).readCSV();
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
					new Utils(new Sics()).readCSV();
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
					utils.selectBase();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
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
