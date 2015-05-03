package yajdr.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RandomName extends JPanel implements ActionListener
{

	private static final Log	log					= LogFactory.getLog(RandomName.class);

	private static final long	serialVersionUID	= 5177403106252608226L;

	private JButton				generate			= new JButton("Generate Names");

	private JTextField			nameLength			= new JTextField("6", 10);

	private JTextField			numberNames			= new JTextField("5", 10);

	private JTextPane			output				= new JTextPane();

	private JScrollPane			jsp					= new JScrollPane(output);

	private JTextArea			logArea				= new JTextArea();

	private JScrollPane			logPane				= new JScrollPane(logArea);

	private JButton				viewLog				= new JButton("View Log");

	private TreeSet<String>		logSet				= new TreeSet<String>();

	private JToolBar			toolbar				= new JToolBar();

	private JButton				save				= new JButton("Save");

	private JFrame				jf;



	public RandomName()
	{
		super(new BorderLayout());
		Box box = new Box(0);
		box.add(new JLabel("Length of Name: "));
		box.add(nameLength);
		box.add(new JLabel("Number of Names: "));
		box.add(numberNames);
		box.add(generate);
		box.add(viewLog);
		add(box, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		generate.addActionListener(this);
		viewLog.addActionListener(this);
	}



	public void actionPerformed(ActionEvent ae)
	{
		Object buttonPressed = ae.getSource();
		if (buttonPressed == generate)
		{
			int numNam = Integer.parseInt(numberNames.getText());
			output.setText("");
			for (int i = 0; i < numNam; i++)
			{
				char[] name = randomize(Integer.parseInt(nameLength.getText()));
				logSet.add(new String(name) + "\n");
				output.replaceSelection(new String(name) + "\n");
			}
		}
		else if (buttonPressed == viewLog)
		{
			jf = new JFrame();
			jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			Iterator<String> i = logSet.iterator();
			while (i.hasNext())
			{
				logArea.append(i.next());
			}
			jf.getContentPane().add(logPane, BorderLayout.CENTER);
			toolbar.add(save);
			jf.getContentPane().add(toolbar, BorderLayout.NORTH);
			save.addActionListener(this);
			jf.setTitle("Name Log");
			jf.pack();
			if (jf.getSize().height > Toolkit.getDefaultToolkit().getScreenSize().height)
			{
				jf.setSize(new Dimension(jf.getSize().width, Toolkit.getDefaultToolkit().getScreenSize().height));
			}
			jf.setVisible(true);
			jf.setLocation(DieRollerMain.centreOnScreen(jf.getSize()));
			return;
		}
		else if (buttonPressed == save && jf.isVisible())
		{
			try
			{
				FileWriter outStream = new FileWriter("Random Names");
				PrintWriter outs = new PrintWriter(outStream);
				outs.println(output.getText());
				outs.close();
			}
			catch (IOException e)
			{
				log.error("Error saving random names list...", e);
			}
		}
	}



	public char[] randomize(int nameLength)
	{
		char[] name = new char[nameLength];

		if (Math.random() < 0.5)
		{
			name[0] = getDefaultCons(random(21));
		}
		else
		{
			name[0] = getDefaultVowel(random(6));
		}

		for (int i = 1; i < nameLength; i++)
		{
			if (isSoftCons(name[i - 1]))
			{
				name[i] = getDefaultVowel(random(6));
			}
			else
			{
				name[i] = getNextChar();
			}
		}
		return name;
	}



	private char getDefaultCons(int cha)
	{
		switch (cha)
		{
			case 1:
				return 'b';
			case 2:
				return 'c';
			case 3:
				return 'd';
			case 4:
				return 'f';
			case 5:
				return 'g';
			case 6:
				return 'h';
			case 7:
				return 'j';
			case 8:
				return 'k';
			case 9:
				return 'l';
			case 10:
				return 'm';
			case 11:
				return 'n';
			case 12:
				return 'p';
			case 13:
				return 'q';
			case 14:
				return 'r';
			case 15:
				return 's';
			case 16:
				return 't';
			case 17:
				return 'v';
			case 18:
				return 'w';
			case 19:
				return 'x';
			case 20:
				return 'y';
			case 21:
				return 'z';
			default:
				return ' ';
		}
	}



	private char getDefaultVowel(int cha)
	{
		switch (cha)
		{
			case 1:
				return 'a';
			case 2:
				return 'e';
			case 3:
				return 'i';
			case 4:
				return 'o';
			case 5:
				return 'u';
			case 6:
				return 'y';
			default:
				return ' ';
		}
	}



	private boolean isSoftCons(char cha)
	{
		switch (cha)
		{
			case 'h':
			case 'j':
			case 'l':
			case 'r':
			case 'w':
			case 'y':
				return true;
			default:
				return false;
		}
	}



	private char getNextChar()
	{
		char temp = ' ';
		for (int z = 0; z < 3; z++)
		{
			char lt = getDefaultCons(random(21));
			if (isSoftCons(lt))
			{
				return lt;
			}
		}
		if (temp == ' ')
		{
			return getDefaultVowel(random(6));
		}
		return temp;
	}



	private int random(int size)
	{
		return (int) (Math.floor(Math.random() * size) + 1);
	}
}
