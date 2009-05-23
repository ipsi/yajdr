package yajdr.gui.print;

import javax.swing.JTable;

public class PageAttributes
{
	private int		fontSize		= -1;
	private int		charmsPerPage	= -1;
	private int		charmsPerRow	= -1;
	private int		charmsPerColumn	= -1;
	private String	fontLocation	= null;
	private JTable	table			= null;



	/**
	 * @param fontSize
	 *            the fontSize to set
	 */
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}



	/**
	 * @return the fontSize
	 */
	public int getFontSize()
	{
		return fontSize;
	}



	/**
	 * @param charmsPerPage
	 *            the charmsPerPage to set
	 */
	public void setCharmsPerPage(int charmsPerPage)
	{
		this.charmsPerPage = charmsPerPage;
	}



	/**
	 * @return the charmsPerPage
	 */
	public int getCharmsPerPage()
	{
		return charmsPerPage;
	}



	/**
	 * @param charmsPerRow
	 *            the charmsPerRow to set
	 */
	public void setCharmsPerRow(int charmsPerRow)
	{
		this.charmsPerRow = charmsPerRow;
	}



	/**
	 * @return the charmsPerRow
	 */
	public int getCharmsPerRow()
	{
		return charmsPerRow;
	}



	/**
	 * @param charmsPerColumn
	 *            the charmsPerColumn to set
	 */
	public void setCharmsPerColumn(int charmsPerColumn)
	{
		this.charmsPerColumn = charmsPerColumn;
	}



	/**
	 * @return the charmsPerColumn
	 */
	public int getCharmsPerColumn()
	{
		return charmsPerColumn;
	}



	/**
	 * @param fontLocation
	 *            the fontLocation to set
	 */
	public void setFontLocation(String fontLocation)
	{
		this.fontLocation = fontLocation;
	}



	/**
	 * @return the fontLocation
	 */
	public String getFontLocation()
	{
		return fontLocation;
	}



	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(JTable table)
	{
		this.table = table;
	}



	/**
	 * @return the table
	 */
	public JTable getTable()
	{
		return table;
	}
}
