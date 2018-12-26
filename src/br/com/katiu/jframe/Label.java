package br.com.katiu.jframe;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Label extends JPanel {

	private static final long serialVersionUID = -7938106085462175246L;
	
	private JLabel value = new JLabel();

	public Label(String nomeLabel) {
		setLayout( new FlowLayout() );
		value = new JLabel(nomeLabel);
		add(value);
	}
}
