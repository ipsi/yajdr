package yajdr.gui.charmgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import yajdr.core.DieRollerMain;
import yajdr.gui.CharmGUI;


/**
 * This creates the filter window. It's currently quite ineffecient, and will be redone when I have
 * time, and probably after some new things are implemented. It also has the problem of not
 * maintaining the current query data, and instead reselecting the entire dataset. (SELECT * FROM
 * charmlist) I'm not quite sure why I made some of the decisions I did when building this class. I
 * was obviously on crack. Needs rebuilding. Logic kinda silly. Works, but could be cleaner.
 */
public class Filter extends JFrame implements ActionListener
{

	private static final long	serialVersionUID	= -1113050928042393213L;

	private Vector<JCheckBox>	boxes				= new Vector<JCheckBox>();

	private JButton				filter				= new JButton("Filter");

	private JButton				all					= new JButton("All");

	private JButton				none				= new JButton("None");

	private JToolBar			bar					= new JToolBar();

	private JTable				table;

	private CharmGUI			cgui;

	private String[]			checkBoxNames;



	public Filter(JTable t, CharmGUI cgui, String[] sar)
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		table = t;
		this.cgui = cgui;
		checkBoxNames = sar;

		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(bar, BorderLayout.NORTH);

		bar.add(filter);
		bar.add(all);
		bar.add(none);

		filter.addActionListener(this);
		all.addActionListener(this);
		none.addActionListener(this);

		Box panel = new Box(1);

		panel.add(makeBoxes());
		// panel.add(makeFields());

		getContentPane().add(panel, BorderLayout.CENTER);

		pack();

		setLocation(DieRollerMain.centreOnScreen(getSize()));

		setVisible(true);
	}



	private Component makeBoxes()
	{
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		Box checkBoxFull = new Box(1);
		JPanel checkBoxes_1 = new JPanel(gbl);
		JPanel checkBoxes_2 = new JPanel(gbl);

		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.weightx = 1;
		gbc.weighty = 0;

		TreeSet<Object> ts = getColumns();

		for (int i = 0; i < checkBoxNames.length / 2; i++)
		{
			boxes.add(new JCheckBox(checkBoxNames[i]));
			if (ts.contains(checkBoxNames[i]))
			{
				boxes.elementAt(i).setSelected(true);
			}
			checkBoxes_1.add(boxes.elementAt(i));
			gbl.setConstraints(boxes.elementAt(i), gbc);
		}

		for (int i = checkBoxNames.length / 2; i < checkBoxNames.length; i++)
		{
			boxes.add(new JCheckBox(checkBoxNames[i]));
			if (ts.contains(checkBoxNames[i]))
			{
				boxes.elementAt(i).setSelected(true);
			}
			checkBoxes_2.add(boxes.elementAt(i));
			gbl.setConstraints(boxes.elementAt(i), gbc);
		}

		checkBoxFull.add(checkBoxes_1);
		checkBoxFull.add(checkBoxes_2);
		return checkBoxFull;
	}



	private TreeSet<Object> getColumns()
	{
		TreeSet<Object> ts = new TreeSet<Object>();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
		{
			ts.add(table.getColumnModel().getColumn(i).getHeaderValue());
		}
		return ts;
	}



	public void actionPerformed(ActionEvent ae)
	{
		Object bp = ae.getSource();
		if (bp == filter)
		{
			String statement = "SELECT";
			Iterator<JCheckBox> i = boxes.iterator();
			while (i.hasNext())
			{
				JCheckBox jcb = i.next();
				statement += (jcb.isSelected() ? (" \"" + jcb.getText() + "\",") : "");
			}
			statement = statement.substring(0, statement.lastIndexOf(","));
			statement += " FROM charmlist";

			cgui.makeTable(statement);

			setVisible(false);
			dispose();
		}
		else if (bp == all)
		{
			for (int i = 0; i < boxes.size(); i++)
			{
				boxes.elementAt(i).setSelected(true);
			}
		}
		else if (bp == none)
		{
			for (int i = 0; i < boxes.size(); i++)
			{
				boxes.elementAt(i).setSelected(false);
			}
		}
	}
}
