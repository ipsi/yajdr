package yajdr.gui.charmgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedString;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import yajdr.core.DieRollerMain;
import yajdr.gui.charmtree.Charm;

/**
 * This class creates a pop-up window for a charm. Currently, it requires that a table and row
 * number are passed to the constructor, which is the only method present in this class. The frame
 * is layed out with GridBagConstraints. The order of the columns in the table is irrelevant, with
 * the exception that the Description field will always be the bottom box in the window.
 * <p>
 * The names of the columns will be used as titles for each textbox located in the window, with the
 * exception that the Minimum Ability column will be replaced with a column name of the form Minimum
 * *A* where *A* is the ability of the charm on this row. In the event that Minimum Ability or
 * Ability are not present, the other will be used as is.
 * <p>
 * The name of the charm is the name of the window, except in the event where the Name column is not
 * present, where it will be "Name Unknown"
 */
public class CharmView extends JFrame
{

	private static final long	serialVersionUID	= -4046381269003783373L;

	private JTextField[]		fields				= null;

	private String[]			labelNames			= null;



	public CharmView(JTable table, int row)
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		int columns = table.getColumnCount();

		int descIndex = -1;
		int nameIndex = -1;
		int abIndex = -1;
		int minAbIndex = -1;

		GridBagLayout gbl = new GridBagLayout();
		getContentPane().setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;

		labelNames = new String[columns];
		fields = new JTextField[columns];

		for (int i = 0; i < columns; i++)
		{
			labelNames[i] = (String) table.getColumnModel().getColumn(i).getHeaderValue();

			// Because we have special rules for dealing with Fields with these names, we'll get
			// their index now
			// and save it for later use.
			if (labelNames[i].equalsIgnoreCase("Description"))
			{
				descIndex = i;
			}
			else if (labelNames[i].equalsIgnoreCase("Name"))
			{
				nameIndex = i;
			}
			else if (labelNames[i].equalsIgnoreCase("Ability"))
			{
				abIndex = i;
			}
			else if (labelNames[i].equalsIgnoreCase("Minimum Ability"))
			{
				minAbIndex = i;
			}
		}

		for (int i = 0; i < columns; i++)
		{
			// Special Behaviour for dealing with Ability Name and Description,
			// so skip them this time round. (But only skip Ability Name if
			// the Minimum Ability has not been filtered out.)
			if (i == descIndex || (i == abIndex && minAbIndex != -1))
			{
				continue;
			}

			fields[i] = new JTextField();
			fields[i].setMaximumSize(new Dimension(200, 10));
			fields[i].setEditable(false);

			// If this is run deals with the Minimum Ability, and the Ability Name is present,
			// use Minimum *Ability Name* rather than Minimum Ability. Otherwise, default.
			if (i == minAbIndex && abIndex != -1)
			{
				String s = labelNames[i].substring(0, labelNames[i].indexOf(" "));
				s += " " + table.getValueAt(row, abIndex).toString();
				fields[i].setBorder(BorderFactory.createTitledBorder(s));
			}
			else
			{
				fields[i].setBorder(BorderFactory.createTitledBorder(labelNames[i]));
			}

			// Yoink the text from the table, and set background for this component to white.
			fields[i].setText((table.getValueAt(row, i).toString()).replace(';', ',').replace('`', '\''));
			fields[i].setBackground(Color.WHITE);

			// Set GridBagConstraints for this Component
			getContentPane().add(fields[i]);
			gbl.setConstraints(fields[i], gbc);
		}

		pack();
		int width = getSize().width;
		int descRows = 0;

		// Special Behaviour for dealing with the Description field. Make it the last one
		// in the frame, put it into a scrollpane, and make it so that it will take up all extra
		// room.
		if (descIndex != -1)
		{
			JTextArea desc = new JTextArea();
			desc.setText(((String) table.getValueAt(row, descIndex)).replaceAll("`", "'"));
			desc.setCaretPosition(0);
			desc.setEditable(false);
			desc.setLineWrap(true);
			desc.setWrapStyleWord(true);
			desc.setMaximumSize(new Dimension(width, desc.getMaximumSize().width));

			JScrollPane jsp = new JScrollPane(desc);
			jsp.setBackground(Color.WHITE);
			jsp.setBorder(BorderFactory.createTitledBorder(labelNames[descIndex]));

			gbc.fill = GridBagConstraints.BOTH;
			gbc.weighty = 1.0;
			gbc.weightx = 0.0;
			gbl.setConstraints(jsp, gbc);
			getContentPane().add(jsp);

			AttributedString atString = new AttributedString(desc.getText());
			LineBreakMeasurer lbm = new LineBreakMeasurer(atString.getIterator(), new FontRenderContext(null, true, true));
			Object o = lbm.nextLayout(width);
			descRows++;
			while (o != null)
			{
				descRows++;
				o = lbm.nextLayout(width);
			}
		}

		// If we know the name, set it as the title of the frame.
		if (nameIndex != -1)
		{
			setTitle(((String) table.getValueAt(row, nameIndex)).replace('`', '\''));
		}
		else
		{
			setTitle("Name Unknown");
		}

		// Description doesn't like packing, so change the size slightly here.
		if (descIndex != -1)
		{
			pack();
			setSize(width + 10, getSize().height + (15 * descRows));
		}

		// Centre frame on Screen.
		setLocation(DieRollerMain.centreOnScreen(getSize()));
		setVisible(true);
	}



	public CharmView(Charm c)
	{
		this(new JTable(CharmView.getRowData(c), c.getColumns()), 0);
	}



	private static Object[][] getRowData(Charm c)
	{
		Object[][] o = new Object[1][c.getAttribs().length];
		for (int i = 0; i < c.getAttribs().length; i++)
		{
			o[0][i] = c.getAttribs()[i];
		}
		return o;
	}
}
