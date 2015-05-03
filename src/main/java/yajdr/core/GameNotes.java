package yajdr.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class is designed to allow someone to make notes for their game, and then save said notes as
 * a file. While easily accomplished, it would be nice to be able to easily serialize this object.
 * As of java 1.3.1, JFileChooser could not be serialized.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 1.0
 */

public class GameNotes extends JPanel implements ActionListener, DocumentListener
{
	private static final Logger logger				= LoggerFactory.getLogger(GameNotes.class);

	private static final long	serialVersionUID	= 9034263348124531749L;

	private JTextPane			notes				= new JTextPane();

	private JScrollPane			notesPane			= new JScrollPane(notes);

	private JButton				save				= new JButton(new ImageIcon(getClass().getResource("/Images/save16_d.gif")));

	private JButton				load				= new JButton(new ImageIcon(getClass().getResource("/Images/open_document16.gif")));

	private JButton				newDocument			= new JButton(new ImageIcon(getClass().getResource("/Images/new_document16.gif")));

	private JButton				saveAs				= new JButton(new ImageIcon(getClass().getResource("/Images/save_green16_h.gif")));

	private JButton				autoLoad			= new JButton("File to load on start");

	private JToggleButton		ital				= new JToggleButton("I");

	private JToggleButton		bol					= new JToggleButton("B");

	private File				currentFile			= null;

	private JToolBar			toolBar				= new JToolBar();

	private boolean				dirty				= false, newDoc = false;

	private static String		AUTO_LOAD_PROP_FILE	= "autoLoad.ser";



	/**
   *
   */
	public GameNotes()
	{
		setLayout(new BorderLayout());
		toolBar.setFloatable(false);
		notes.getDocument().addDocumentListener(this);

		save.setToolTipText("Save File");
		load.setToolTipText("Load File");
		newDocument.setToolTipText("Create new Document");
		saveAs.setToolTipText("Save file as...");
		autoLoad.setToolTipText("Set which file this program should autoload on startup");

		toolBar.add(newDocument);
		toolBar.add(load);
		toolBar.add(save);
		toolBar.add(saveAs);
		toolBar.add(autoLoad);
		toolBar.add(bol);
		toolBar.add(ital);
		add(notesPane, BorderLayout.CENTER);
		add(toolBar, BorderLayout.NORTH);

		save.addActionListener(this);
		saveAs.addActionListener(this);
		load.addActionListener(this);
		newDocument.addActionListener(this);
		autoLoad.addActionListener(this);
		bol.addActionListener(this);
		ital.addActionListener(this);

		// notes.setLineWrap(true);
		autoLoad();
	}



	public void actionPerformed(ActionEvent ae)
	{
		Object buttonPressed = ae.getSource();
		if (buttonPressed == save)
		{
			save(false);
		}
		else if (buttonPressed == saveAs)
		{
			save(true);
		}
		else if (buttonPressed == load)
		{
			if (discard())
			{
				load(false);
			}
		}
		else if (buttonPressed == autoLoad)
		{
			setAutoLoad();
		}
		else if (buttonPressed == newDocument)
		{
			if (discard())
			{
				makeNewFile();
				notes.setText("");
			}
		}
		else if (buttonPressed == bol)
		{
			if (bol.isSelected())
			{
				notes.setFont(new Font("Bold", Font.BOLD, notes.getFont().getSize()));
			}
			else
			{
				notes.setFont(new Font("Normal", Font.PLAIN, notes.getFont().getSize()));
			}
		}
		else if (buttonPressed == ital)
		{
			if (ital.isSelected())
			{
				notes.setFont(new Font("Ital", Font.ITALIC, notes.getFont().getSize()));
			}
			else
			{
				notes.setFont(new Font("Normal", Font.PLAIN, notes.getFont().getSize()));
			}
		}
	}



	public void changedUpdate(DocumentEvent de)
	{
		dirty = true;
	}



	public void insertUpdate(DocumentEvent de)
	{
		dirty = true;
	}



	public void removeUpdate(DocumentEvent de)
	{
		dirty = true;
	}



	private boolean discard()
	{
		if (!dirty)
		{
			return true;
		}

		int choice = JOptionPane.showConfirmDialog(null, "Document has changed. Save changes?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		switch (choice)
		{
			case JOptionPane.YES_OPTION:
				save(false);
				return true;
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION:
			default:
				return false;
		}
	}



	private void makeNewFile()
	{
		currentFile = new File("");
		newDoc = true;
	}



	private void save(boolean saveAsBoolean)
	{
		FileWriter outstream;
		PrintWriter outs = null;
		JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
		try
		{
			if (saveAsBoolean || newDoc)
			{
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					outstream = new FileWriter(fc.getSelectedFile());
					outs = new PrintWriter(outstream);
					newDoc = false;
				}
			}
			else
			{
				outstream = new FileWriter(currentFile);
				outs = new PrintWriter(outstream);
			}

			if (outs != null)
			{
				outs.println(notes.getText());
				outs.close();
			}
			dirty = false;
		}
		catch (IOException ex)
		{
			System.out.println("IO Error" + ex.getMessage());
		}
	}



	private void load(boolean autoload)
	{
		FileReader instream;
		BufferedReader ins = null;
		try
		{
			if (!autoload)
			{
				JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					currentFile = fc.getSelectedFile();
				}
			}

			instream = new FileReader(currentFile);
			ins = new BufferedReader(instream);

			String input = "";
			for (String temp = ins.readLine(); temp != null; temp = ins.readLine())
			{
				input += temp + "\n";
			}
			notes.setText(input.substring(0, input.lastIndexOf("\n")));
			ins.close();

			dirty = false;
		}
		catch (IOException e)
		{
			logger.error("Caught IOException trying to load file", e);
		}
	}



	private void setAutoLoad()
	{
		int choice = JOptionPane.showOptionDialog(null, "Set Current File to Autoload?", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		switch (choice)
		{
			case JOptionPane.YES_OPTION: // If yes, write that to the file, and then return.
				writeAutoLoadFile(currentFile);
				return;
			case JOptionPane.NO_OPTION: // If no, then allow them to choose a file.
				break;
			case JOptionPane.CANCEL_OPTION: // If cancel, do nothing.
				return;
		}

		JFileChooser temp = new JFileChooser("c:\\");
		temp.setMultiSelectionEnabled(false);

		choice = temp.showOpenDialog(null);
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			writeAutoLoadFile(temp.getSelectedFile());
		}
	}



	private void writeAutoLoadFile(File f)
	{
		try
		{
			FileOutputStream outStream = new FileOutputStream("autoLoad.ser");
			ObjectOutputStream outs = new ObjectOutputStream(outStream);
			outs.writeObject(f);
			outs.flush();
			outs.close();
		}
		catch (IOException e)
		{
			logger.error("Caught IOException trying to write auto-load file", e);
		}
	}



	private void autoLoad()
	{
		try
		{
			File auto = new File(GameNotes.AUTO_LOAD_PROP_FILE);
			if (auto.exists())
			{
				FileInputStream inStream = new FileInputStream(GameNotes.AUTO_LOAD_PROP_FILE);
				ObjectInputStream ins = new ObjectInputStream(inStream);
				currentFile = (File) ins.readObject();
				ins.close();

				if (currentFile.exists())
					load(true);
				else
					logger.warn("Can't find auto-load file of [" + currentFile.getCanonicalPath() + "]");
			}
			else
				logger.debug("Couldn't find file [" + auto.getCanonicalPath() + "]");
		}
		catch (IOException e)
		{
			logger.error("Caught IOException trying to read auto-load file", e);
		}
		catch (ClassNotFoundException e)
		{
			logger.error("Caught ClassNotFoundException trying to read auto-load file", e);
		}
	}
}
