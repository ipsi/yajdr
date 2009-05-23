package yajdr.gui.charmgui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import yajdr.core.DieRollerMain;
import yajdr.gui.CharmGUI;


/**
 * This class deals with searching the database, and putting those results into the table in the
 * main application. Seperate each item you want to search for with ',' or ' ' (Without the quotes).
 * Entering values in the AND field will require that the column match *all* those values.
 * Obviously, selecting anything other than Contains would be a silly idea if you enter more than
 * one search term. Entering values in the OR column will require that the column match *any* of
 * those values. Works fine with any of the four options. Using OR and AND will require that column
 * match <b>either</b> <i>all</i> values in the AND box, <b>or</b> <i>any</i> of the values in the
 * OR box. Entering values in the NOT box will simply ensure that the column will not, depending on
 * what you select, Start with, Contain, End with, or Equal the values in the box. Entering a value
 * in the AND/OR box and the same value in the NOT box is probably not a good idea. Not sure what
 * will happen.
 */
public class Search extends JFrame implements ActionListener
{

	private static final long	serialVersionUID	= 4696621402788248121L;

	// Containers
	private JTabbedPane			main				= new JTabbedPane();

	private JToolBar			toolBar				= new JToolBar();

	// Contains a set of Panels. Vector for ease of use.
	private Vector<SearchPanel>	panels				= new Vector<SearchPanel>();

	// Table used to determine column headings.
	private JTable				table;

	// Searches all columns (Takes data from all tabs)
	private JButton				searchAll			= new JButton("Search All");

	// Need this to tell the normal CharmGUI that we've finished, and to update the table.
	private CharmGUI			cgui;



	/**
	 * Constructs the frame. Most of the work is done by other methods. This just calls them. The
	 * frame is layed out via GridBagLayout.
	 * 
	 * @param t
	 *            JTable Table we get the tab names from.
	 * @param cgui
	 *            CharmGUI Object which will actually execute the query and construct the table.
	 */
	public Search(JTable t, CharmGUI cgui)
	{
		table = t;
		this.cgui = cgui;
		for (int i = 0; i < t.getColumnCount(); i++)
		{
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();

			SearchPanel panel = new SearchPanel(gbl, gbc, this);
			panel.setName((String) table.getColumnModel().getColumn(i).getHeaderValue());
			panels.add(panel);
			main.addTab((String) table.getColumnModel().getColumn(i).getHeaderValue(), get(i));
		}
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(main, BorderLayout.CENTER);

		searchAll.addActionListener(this);
		toolBar.add(searchAll);

		pack();
		setTitle("Search");
		setLocation(DieRollerMain.centreOnScreen(getSize()));
		setVisible(true);
	}



	/**
	 * Deals with what happens when we press a button. If the button was Search All, then each
	 * column will be searched, the text extracted from its text boxes, and all plugged together
	 * into a coherent SQL statement, which will then be passed back to the CharmGUI and executed.
	 * If the button was Search This Column Only, then the program will look for the tab where the
	 * button came from. When it finds it, it will extract all necessary data from, deal with it
	 * similarily.
	 * 
	 * @param ae
	 *            ActionEvent
	 */
	public void actionPerformed(ActionEvent ae)
	{
		Object bp = ae.getSource();
		if (bp == searchAll)
		{
			// This could probably be rebuilt so that it only selects Columns which are present in
			// the table.
			// Also, I could probably simplify this method so that both buttons use most of the same
			// code.
			String statement = "SELECT * FROM charmlist WHERE";
			int l = statement.length();

			for (int i = 0; i < panels.size(); i++)
			{
				statement += getStatement(get(i));
			}
			System.out.println(statement);
			if (statement.length() == l)
			{
				statement = statement.substring(0, statement.lastIndexOf(" "));
			}
			else
			{
				statement = statement.substring(0, statement.lastIndexOf(" AND "));
			}

			search(statement);
		}
		else
		{
			String statement = "SELECT * FROM charmlist WHERE";
			int l = statement.length();

			for (int i = 0; i < panels.size(); i++)
			{
				if (containsButton(get(i), bp))
				{
					statement += getStatement(get(i));
					if (statement.length() > l)
					{
						statement = statement.substring(0, statement.lastIndexOf(" AND "));
					}
					else
					{
						statement = statement.substring(0, statement.lastIndexOf(" "));
					}
					search(statement);

					return;
				}
			}
		}
	}



	/**
	 * This performs most of the work in terms of preparing the statement. It takes a panel, goes
	 * through it, and takes the text from each box. Once that's done, it will then tokenize each
	 * string (Delims ',' and ' '), add to each token the necessary SQL Syntax (i.e. AND Name LIKE
	 * '%Token1%', or OR Name LIKE 'Token2%' or AND Name NOT LIKE '%Token3', etc.) If none of the
	 * textfields contained anything, this will return a string of length 0. Otherwise, it will be
	 * prefixed with the initial part of this statement, and appended with AND and then returned to
	 * actionPerformed.
	 * 
	 * @param panel
	 *            SearchPanel Panel containing statement data
	 * @return String Statement
	 */
	private String getStatement(SearchPanel panel)
	{
		String initial = " " + panel.getName() + " LIKE ";
		String statement = "";

		// Need to know if it's Begins with, Ends with, Contains or Equals
		String radio = panel.getSelectedRadioButton();
		if (radio == null)
		{
			throw new RuntimeException("no selected radio button");
		}

		// Do this all now, reasons obvious shortly.
		String and = panel.getText("AND");
		String or = panel.getText("OR");
		String not = panel.getText("NOT");

		// If all the lengths are 0, then none contain any useful information, and we don't need to
		// proceed.
		if (not.length() == 0 && or.length() == 0 && and.length() == 0)
		{
			return "";
		}

		// Our very good friend.
		StringTokenizer st;

		// If just this one contains nothing, then we don't need to do anything tricky with it.
		if (and.length() > 0)
		{
			// Tokenize the string.
			st = new StringTokenizer(and, ", ");

			// Do some funny stuff here. See append(String, String, String, StringTokenizer);
			statement = append(statement, radio, "AND" + initial, st);

			// Because of how this works, we'll likely have an extra AND on the end. I can probably
			// get rid of this
			// now, but it'll stay, just in case.
			if (statement.lastIndexOf("AND") > -1)
			{
				statement = statement.substring(0, statement.lastIndexOf("AND"));
			}
		}

		// If or + and are not empty, then we append OR *n* LIKE to the statement, where *n* is the
		// current column
		// name. If and is length 0, then this will be dealt with later. Notice we haven't actually
		// prefixed AND with
		// anything yet have we?
		if (or.length() > 0 && and.length() > 0)
		{
			statement += "OR" + initial;
		}
		// If AND + OR are both length 0, then NOT is the only one left. Dealing with SQL structure
		// because of that.
		else if (not.length() > 0 && and.length() == 0 && or.length() == 0)
		{
			initial = " " + panel.getName() + " NOT LIKE ";
		}
		// Finally, if AND is non-empty but OR is, then append like so. We'll deal with the remaing
		// cases with OR,
		// because we know OR must be > 0.
		else if (not.length() > 0 && or.length() == 0)
		{
			statement += " AND " + panel.getName() + " NOT LIKE ";
		}

		// If OR is non-empty,
		if (or.length() > 0)
		{
			// Tokenize it,
			st = new StringTokenizer(or, ", ");

			// Append stuff to it.
			statement = append(statement, radio, "OR" + initial, st);
			// If we have an extra OR floating about. And we do,
			if (statement.lastIndexOf("OR") > -1)
			{
				// Get rid of it.
				statement = statement.substring(0, statement.lastIndexOf("OR"));
			}
			// Finally, if NOT is non-empty, append like so:
			if (not.length() > 0)
			{
				statement += "AND " + panel.getName() + " NOT LIKE ";
			}
		}

		// If NOT is non-empty,
		if (not.length() > 0)
		{
			// Tokenize it,
			st = new StringTokenizer(not, ", ");

			// And append stuff to it.
			statement = append(statement, radio, "AND " + panel.getName() + " NOT LIKE ", st);
			// If we have an extra NOT floating around. And we do,
			if (statement.lastIndexOf("AND " + panel.getName() + " NOT LIKE ") > -1)
			{
				// Get rid of it.
				statement = statement.substring(0, statement.lastIndexOf("AND " + panel.getName() + " NOT LIKE "));
			}
		}

		// Complete the statement.
		statement = initial + statement + " AND ";

		// Return it.
		return statement;
	}



	/**
	 * This calls the "makeTable" method in CharmGUI, to make and insert the table in the GUI.
	 * <code>statement</code> is a properly structured SQL statement that contains all search terms.
	 * 
	 * @param statement
	 *            String This is the statement that will be executed.
	 */
	private void search(String statement)
	{
		// System.out.println(statement);
		cgui.makeTable(statement);
	}



	/**
	 * This method takes a statement, and, depending on what the user wants to do, will append "%"
	 * to the beginning and/or end of the statement. It will then append <code>extra</code> to the
	 * back of the statement. <code>extra</code> is usually something like " AND "Name" LIKE ",
	 * " OR "Ability" LIKE "
	 * 
	 * @param statement
	 *            String This is the statement so far.
	 * @param radio
	 *            String Which radio button is selected (Contains, Begins with, etc)
	 * @param extra
	 *            String Stuff to append to the statement
	 * @param st
	 *            StringTokenizer Contains all elements to be processed.
	 * @return String The fully structured statement for the current panel.
	 */
	private String append(String statement, String radio, String extra, StringTokenizer st)
	{
		while (st.hasMoreTokens())
		{
			if (radio.equalsIgnoreCase("Ends With") || radio.equalsIgnoreCase("Contains"))
			{
				statement += "'%";
			}
			else
			{
				statement += "'";
			}

			statement += st.nextToken();

			if (radio.equalsIgnoreCase("Begins With") || radio.equalsIgnoreCase("Contains"))
			{
				statement += "%' " + extra;
			}
			else
			{
				statement += "' " + extra;
			}
		}
		return statement;
	}



	/**
	 * Returns the panel at the given index. The index is the order they appear on screen.
	 * 
	 * @param i
	 *            int Index of the panel to get
	 * @return SearchPanel SearchPanel at index i in the frame.
	 */
	private SearchPanel get(int i)
	{
		return panels.get(i);
	}



	/**
	 * This method is called when the button pushed was not search all, but rather
	 * "Search This column", and this searches through the panel, and asks if the panel contains the
	 * button that generated the <code>ActionEvent</code>
	 * 
	 * @param panel
	 *            JPanel Panel to search
	 * @param button
	 *            Object Button we want to know if <code>panel</code> contains.
	 * @return boolean Is true if <code>panel</code> contains <code>button</code>
	 */
	private boolean containsButton(JPanel panel, Object button)
	{
		for (int j = 0; j < panel.getComponentCount(); j++)
		{
			if (panel.getComponent(j).equals(button))
			{
				return true;
			}
		}
		return false;
	}



	/**
	 * <b>SearchPanel</b>
	 * <p>
	 * This is the class for each tab you see in the search frame. To be honest, there isn't much to
	 * say about this, as everything that deals with it is described in Search.
	 * 
	 * @see yajdr.gui.charmgui.Search
	 */
	private class SearchPanel extends JPanel
	{

		private static final long			serialVersionUID	= 2236865408332906339L;

		private Vector<JRadioButton>		radios				= new Vector<JRadioButton>();

		private TreeMap<String, JTextField>	texts				= new TreeMap<String, JTextField>();



		public SearchPanel(GridBagLayout gbl, GridBagConstraints gbc, Search s)
		{
			super(gbl);
			ButtonGroup bg = new ButtonGroup();

			String[] buttonNames = { "Begins With", "Ends With", "Contains", "Equals" };
			String[] textNames = { "AND", "OR", "NOT" };

			gbc.anchor = GridBagConstraints.NORTH;
			gbc.weightx = 1.0;
			gbc.gridx = GridBagConstraints.RELATIVE;

			for (int i = 0; i < buttonNames.length; i++)
			{
				JRadioButton jrb = new JRadioButton(buttonNames[i]);
				bg.add(jrb);
				radios.add(jrb);

				if (i == buttonNames.length - 1)
				{
					gbc.gridwidth = GridBagConstraints.REMAINDER;
					gbl.setConstraints(jrb, gbc);
				}
				else
				{
					gbl.setConstraints(jrb, gbc);
				}

				if (i == 2)
				{
					jrb.setSelected(true);
				}
				add(jrb);
			}

			gbc.anchor = GridBagConstraints.WEST;

			for (int i = 0; i < textNames.length; i++)
			{
				gbc.gridx = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.NONE;

				JLabel lab = new JLabel(textNames[i]);
				gbl.setConstraints(lab, gbc);
				add(lab);

				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.fill = GridBagConstraints.HORIZONTAL;

				JTextField jtf = new JTextField();
				texts.put(lab.getText(), jtf);
				gbl.setConstraints(jtf, gbc);
				add(jtf);
			}

			gbc.anchor = GridBagConstraints.SOUTH;
			// gbc.fill = GridBagConstraints.NONE;
			gbc.gridheight = GridBagConstraints.REMAINDER;

			JButton button = new JButton("Search this Column only");
			button.addActionListener(s);
			gbl.setConstraints(button, gbc);
			add(button);
		}



		public String getSelectedRadioButton()
		{
			for (int i = 0; i < radios.size(); i++)
			{
				if (radios.get(i).isSelected())
				{
					return radios.get(i).getText();
				}
			}
			return null;
		}



		public String getText(String s)
		{
			return texts.get(s).getText();
		}
	}
}
