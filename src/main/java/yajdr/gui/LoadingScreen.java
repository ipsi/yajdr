package yajdr.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Box;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JWindow;

import yajdr.core.DieRollerMain;
import yajdr.interfaces.ThreadProgress;

public class LoadingScreen extends JWindow implements ThreadProgress
{

	private static final long	serialVersionUID	= 6891022106440086680L;

	private JProgressBar		progBar				= new JProgressBar(0, 100);

	private JTextField			lab					= new JTextField("Loading DiceRollerApp");



	public LoadingScreen()
	{
		progBar.setStringPainted(true);
		// lab.setLabelFor(progBar);

		Box box = new Box(1);
		box.add(lab);
		box.add(progBar);
		getContentPane().add(box, BorderLayout.CENTER);

		lab.setBackground(Color.WHITE);
		lab.setEditable(false);

		pack();
		setSize(getSize().width + 20, getSize().height);
		setLocation(DieRollerMain.centreOnScreen(getSize()));

		setVisible(true);
	}



	public void update(int current, int total)
	{
		progBar.setMaximum(total);
		progBar.setValue(current);
	}



	public void update(String message)
	{
		progBar.setString(message);
	}



	public void resetProgressBar()
	{
		progBar.setValue(0);
	}
}
