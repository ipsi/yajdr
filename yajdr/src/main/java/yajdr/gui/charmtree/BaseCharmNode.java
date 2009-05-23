package yajdr.gui.charmtree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.mckoi.jfccontrols.ResultSetTableModel;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * <b>BaseCharmNode</b>
 * <p>
 * This is the class that deals with creating the charm trees. Each tree is created dynamically by
 * this program each time the button is pressed. A tree is represented by a set of buttons, each of
 * which is backed by a charm object, containing all info associated with the charm of that name.
 * <p>
 * When a button is pushed, a new window will pop up, giving full details on the charm. Lines show
 * the direction of the charm tree. An arrow indicates the charm pointed at has the charm that is
 * pointing as a prerequisite.
 * <p>
 * All charms can be save as JPG's by using the menu option. Eventually, further formats may be
 * added.
 * 
 * @author Andrew Thorburn
 */
public class BaseCharmNode
{

	private TreeMap<String, Charm>			charms;

	private TreeMap<String, Charm>			baseCharms;

	private TreeMap<String, XYConstraints>	charmConstraints;

	private TreeSet<String>					components;

	private String							ability;

	private int								width;

	private int								height;



	/**
	 * This takes a table full of charm data, and sets up the groundwork for generating the tree.
	 * <p>
	 * First, it goes through and creates charm objects for each charm in the table. Then, it goes
	 * through again, and assigns the charm objects their prerequisites. If a charm has none, then
	 * it is added to a map of base charms.
	 * 
	 * @param t
	 *            JTable This contains all initial charm data
	 */
	public BaseCharmNode(JTable t)
	{
		charms = new TreeMap<String, Charm>();
		baseCharms = new TreeMap<String, Charm>();
		charmConstraints = new TreeMap<String, XYConstraints>();
		components = new TreeSet<String>();

		width = new Charm(t, 0).getCharmComponent().getSize().width;
		height = new Charm(t, 0).getCharmComponent().getSize().height + 40;

		for (int i = 0; i < t.getColumnCount(); i++)
		{
			if (t.getColumnName(i).equals("Ability"))
			{
				ability = (String) t.getValueAt(0, i);
				break;
			}
		}

		for (int i = 0; i < t.getRowCount(); i++)
		{
			Charm c = new Charm(t, i);
			charms.put(c.getName(), c);
		}

		for (int i = 0; i < charms.size(); i++)
		{
			Charm c = charms.get(t.getValueAt(i, 0).toString());

			String pr = c.getAttribs()[c.getPrereqIndex()];
			StringTokenizer st = new StringTokenizer(pr, ";");

			if (st.countTokens() == 1 && st.nextToken().equalsIgnoreCase("None"))
			{
				baseCharms.put(c.getName(), c);
				continue;
			}
			
			st = new StringTokenizer(pr, ";");

			while (st.hasMoreElements())
			{
				String s = st.nextToken();

				if (s.charAt(0) == ' ')
				{
					s = s.substring(1, s.length());
				}

				Charm p = charms.get(s);
				if (p != null)
				{
					p.addDescendant(c);
					c.addPrereq(p);
				}// End IF
			}// End WHILE
		}// End FOR
	}// End Constructor



	@SuppressWarnings("unused")
	private Charm getCharm(String name)
	{
		try
		{
			Connection con;
			File f = new File("c:\\mckoi1.0.3\\db.conf");

			if (f.exists())
			{
				con = DriverManager.getConnection("jdbc:mckoi:local://c:\\mckoi1.0.3\\db.conf", "ipsi", "brujah12I");
			}
			else
			{
				con = DriverManager.getConnection("jdbc:mckoi:local://.\\db.conf", "ipsi", "brujah12I");
			}

			Statement state = con.createStatement();
			ResultSet rs = state.executeQuery("SELECT * FROM charmlist WHERE \"Name\" LIKE '" + name + "'");
			ResultSetTableModel tm = new ResultSetTableModel(rs);
			return new Charm(new JTable(tm), 0);
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}



	/**
	 * Runs through and prints out the value of the <code>toString</code> method of each charm
	 * object it knows about.
	 * 
	 * @see {@linkPlain Charm}
	 */
	public void test()
	{
		Iterator<Charm> i = charms.values().iterator();
		while (i.hasNext())
		{
			Object o = i.next();
			System.out.println(o.toString());
			((Charm) o).viewCharm();
		}
	}



	private class CharmFrame extends JPanel
	{

		private static final long	serialVersionUID	= 6960771451854478468L;

		private Vector<Shape>		shapes				= new Vector<Shape>();



		public CharmFrame(LayoutManager l)
		{
			super(l);
		}



		public void addShapes(Shape shape)
		{
			shapes.add(shape);
		}



		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			RenderingHints rh = new RenderingHints(null);
			// rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
			// RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// rh.put(RenderingHints.KEY_COLOR_RENDERING,
			// RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			// rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			// rh.put(RenderingHints.KEY_FRACTIONALMETRICS,
			// RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			// rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			// rh.put(RenderingHints.KEY_INTERPOLATION,
			// RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

			g2.addRenderingHints(rh);
			g2.setStroke(new BasicStroke(2.5F));

			for (int i = 0; i < shapes.size(); i++)
			{
				Object o = shapes.elementAt(i);

				if (o instanceof Line2D.Float)
				{
					g2.draw((Shape) o);
				}
				else if (o instanceof GeneralPath)
				{
					// g2.draw((Shape)o);
					g2.fill((Shape) o);
				}
			}
		}
	}



	public void buildCharmTree()
	{
		test();
		if (true)
			return;
		JFrame jw = new JFrame();

		System.out.println("Comps: " + getCompIndex(0, 2));

		XYLayout xyl = new XYLayout();

		CharmFrame j = new CharmFrame(xyl);
		jw.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		jw.setState(Frame.MAXIMIZED_BOTH);

		Iterator<Charm> i = baseCharms.values().iterator();

		int numCharm = 0;
		int base = 0;

		while (i.hasNext())
		{
			Object o = i.next();
			if (o instanceof Charm)
			{
				Charm c = (Charm) o;
				numCharm += buildTree(c, j, xyl, 0, numCharm, 0, base);
				// numCharm++;
				base++;
				drawLines(c, j);
			}
		}

		JScrollPane jsp = new JScrollPane(j);
		j.setBackground(Color.WHITE);
		jw.getContentPane().add(jsp);
		jw.setTitle(ability + " Charm Tree");

		JMenuBar jmb = new JMenuBar();
		jmb.add(menu(jw));
		jw.setJMenuBar(jmb);

		jw.setVisible(true);
	}



	private int buildTree(Charm current, CharmFrame jw, XYLayout xyl, int depth, int numCharm, double column, int base)
	{
		XYConstraints xyc = new XYConstraints();

		int d = 1;

		if (current == null)
		{
			return 1;
		}

		Charm[] desc = current.getDescendants();

		JButton b = current.getCharmComponent();

		int index = getCompIndex(base, depth, current);
		index = index < 0 ? 0 : index;

		int comps = getCompIndex(base, depth);

		if (!components.contains(current.getName()))
		{
			double n = comps;
			double m = Toolkit.getDefaultToolkit().getScreenSize().width;
			double o = width;

			/*
			 * Need space between components - Take screen width, subtract width of total
			 * components, divide by number of components. That's the space between each component,
			 * or between the edge of the screen and the component. Once we have space between
			 * comps, if index == 0, setX(space) If index == 1, setX(space + space + width) If index
			 * == 2, setX(space + space + space + width + width)
			 */

			double space = (m - (comps * o)) / n;
			double x = (index == 0 || index == comps - 1) ? space / 2 : space;
			space = (index != 0 && index != comps - 1) ? space / 2 : space;

			xyc.setX((int) (x + (space * index + width * index)));
			xyc.setY(height * (depth + numCharm - ((base > 1) ? 1 : 0)));
			// xyc.setY(height * depth);
			xyl.addLayoutComponent(b, xyc);

			charmConstraints.put(current.getName(), xyc);

			jw.add(b);
			components.add(current.getName());
		}

		depth++;

		if (desc == null)
		{
			return d;
		}

		for (int i = 0; i < desc.length; i++)
		{
			Charm charm = desc[i];
			d += buildTree(charm, jw, xyl, depth, numCharm, i, base);
		}

		return (d / desc.length) + 1;
	}



	private void drawLines(Charm current, CharmFrame jw)
	{
		float width = current.getCharmComponent().getSize().width;
		float height = current.getCharmComponent().getSize().height;

		Charm[] desc = current.getDescendants();

		if (desc == null)
		{
			return;
		}

		for (int i = 0; i < desc.length; i++)
		{
			Charm charm = desc[i];
			drawLines(charm, jw);

			XYConstraints cur = charmConstraints.get(current.getName());
			XYConstraints des = charmConstraints.get(charm.getName());

			Point2D.Float start = new Point2D.Float(cur.getX() + (width / 2), cur.getY() + height);
			Point2D.Float end = new Point2D.Float(des.getX() + (width / 2), des.getY());

			float sideLength = 5F;
			GeneralPath gp = new GeneralPath();
			Line2D.Float line;

			if (des.getY() == cur.getY() && des.getX() < cur.getX())
			{
				start.setLocation(cur.getX(), cur.getY() + (height / 2));
				end.setLocation(des.getX() + width, cur.getY() + (height / 2));

				gp.append(new Line2D.Float(end.x, end.y, end.x + sideLength, end.y - sideLength), true);
				gp.append(new Line2D.Float(end.x + sideLength, end.y - sideLength, end.x + sideLength, end.y + sideLength), true);

				line = new Line2D.Float(start.x, start.y, end.x, end.y);
			}
			else if (des.getY() == cur.getY())
			{
				start.setLocation(cur.getX() + width, cur.getY() + (height / 2));
				end.setLocation(des.getX(), cur.getY() + (height / 2));

				gp.append(new Line2D.Float(end.x, end.y, end.x - sideLength, end.y - sideLength), true);
				gp.append(new Line2D.Float(end.x - sideLength, end.y - sideLength, end.x - sideLength, end.y + sideLength), true);

				line = new Line2D.Float(start.x, start.y, end.x, end.y);
			}
			else if (cur.getY() > des.getY())
			{
				start.setLocation(start.x, start.y - height);
				end.setLocation(end.x, end.y + height);

				gp.append(new Line2D.Float(end.x, end.y, end.x - sideLength, end.y + sideLength), true);
				gp.append(new Line2D.Float(end.x - sideLength, end.y + sideLength, end.x + sideLength, end.y + sideLength), true);

				line = new Line2D.Float(start.x, start.y, end.x, end.y + sideLength / 2);
			}
			else
			{
				gp.append(new Line2D.Float(end.x, end.y, end.x - sideLength, end.y - sideLength), true);
				gp.append(new Line2D.Float(end.x - sideLength, end.y - sideLength, end.x + sideLength, end.y - sideLength), true);

				line = new Line2D.Float(start.x, start.y, end.x, end.y - sideLength / 2);
			}

			gp.closePath();
			jw.addShapes(gp);
			jw.addShapes(line);
		}
	}



	private int getCompIndex(int base, int depth)
	{
		return getCompIndex(base, depth, null);
	}



	private int getCompIndex(int base, int depth, Object comp)
	{
		Charm baseC = null;

		Iterator<Charm> it = baseCharms.values().iterator();

		for (int i = 0; i < base; i++)
		{
			it.next();
		}

		baseC = it.next();

		Vector<Charm> descs = new Vector<Charm>();
		Vector<Charm> descsTwo = null;

		descs.add(baseC);

		for (int i = 0; i < depth; i++)
		{

			if (descs == null)
			{
				descs = new Vector<Charm>();

				buildCharms(descsTwo, descs);

				descsTwo = null;
			}
			else if (descsTwo == null)
			{
				descsTwo = new Vector<Charm>();

				buildCharms(descs, descsTwo);

				descs = null;
			}
		}

		int retval;

		if (comp != null)
		{
			retval = (descs != null) ? descs.indexOf(comp) : (descsTwo != null ? descsTwo.indexOf(comp) : -1);
		}
		else
		{
			retval = (descs != null) ? descs.size() : (descsTwo != null ? descsTwo.size() : -1);
		}

		return retval;
	}



	private void buildCharms(Vector<Charm> descsTwo, Vector<Charm> descs)
	{
		Charm temp;
		for (int j = 0; j < descsTwo.size(); j++)
		{
			temp = (descsTwo.elementAt(j) != null) ? descsTwo.elementAt(j) : null;

			Charm[] te = temp.getDescendants();

			if (te == null)
			{
				if (!descs.contains(temp))
				{
					descs.add(temp);
				}
				continue;
			}

			for (int k = 0; k < te.length; k++)
			{
				if (!descs.contains(te[k]))
				{
					descs.add(te[k]);
				}// END IF
			}// END FOR (k)
		}// END FOR (j)
	}// END buildCharms



	private JMenu menu(JFrame j)
	{
		JMenu menu = new JMenu("File");
		JMenuItem menItem = new JMenuItem("Save as JPG");
		menu.add(menItem);

		menItem.addActionListener(new SaveJPG(j));

		return menu;
	}



	private class SaveJPG implements ActionListener
	{

		private JFrame	cf;



		public SaveJPG(JFrame cf)
		{
			this.cf = cf;
		}



		public void actionPerformed(ActionEvent ae)
		{
			CharmFrame panel = null;
			for (int i = 0; i < cf.getContentPane().getComponentCount(); i++)
			{
				if (cf.getContentPane().getComponent(i) instanceof JScrollPane)
				{
					panel = (CharmFrame) ((JScrollPane) cf.getContentPane().getComponent(i)).getViewport().getView();
				}
			}

			if (panel == null)
			{
				System.out.println("panel == null");
				return;
			}
			BufferedImage img = new BufferedImage(panel.getSize().width, panel.getSize().height, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g2 = (Graphics2D) img.getGraphics();
			panel.update(g2);

			JFileChooser jfc = new JFileChooser();
			File f = null;
			if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				f = jfc.getSelectedFile();
			}
			if (f == null)
				return;

			try
			{
				FileOutputStream fos = new FileOutputStream(f);
				JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(fos);
				enc.encode(img);
				fos.flush();
				fos.close();
			}
			catch (FileNotFoundException ex)
			{
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				System.err.println(ex.getMessage());
				ex.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Save Successful", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}// End Class
