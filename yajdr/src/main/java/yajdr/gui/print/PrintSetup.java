package yajdr.gui.print;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import yajdr.core.DieRollerMain;

public class PrintSetup extends JFrame implements ActionListener, KeyListener
{

	private static final long	serialVersionUID	= -7126698749954745878L;

	private JTextField			charmsPerRow		= null;

	private JLabel				cprLab				= new JLabel("Charms per Row");

	private JTextField			charmsPerColumn		= null;

	private JLabel				cpcLab				= new JLabel("Charms per Column");

	// private JTextField charmsPerPage = new JTextField("" + charmsPerPage);
	String						charmsPerPageBase	= "Charms Per Page: ";

	private JLabel				cppLab				= new JLabel(charmsPerPageBase);

	private JLabel				cppTwoLab			= null;

	private JButton				pageSetup			= new JButton("Page Setup");

	private JButton				saveChanges			= new JButton("Save Changes");

	private PrinterJob			printerjob;

	private PageFormat			format;

	private PrintCharm			printCharm			= null;



	public PrintSetup(PrinterJob pj, PageFormat pf, PrintCharm printCharm)
	{
		printerjob = pj;
		format = pf;
		this.printCharm = printCharm;

		charmsPerRow = new JTextField(String.valueOf(printCharm.getCharmsPerRow()));
		charmsPerColumn = new JTextField(String.valueOf(printCharm.getCharmsPerColumn()));
		cppTwoLab = new JLabel(String.valueOf(printCharm.getCharmsPerPage()));

		setTitle("Page Setup");

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		getContentPane().setLayout(gbl);

		addStuff(getContentPane(), cppLab, gbc, gbl);
		addOtherStuff(getContentPane(), cppTwoLab, gbc, gbl);
		// addOtherStuff(getContentPane(), charmsPerPage, gbc, gbl);

		addStuff(getContentPane(), cprLab, gbc, gbl);
		addOtherStuff(getContentPane(), charmsPerRow, gbc, gbl);

		addStuff(getContentPane(), cpcLab, gbc, gbl);
		addOtherStuff(getContentPane(), charmsPerColumn, gbc, gbl);

		addOtherStuff(getContentPane(), pageSetup, gbc, gbl);
		addOtherStuff(getContentPane(), saveChanges, gbc, gbl);

		pageSetup.addActionListener(this);
		saveChanges.addActionListener(this);
		pack();
		setLocation(DieRollerMain.centreOnScreen(getSize()));
		setVisible(true);
	}



	private void addStuff(Container cont, Component comp, GridBagConstraints gbc, GridBagLayout gbl)
	{
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = 1;

		gbl.setConstraints(comp, gbc);
		cont.add(comp);
	}



	private void addOtherStuff(Container cont, Component comp, GridBagConstraints gbc, GridBagLayout gbl)
	{
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		if (comp instanceof JTextField)
		{
			((JTextField) comp).addKeyListener(this);
		}

		gbl.setConstraints(comp, gbc);
		cont.add(comp);
	}



	public void actionPerformed(ActionEvent ae)
	{
		Object bp = ae.getSource();

		if (bp == pageSetup)
		{
			format = printerjob.pageDialog(printCharm.getPageFormat());
		}
		else if (bp == saveChanges)
		{
			printCharm.setCharmsPerRow(getCharmsPerRow());
			printCharm.setCharmsPerColumn(getCharmsPerColumn());
			printCharm.setCharmsPerPage(printCharm.getCharmsPerRow() * printCharm.getCharmsPerColumn());
			printCharm.setPageFormat(format);
			setVisible(false);
			dispose();
		}
	}



	public void keyPressed(KeyEvent ke)
	{
		// Needed to satisfy inteface.
	}



	public void keyReleased(KeyEvent ke)
	{
		if (getCharmsPerRow() > 0 && getCharmsPerColumn() > 0)
		{
			cppTwoLab.setText("" + (getCharmsPerRow() * getCharmsPerColumn()));
		}
	}



	public void keyTyped(KeyEvent ke)
	{
		// Needed to satisfy inteface.
	}



	private int getCharmsPerRow()
	{
		try
		{
			return Integer.parseInt(charmsPerRow.getText());
		}
		catch (NumberFormatException ex)
		{
			return -1;
		}
	}



	private int getCharmsPerColumn()
	{
		try
		{
			return Integer.parseInt(charmsPerColumn.getText());
		}
		catch (NumberFormatException ex)
		{
			return -1;
		}
	}
}
