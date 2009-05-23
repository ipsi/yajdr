package yajdr.gui.print;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileInputStream;
import java.text.AttributedString;

public class Document implements Printable
{

	private Rectangle		rect;

	private Font			times		= null;

	private Font			timesBold	= null;

	private PageAttributes	attribs		= null;



	public Document(PageAttributes attribs)
	{
		this.attribs = attribs;
	}



	public int print(Graphics g, PageFormat pf, int index)
	{
		Graphics2D g2d = (Graphics2D) g;

		g2d.translate(pf.getImageableX(), pf.getImageableY());
		setDimensions(pf);

		createFonts(attribs.getFontSize());

		int[] rows = attribs.getTable().getSelectedRows();

		g2d.setStroke(new BasicStroke(2));

		for (int i = 0 + (attribs.getCharmsPerPage() * index); i < rows.length; i++)
		{
			if (i == (attribs.getCharmsPerPage() + attribs.getCharmsPerPage() * index))
			{
				return PAGE_EXISTS;
			}

			double di = i - (attribs.getCharmsPerPage() * index);
			checkRowEnd(di);

			Rectangle2D.Double border = new Rectangle2D.Double(rect.x, rect.y, rect.width + 4, rect.height);

			// Ensuring border does not intersect with text
			rect.x += 4;
			rect.y += 12;
			g2d.draw(border);

			drawStrings(rows[i], g2d, attribs.getFontSize());

			// Moving the pointer across the width of the charm card, and then adding space of 6
			// pts between
			// it and the next charm.
			rect.x += rect.width + 6;
		}
		return PAGE_EXISTS;
	}



	private void createFonts(int fontSize)
	{
		try
		{
			File f = new File(attribs.getFontLocation() + "\\times.ttf");
			times = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(f)).deriveFont((float) fontSize);

			f = new File(attribs.getFontLocation() + "\\timesbd.ttf");
			timesBold = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(f)).deriveFont((float) fontSize);
		}
		catch (Exception ex)
		{
			System.err.println("Could not create all fonts.");
			times = null;
			timesBold = null;
		}
	}



	private void setDimensions(PageFormat pf)
	{
		int height = (int) (Math.floor(pf.getImageableHeight() / attribs.getCharmsPerColumn())) - 8;
		int width = (int) (Math.floor(pf.getImageableWidth() / attribs.getCharmsPerRow())) - 18;
		int y = 2;
		int x = 2;

		rect = new Rectangle(x, y, width, height);
	}



	private void drawStrings(int row, Graphics2D g2d, int fontSize)
	{
		for (int j = 0; j < attribs.getTable().getColumnCount(); j++)
		{
			AttributedString string;
			String column = (String) attribs.getTable().getColumnModel().getColumn(j).getHeaderValue();
			String text = attribs.getTable().getValueAt(row, j).toString();

			if (column.equalsIgnoreCase("Name"))
			{
				string = formatName(text, fontSize);
			}
			else if (column.equalsIgnoreCase("Ability"))
			{
				string = formatAbility(column, text, fontSize);
			}
			else if (column.equalsIgnoreCase("Minimum Ability"))
			{
				string = formatMinimumAbility(row, column, text, fontSize);
			}
			else
			{
				string = formatDefault(column, text, fontSize);
			}

			if (string == null)
			{
				continue;
			}

			LineBreakMeasurer lbm = new LineBreakMeasurer(string.getIterator(), new FontRenderContext(null, true, true));

			for (TextLayout layout = lbm.nextLayout(rect.width); layout != null; layout = lbm.nextLayout(rect.width))
			{
				layout.draw(g2d, rect.x, rect.y);
				rect.y += 12;
			}
		}
		return;
	}



	private AttributedString formatName(String text, int fontSize)
	{
		AttributedString attString;
		attString = new AttributedString(text);

		if (times != null)
		{
			attString.addAttribute(TextAttribute.FONT, timesBold);
			return attString;
		}
		attString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		attString.addAttribute(TextAttribute.SIZE, new Float(fontSize));
		return attString;
	}



	private AttributedString formatAbility(String column, String text, int fontSize)
	{
		if (getMinimumAbilityIndex() != -1)
		{
			return null;
		}
		return formatDefault(column, text, fontSize);
	}



	private AttributedString formatMinimumAbility(int row, String column, String text, int fontSize)
	{
		if (getAbilityIndex() != -1)
		{
			column = "Minimum " + attribs.getTable().getValueAt(row, getAbilityIndex());
			AttributedString attString = new AttributedString(column + ": " + text);

			if (times != null)
			{
				attString.addAttribute(TextAttribute.FONT, timesBold, 0, column.length());
				attString.addAttribute(TextAttribute.FONT, times, column.length(), attString.getIterator().getEndIndex());
				return attString;
			}
			attString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, column.length());
			attString.addAttribute(TextAttribute.SIZE, new Float(fontSize));
			return attString;
		}
		return formatDefault(column, text, fontSize);
	}



	private AttributedString formatDefault(String column, String text, int fontSize)
	{
		AttributedString attString = new AttributedString(column + ": " + text);

		if (times != null)
		{
			attString.addAttribute(TextAttribute.FONT, timesBold, 0, column.length());
			attString.addAttribute(TextAttribute.FONT, times, column.length(), attString.getIterator().getEndIndex());
		}
		attString.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, column.length());
		attString.addAttribute(TextAttribute.SIZE, new Float(fontSize));
		return attString;
	}



	private int getAbilityIndex()
	{
		for (int i = 0; i < attribs.getTable().getColumnCount(); i++)
		{
			if (((String) attribs.getTable().getColumnModel().getColumn(i).getHeaderValue()).equalsIgnoreCase("Ability"))
			{
				return i;
			}
		}
		return -1;
	}



	private int getMinimumAbilityIndex()
	{
		for (int i = 0; i < attribs.getTable().getColumnCount(); i++)
		{
			if (((String) attribs.getTable().getColumnModel().getColumn(i).getHeaderValue()).equalsIgnoreCase("Minimum Ability"))
			{
				return i;
			}
		}
		return -1;
	}



	private void checkRowEnd(double di)
	{
		if (di / attribs.getCharmsPerRow() == 1)
		{
			rect.x = 2;
		}

		if ((int) (Math.floor(di / attribs.getCharmsPerRow())) == 0)
		{
			rect.y = (int) (Math.floor(di / attribs.getCharmsPerRow())) * rect.height + 4;
		}
		else
		{
			rect.y = (int) (Math.floor(di / attribs.getCharmsPerRow())) * rect.height + 12;
		}
		return;
	}
}
