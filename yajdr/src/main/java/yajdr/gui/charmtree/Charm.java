package yajdr.gui.charmtree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;

import yajdr.gui.charmgui.CharmView;


public class Charm
{

	private Object[]	attributes;

	private String[]	columns;

	private Charm[]		descendants		= null;

	private int			prereqIndex		= -1;

	private int			prereqCount		= 0;

	private int			descendantCount	= 0;

	private JButton		button;



	public Charm(JTable table, int row)
	{
		columns = new String[table.getColumnCount()];
		attributes = new Object[columns.length];
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			columns[i] = (String) table.getColumnModel().getColumn(i).getHeaderValue();

			if (columns[i].equalsIgnoreCase("Prerequisite Charms"))
			{
				prereqIndex = i;
			}
			attributes[i] = table.getValueAt(row, i);
		}
		button = createCharmComponent();
	}



	public String[] getAttribs()
	{
		String[] attribs = new String[attributes.length];
		for (int i = 0; i < attribs.length; i++)
		{
			if (attributes[i] instanceof String)
			{
				attribs[i] = ((String) attributes[i]).toString();
			}
			else if (attributes[i] instanceof Integer)
			{
				attribs[i] = ((Integer) attributes[i]).toString();
			}
			else if (attributes[i] instanceof Charm[])
			{
				String prereqs = "";
				for (int j = 0; j < ((Charm[]) attributes[i]).length; j++)
				{
					prereqs += ((Charm[]) attributes[i])[j].getName() + ", ";
				}
				attribs[i] = prereqs.substring(0, prereqs.lastIndexOf(", "));
			}
			else
			{
				attribs[i] = null;
			}
		}

		return attribs;
	}



	public String getName()
	{
		return (attributes[0] instanceof String) ? (String) attributes[0] : null;
	}



	public void setAttribs(Object[] attribs)
	{
		attributes = attribs;
	}



	public void replaceAttribs(Object[] attribs)
	{
		for (int i = 0; i < attribs.length; i++)
		{
			attributes[i] = (attribs[i] != null) ? attribs[i] : attributes[i];
		}
	}



	public Charm[] getPrereqs()
	{
		for (int i = 0; i < attributes.length; i++)
		{
			if (attributes[i] instanceof Charm[])
			{
				return (Charm[]) attributes[i];
			}
		}
		return null;
	}



	public void setPrereqs(Charm[] c)
	{
		attributes[prereqIndex] = c;
	}



	public void addPrereq(Charm c)
	{
		if (containsPrerequisite(c))
		{
			return;
		}

		Charm[] prereq = getPrereqs();

		if (prereq == null)
		{
			prereq = new Charm[1];
		}
		else if (prereqCount >= prereq.length)
		{
			prereq = resizeArray(prereq);
		}

		prereq[prereqCount] = c;
		prereqCount++;
		setPrereqs(prereq);
	}



	public boolean containsPrerequisite(Charm c)
	{
		Charm[] crry = getPrereqs();
		if (crry == null)
			return false;

		for (int i = 0; i < crry.length; i++)
		{
			if (crry[i].equals(c))
			{
				return true;
			}
		}
		return false;
	}



	public int getPrereqIndex()
	{
		return prereqIndex;
	}



	public Charm[] getDescendants()
	{
		return descendants;
	}



	public void setDescendants(Charm[] des)
	{
		descendants = des;
	}



	public void addDescendant(Charm c)
	{
		if (containsDescendant(c))
		{
			return;
		}
		Charm[] descendants = getDescendants();

		if (descendants == null)
		{
			descendants = new Charm[1];
		}
		else if (descendantCount >= descendants.length)
		{
			descendants = resizeArray(descendants);
		}

		descendants[descendantCount++] = c;
		setDescendants(descendants);
	}



	public boolean containsDescendant(Charm c)
	{
		if (descendants == null)
			return false;

		for (int i = 0; i < descendants.length; i++)
		{
			if (descendants[i].equals(c))
			{
				return true;
			}
		}
		return false;
	}



	public String[] getColumns()
	{
		return columns;
	}



	public void viewCharm()
	{
		new CharmView(this);
	}



	public String toString()
	{
		String s = "";
		String[] a = getAttribs();
		String[] c = getColumns();
		for (int i = 0; i < a.length; i++)
		{
			s += c[i] + ": " + a[i] + ", ";
		}
		return s.substring(0, s.lastIndexOf(", "));
	}



	public boolean equals(Object o)
	{
		if (o instanceof Charm)
		{
			return getName().equals(((Charm) o).getName());
		}

		return false;
	}



	public JButton getCharmComponent()
	{
		return button;
	}



	public JButton createCharmComponent()
	{
		ImageIcon image = new ImageIcon("Images\\CharmContainer.jpg");

		JButton b = new CharmButton(image, getName(), this);
		b.setMaximumSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
		b.setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
		b.setMinimumSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
		b.setSize(new Dimension(image.getIconWidth(), image.getIconHeight()));

		return b;
	}



	private class CharmButton extends JButton implements ActionListener
	{

		private static final long	serialVersionUID	= 196814182781821862L;

		private Icon				i;

		private String				name;

		private Charm				charm;



		public CharmButton(Icon i, String name, Charm charm)
		{
			super(i);
			addActionListener(this);
			this.i = i;
			this.name = name.replaceAll("`", "'");
			this.charm = charm;
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}



		public void actionPerformed(ActionEvent ae)
		{
			charm.viewCharm();
		}



		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			/*
			 * System.out.println(getName()); if(getName() == null){ return; }
			 */

			JLabel l = new JLabel(name.replace('`', '\''));
			JLabel lb = new JLabel("M");

			Graphics2D g2 = (Graphics2D) g;

			int x = (i.getIconWidth() - l.getPreferredSize().width) / 2;
			int y = (i.getIconHeight() + lb.getPreferredSize().height) / 2;

			String temp = null, temptwo = null;
			if (x < 10)
			{
				int ind = name.lastIndexOf(" ", name.length() / 2);
				if (ind == -1)
				{
					ind = name.indexOf(" ");
				}
				temp = name.substring(ind, name.length());
				temptwo = name.substring(0, ind);

				l.setText(temptwo);
				x = (i.getIconWidth() - l.getPreferredSize().width) / 2;
				y = (i.getIconHeight() + (2 * lb.getPreferredSize().height)) / 2;
			}
			if (temp == null)
			{
				/*
				 * AttributedString as = new AttributedString(name);
				 * as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
				 * as.addAttribute(TextAttribute.SIZE, new Float(8)); TextLayout tl = new
				 * TextLayout(as.getIterator(), new FontRenderContext(null, true, true));
				 * tl.draw(g2, x, y);
				 */
				g2.drawString(name, x, y);
			}
			else
			{
				g2.drawString(temptwo, x, y - lb.getPreferredSize().height);

				l.setText(temp);
				x = (i.getIconWidth() - l.getPreferredSize().width) / 2;
				g2.drawString(temp, x, y);
			}
		}
	}



	private Charm[] resizeArray(Charm[] array)
	{
		Charm[] newArray = new Charm[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}

}
