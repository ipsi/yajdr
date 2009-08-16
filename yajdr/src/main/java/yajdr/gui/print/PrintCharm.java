package yajdr.gui.print;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrintCharm
{
	private static final Log	log				= LogFactory.getLog(PrintCharm.class);

	private int					pages;

	private int					charmsPerPage	= 8;

	private int					charmsPerRow	= 4;

	private int					charmsPerColumn	= 2;

	private int					fontSize		= 8;

	private String				fontLocation	= "C:\\WINDOWS\\Fonts";

	private PageFormat			pageFormat;

	private JTable				table;

	private static double		CM_TO_INCH		= 0.393700787;



	public PrintCharm(JTable t)
	{
		table = t;
		createDefaultPage();
	}



	public void print()
	{
		PrintService[] availableServices = PrinterJob.lookupPrintServices();

		if (availableServices.length == 0)
		{
			log.warn("No Printers could be found!");
			return;
		}

		if (log.isTraceEnabled())
		{
			for (PrintService p : availableServices)
				log.trace("Found PrintService [" + p.getName() + "]");
		}

		PrinterJob printjob = PrinterJob.getPrinterJob();

		try
		{
			printjob.setPrintService(availableServices[0]);
		}
		catch (PrinterException e)
		{
			log.warn("Couldn't set the print service!", e);
			return;
		}
		printjob.setJobName("Charm Cards");
		Book book = new Book();

		pages = (int) Math.ceil(((double) table.getSelectedRowCount()) / charmsPerPage);

		PageAttributes attribs = new PageAttributes();
		attribs.setCharmsPerColumn(charmsPerColumn);
		attribs.setCharmsPerPage(charmsPerPage);
		attribs.setCharmsPerRow(charmsPerRow);
		attribs.setFontLocation(fontLocation);
		attribs.setFontSize(fontSize);
		attribs.setTable(table);

		book.append(new Document(attribs), pageFormat, pages);
		printjob.setPageable(book);

		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		if (printjob.printDialog(pras))
		{
			try
			{
				printjob.print();
			}
			catch (Exception e)
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}



	public void pageSetup()
	{
		new PrintSetup(PrinterJob.getPrinterJob(), pageFormat, this);
	}



	private void createDefaultPage()
	{
		pageFormat = PrinterJob.getPrinterJob().defaultPage();

		double a4x = (21 * PrintCharm.CM_TO_INCH * 72);
		double a4y = (29.7 * PrintCharm.CM_TO_INCH * 72);

		Paper paper = new Paper();
		paper.setSize(a4x, a4y);

		double imagex = a4x / 21;
		double imagey = a4y / 29.7;
		double imagewidth = paper.getWidth() - 2 * imagex;
		double imageheight = paper.getHeight() - 2 * imagey;

		paper.setImageableArea(imagex, imagey, imagewidth, imageheight);
		pageFormat.setOrientation(PageFormat.LANDSCAPE);
		pageFormat.setPaper(paper);
	}



	public int getCharmsPerPage()
	{
		return charmsPerPage;
	}



	public int getCharmsPerRow()
	{
		return charmsPerRow;
	}



	public int getCharmsPerColumn()
	{
		return charmsPerColumn;
	}



	public void setCharmsPerPage(int charmsPerPage)
	{
		this.charmsPerPage = charmsPerPage;
	}



	public void setCharmsPerRow(int charmsPerRow)
	{
		this.charmsPerRow = charmsPerRow;
	}



	public void setCharmsPerColumn(int charmsPerColumn)
	{
		this.charmsPerColumn = charmsPerColumn;
	}



	public PageFormat getPageFormat()
	{
		return pageFormat;
	}



	public void setPageFormat(PageFormat pageFormat)
	{
		this.pageFormat = pageFormat;
	}
}
