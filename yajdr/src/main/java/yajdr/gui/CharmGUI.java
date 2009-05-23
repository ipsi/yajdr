package yajdr.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import yajdr.gui.charmgui.CharmView;
import yajdr.gui.charmgui.Filter;
import yajdr.gui.charmgui.Search;
import yajdr.gui.charmtree.BaseCharmNode;
import yajdr.gui.print.PrintCharm;

import com.mckoi.jfccontrols.ResultSetTableModel;


public class CharmGUI extends JPanel implements ActionListener
{

	private static final Logger	logger				= Logger.getLogger(CharmGUI.class);

	private static final long	serialVersionUID	= 3150676999527647890L;

	// JToolBar and Button Data fields
	private JToolBar			toolBar				= new JToolBar();

	private JToolBar			tb2					= new JToolBar();

	// toolBar
	private JButton				filter				= new JButton("Filter");

	private JButton				resetFilter			= new JButton("Reset Filter");

	private JButton				search				= new JButton("Search");

	private JButton				popUp				= new JButton("Float Charm");

	private JButton				print				= new JButton("Print Selected");

	private JButton				pageSetup			= new JButton("Page Setup");

	private JButton				charmTree			= new JButton("Charm Tree");

	private JComboBox			abilities			= new JComboBox(new String[] { "Archery", "Brawl", "Martial Arts", "Melee" });

	// tb2
	private JButton				selectedRowsTree	= new JButton("Construct Tree from Selected Rows");

	// Table
	private JTable				table				= makeDefaultTable();

	private JScrollPane			jsp					= new JScrollPane(table);

	// Utility
	private Connection			con;

	private String[]			checkBoxNames		= { "Name", "Cost", "Duration", "Type", "Ability", "Minimum Ability", "Minimum Essence", "Prerequisite Charms", "Description", "Page Ref" };

	private Component			strut				= Box.createHorizontalStrut(10);

	// Statement Vars
	// Not used yet. Reserved for future use.
	private String				lastSelect			= "SELECT * FROM charmlist";

	private PrintCharm			printer;

	/*
	 * Starts up the database. Had some trouble with this during testing of the first release.
	 */
	static
	{
		try
		{
			Class.forName("com.mckoi.JDBCDriver").newInstance();
		}
		catch (InstantiationException e)
		{
			logger.error("InstantiationException on loading Mckoi JDBC Driver", e);
		}
		catch (IllegalAccessException e)
		{
			logger.error("IllegalAccessException on loading Mckoi JDBC Driver", e);
		}
		catch (ClassNotFoundException e)
		{
			logger.error("ClassNotFoundException on loading Mckoi JDBC Driver", e);
		}
	}



	/**
	 * Creates the table and the toolbar. Nothing special in here. Most of the hard work is done by
	 * utility methods. This class subclasses JPanel, so is a bit less useful if you just want the
	 * DB by itself.
	 */
	public CharmGUI()
	{
		getConnection();
		// updateDataBase(false);
		setLayout(new BorderLayout());
		toolBar.setFloatable(false);

		makeButton(filter, toolBar, this);
		toolBar.add(strut);

		makeButton(resetFilter, toolBar, this);
		toolBar.add(strut);

		makeButton(search, toolBar, this);
		toolBar.add(strut);

		makeButton(popUp, toolBar, this);
		toolBar.add(strut);

		makeButton(print, toolBar, this);
		toolBar.add(strut);

		makeButton(pageSetup, toolBar, this);
		toolBar.add(strut);

		makeAbilities();
		toolBar.add(abilities);

		makeButton(charmTree, toolBar, this);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel panel = new JPanel(gbl);

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;

		gbl.setConstraints(toolBar, gbc);
		panel.add(toolBar);

		makeButton(selectedRowsTree, tb2, this);
		tb2.add(strut);

		gbl.setConstraints(tb2, gbc);
		panel.add(tb2);

		add(panel, BorderLayout.NORTH);

		table = makeTable();
		printer = new PrintCharm(table);
		insertTable(table);

		// drawCharms();
	}



	private Object[] makeAbilities()
	{
		try
		{
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT \"Ability\" FROM charmlist");

			TreeSet<String> set = new TreeSet<String>();
			while (rs.next())
			{
				set.add(rs.getString(1));
			}

			abilities = new JComboBox(set.toArray());
		}
		catch (SQLException e)
		{
			logger.error("There was an error trying to select known abilities from the charm list...", e);
		}

		return null;
	}



	private void drawCharms()
	{
		String ability = (String) abilities.getItemAt(abilities.getSelectedIndex());
		String state = "SELECT * FROM charmlist WHERE \"Ability\" LIKE '" + ability + "'";
		try
		{
			getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(state);
			BaseCharmNode bcn = new BaseCharmNode(makeTable(rs));
			bcn.buildCharmTree();
		}
		catch (SQLException e)
		{
			logger.error("There was an error selecting ability [" + ability + "] from the database...", e);
		}

	}



	/**
	 * Makes the buttons you see on the toolbar. Doesn't actually return anything, merely adds
	 * <code>al</code> to the button, and puts the button in <code>cont</code>.
	 * 
	 * @param b
	 *            JButton The button to make
	 * @param cont
	 *            Container The container to add the button to.
	 * @param al
	 *            ActionListener The actionListener to add to the button.
	 */
	private void makeButton(JButton b, Container cont, ActionListener al)
	{
		cont.add(b);
		b.addActionListener(al);
	}



	/**
	 * Tells the program what to do when a button is pressed. There is a response for each button.
	 * If the button was Float Charm, then, for each charm selected, a new CharmView object will be
	 * created. If the button was Filter, it will create a new Filter object. If the button was
	 * Reset Filter, then it will call insertTable, passing it the complete dataset called from the
	 * database. Finally, if the button was Search, then it will create a new Search object.
	 * 
	 * @param ae
	 *            ActionEvent Contains information about the event.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		Object bp = ae.getSource();

		if (bp == popUp)
		{
			for (int i = 0; i < table.getSelectedRows().length; i++)
			{
				new CharmView(table, table.getSelectedRows()[i]);
			}
		}
		else if (bp == filter)
		{
			new Filter(table, this, checkBoxNames);
		}
		else if (bp == resetFilter)
		{
			insertTable(makeTable());
		}
		else if (bp == search)
		{
			new Search(table, this);
		}
		else if (bp == print)
		{
			printer.print();
		}
		else if (bp == pageSetup)
		{
			printer.pageSetup();
		}
		else if (bp == charmTree)
		{
			drawCharms();
		}
		else if (bp == selectedRowsTree)
		{
			multiRowTree();
		}
	}



	public int getRowCount()
	{
		return table.getRowCount();
	}



	/**
	 * This will create a table by executing Statement on the database, and putting the resultant
	 * dataset into a table, and displaying that table. If there is an error somewhere along the
	 * line, then a default, two row table will be inserted instead.
	 * 
	 * @param statement
	 *            String A properly structured SQL statement.
	 */
	public void makeTable(String statement)
	{
		try
		{
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(statement);
			lastSelect = statement;
			insertTable(makeTable(rs));
		}
		catch (SQLException e)
		{
			logger.debug("There was an error selecting all data from the database...", e);
		}
	}



	/**
	 * Makes a table by executing a default Query on the database. (Usually SELECT * FROM
	 * charmlist). If there is an error, a default, two row table will be returned instead.
	 * 
	 * @return JTable The table made from the dataset returned by the query.
	 */
	private JTable makeTable()
	{
		try
		{
			Statement s = con.createStatement();
			String statement = "SELECT * FROM charmlist ORDER BY \"Ability\"";
			ResultSet rs = s.executeQuery(statement);
			lastSelect = statement;
			return makeTable(rs);
		}
		catch (SQLException e)
		{
			logger.debug("There was an error selecting all data from the charmlist table...", e);
			return makeDefaultTable();
		}
	}



	/**
	 * Takes a result set and builds a table from it.
	 * 
	 * @param rs
	 *            ResultSet The dataset obtained by querying the database.
	 * @return JTable The table built from rs.
	 */
	private JTable makeTable(ResultSet rs)
	{

		ResultSetTableModel table_model = new ResultSetTableModel(rs);
		table_model.setPreserveTableStructure(true);
		JTable tb = new JTable(table_model);
		return tb;

	}



	/**
	 * Inserts the current table into the panel. Removes the current one first, future versions may
	 * include the ability to handle multiple tables at once, but not this one. In any case, it adds
	 * the table to the a scrollpane, and then puts the scrollpane into the centre of the panel.
	 * 
	 * @param t
	 *            JTable The table to insert into the panel.
	 */
	private void insertTable(JTable t)
	{
		remove(jsp);
		setVisible(false);
		setVisible(true);
		table = t;
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// Doesn't do what I want. Set to false.
		table.setColumnSelectionAllowed(false);
		table.getTableHeader().setReorderingAllowed(true);
		table.getTableHeader().setResizingAllowed(true);

		jsp = new JScrollPane(table);
		add(jsp, BorderLayout.CENTER);
	}



	/**
	 * Makes a default table, containing two charms: Seasoned Criminal Method, straight from the
	 * core book, and Eh?, which isn't a real charm, oddly enough. If these are the only two charms
	 * present, then the logic has fouled up somehow, and you need to go hunting for error messages.
	 * Also does not contain all columns present in the Database.
	 * 
	 * @return JTable
	 */
	private JTable makeDefaultTable()
	{
		Object[][] temp = {
				{ "Seasoned Criminal Method", "10 Motes", "Simple", "One Day", "3", "1",
						"This Charm grants a character preternatural intuition with regard to criminal subcultures. " + "While under the influence of this Charm, he can easily pick out criminal establishments " + "� pawnshops willing to operate as fences, taverns that are thieves� havens and so on. " + "Likewise, the character can easily pick out those who are actively interested in buying " + "or selling illegal goods and services � he can spot police and officials who " + "will accept bribes and individuals interested in selling or buying " + "drugs, sex or information. Finally, the character using this " + "Charm can easily read lines of power, differentiating important " + "organized crime figures from small-time operators and quickly " + "tracking down the true thrones and powers of the local underworld. " + "In short, characters with this Charm are at home in any " + "criminal subculture. This Charm doesn�t grant the ability to " + "spot agents provocateurs and informers." },
				{ "Eh?", "Why?", "Come on...", "Again?", "2", "12", "None" } };
		Object[] temp2 = { "Charm", "Cost", "Type", "Duration", "Minimum Skill", "Minimum Essence", "Description" };
		JTable j = new JTable(temp, temp2);
		j.setRowSelectionInterval(0, 0);
		return j;
	}



	/**
	 * Inserts data into the Database. Will read it in from a file, currently hardcoded, and insert
	 * the values into the DB. Works by tokenizing a line of text from a file, and handing that into
	 * the DB. Delim's for the tokenizer are \t. Not spaces. Note this method is not actually used
	 * in the class.
	 * 
	 * @param doOnce
	 *            boolean Not used. Was used for something, once.
	 */
	@SuppressWarnings("unused")
	private void updateDataBase(boolean doOnce)
	{
		try
		{
			// logger.debug("Got here");
			Statement s = con.createStatement();

			s.executeQuery("DELETE FROM charmlist");

			// Create Filereader.
			FileReader instream = new FileReader(new File("c:\\test.txt"));
			BufferedReader ins = new BufferedReader(instream);
			// String baseState =
			// "INSERT INTO charmlist (\"Name\", \"Cost\", \"Duration\", \"Type\", \"Ability\", \"Minimum Ability\", \"Minimum Essence\", \"Prerequisite Charms\", \"Description\", \"Page Ref\") VALUES (";
			String baseState = "INSERT INTO charmlist VALUES (";

			while (ins.ready())
			{
				String str = ins.readLine();
				StringTokenizer st = new StringTokenizer(str, "\t");

				while (st.hasMoreElements())
				{
					baseState += "\'" + st.nextToken() + "\', ";
				}

				try
				{
					s.execute(baseState.substring(0, baseState.lastIndexOf(",")) + ")");
				}
				catch (SQLException e)
				{
					logger.error("Error while trying to insert single row into table...", e);
				}

				baseState = "INSERT INTO charmlist VALUES (";
			}
			ins.close();
		}
		catch (SQLException e)
		{
			logger.error("There was an error updating the table...", e);
		}
		catch (IOException e)
		{
			logger.error("There was an error updating the table...", e);
		}
	}



	/**
	 * Attempts to get a connection to the Database. Why on earth do I check if a file exists before
	 * connecting? This is because when I'm programming this, everything is spread all over the
	 * place, and so the db.conf file is not located where the core of the program is. Oops. The
	 * "else" bit is for the distributed version of the application.
	 */
	private void getConnection()
	{
		File tmp = new File("./db.conf");
		try
		{
			logger.debug("db.conf file: " + tmp.getCanonicalPath());
			con = DriverManager.getConnection("jdbc:mckoi:local://db.conf", "ipsi", "brujah12I");
		}
		catch (SQLException e)
		{
			logger.error("Caught SQLException trying to get a connection to the DB!", e);
		}
		catch (IOException e)
		{
			logger.debug("Caught IOException trying to get CanonicalPath for db.conf file...", e);
		}
	}



	private void multiRowTree()
	{
		int[] rows = table.getSelectedRows();

		if (rows == null || rows.length == 0)
		{
			logger.debug("rows == null: " + (rows == null));
			if(rows != null)
				logger.debug("rows.lengh == 0: " + (rows.length == 0));
			return;
		}

		ResultSet rs = null;

		try
		{
			getConnection();
			rs = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(lastSelect);
		}
		catch (SQLException e)
		{
			logger.error("Caught error trying to select sql [" + lastSelect + "] from the database...", e);
		}

		new BaseCharmNode(new JTable(new SelectRowTableModel(rs, table.getSelectedRows()))).buildCharmTree();
	}



	private class SelectRowTableModel extends ResultSetTableModel
	{

		private Logger logger = Logger.getLogger(SelectRowTableModel.class);
		
		private static final long	serialVersionUID	= -8688977238130301144L;

		private ResultSet			rs;

		private int[]				rows;



		public SelectRowTableModel(ResultSet rs, int[] rows)
		{
			super(rs);
			this.rs = rs;
			this.rows = rows;
		}



		public int getRowCount()
		{
			return rows.length;
		}



		public Object getValueAt(int row, int column)
		{
			try
			{
				rs.absolute(rows[row] + 1);
				return rs.getObject(column + 1);
			}
			catch (SQLException e)
			{
				logger.error("Caught exception trying to get value from ResultSet...", e);
			}
			return null;
		}
	}
}
