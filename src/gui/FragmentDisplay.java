package gui;

import generator.Fragmentizer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import assembly.Fragment;
import assembly.FragmentPositionSource;
import assembly.SequenceAssembler;
import assembly.ShotgunSequenceAssembler;

public class FragmentDisplay
{
	private static final String FRAGMENT_TEXT = "Fragment";
	
	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}
	
	private JFrame frame;
	private JTable table;
	private ImagePanel origImagePanel;
	private ImagePanel assembledImagePanel;
	private String origString;
	private String assembledString;
	List<List<Fragment>> origGrouped;
	List<List<Fragment>> assembledGrouped;
	private List<Fragment> fragments;
	int scale = 2;
	
	Fragment selectedFragment = null;
	
	public FragmentDisplay(String origString_, String assembledString_, List<Fragment> fragments_)
	{
		fragments = new ArrayList<Fragment>(fragments_);
		origString = origString_;
		assembledString = assembledString_;
		origGrouped = Fragmentizer.groupByLine(fragments, FragmentPositionSource.ORIGINAL_SEQUENCE);
		assembledGrouped = Fragmentizer.groupByLine(fragments, FragmentPositionSource.ASSEMBLED_SEQUENCE);
		printFragmentGraph(origString, origGrouped, FragmentPositionSource.ORIGINAL_SEQUENCE);
		printFragmentGraph(assembledString, assembledGrouped, FragmentPositionSource.ASSEMBLED_SEQUENCE);
		Image origImage = ImagePanel.getFragmentGroupImage(origString, origGrouped, null,
			FragmentPositionSource.ORIGINAL_SEQUENCE, scale);
		Image assembledImage = ImagePanel.getFragmentGroupImage(assembledString, assembledGrouped, null,
			FragmentPositionSource.ASSEMBLED_SEQUENCE, scale);
		frame = new JFrame("Fragment Display");
		// frame.setBounds(25, 25, 320, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("Adding new ImagePanel");
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = 1;
		constraints.weightx = 0.5;
		constraints.weighty = 0.5;
		constraints.gridwidth = 1;
		constraints.ipadx = constraints.ipady = 2;
		constraints.fill = GridBagConstraints.BOTH;
		origImagePanel = new ImagePanel(origImage);
		frame.getContentPane().add(origImagePanel, constraints);
		constraints.gridy = 1;
		assembledImagePanel = new ImagePanel(assembledImage);
		frame.getContentPane().add(assembledImagePanel, constraints);
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.ipadx = constraints.ipady = 2;
		constraints.gridheight = 2;
		constraints.gridx = 1;
		table = new JTable(new FragmentTableModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new FragmentRedrawSelectionListener());
		
		JScrollPane tableScroller = new JScrollPane(table);
		frame.getContentPane().add(tableScroller, constraints);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * XXX: Temporary
	 */
	private static void printFragmentGraph(String string, List<List<Fragment>> grouped, FragmentPositionSource source)
	{
		System.out.println();
		System.out.println(string);
		for (List<Fragment> list : grouped)
		{
			int begin = 0;
			for (Fragment fragment : list)
			{
				for (int i = 0; i < fragment.getPosition(source) - begin; i++)
				{
					System.out.print(" ");
				}
				System.out.print(fragment.string);
				begin = fragment.getPosition(source) + fragment.string.length();
			}
			System.out.println();
		}
	}
	
	private void redrawImages()
	{
		Image origImage = ImagePanel.getFragmentGroupImage(origString, origGrouped, selectedFragment,
			FragmentPositionSource.ORIGINAL_SEQUENCE, scale);
		origImagePanel.setImage(origImage);
		Image assembledImage = ImagePanel.getFragmentGroupImage(assembledString, assembledGrouped, selectedFragment,
			FragmentPositionSource.ASSEMBLED_SEQUENCE, scale);
		assembledImagePanel.setImage(assembledImage);
		frame.repaint();
	}
	
	/**
	 * TableModel that uses its parent FragmentDisplay's fragment list
	 */
	private class FragmentTableModel extends AbstractTableModel
	{
		/**
		 * Generated by Eclipse
		 */
		private static final long serialVersionUID = -4514730749142944712L;
		
		@Override
		public int getColumnCount()
		{
			return 1 + FragmentPositionSource.values().length;
		}
		
		@Override
		public int getRowCount()
		{
			return fragments.size();
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Fragment fragment = fragments.get(rowIndex);
			if (columnIndex == 0)
			{
				return fragment.string;
			}
			else
			{
				FragmentPositionSource source = FragmentPositionSource.values()[columnIndex - 1];
				return fragment.getPosition(source);
			}
		}
		
		@Override
		public String getColumnName(int columnIndex)
		{
			if (columnIndex == 0)
			{
				return FRAGMENT_TEXT;
			}
			else
			{
				FragmentPositionSource source = FragmentPositionSource.values()[columnIndex - 1];
				return source.guiDescription;
			}
		}
	}
	
	private class FragmentRedrawSelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			selectedFragment = fragments.get(table.getSelectedRow());
			// TODO redraw graphs
			System.out.printf("Selected fragment: %s%n", selectedFragment.string);
			redrawImages();
		}
	}
	
	/**
	 * XXX: Temporary
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 4)
		{
			System.err.printf("*** Usage: %s string n k kTolerance", FragmentDisplay.class.getCanonicalName());
			System.exit(1);
		}
		String string = args[0];
		int n = Integer.parseInt(args[1]);
		int k = Integer.parseInt(args[2]);
		int kTolerance = Integer.parseInt(args[3]);
		FragmentPositionSource source = FragmentPositionSource.ORIGINAL_SEQUENCE;
		List<Fragment> fragments = Fragmentizer.fragmentizeForShotgun(string, n, k, kTolerance);
		SequenceAssembler sa = new ShotgunSequenceAssembler();
		for (Fragment fragment : fragments)
		{
			System.out.printf("%s%n", fragment.string);
		}
		String assembled = sa.assembleSequence(fragments);
		for (Fragment fragment : fragments)
		{
			System.out.printf("%5d: %s%n", fragment.getPosition(source), fragment.string);
		}
		FragmentDisplay display = new FragmentDisplay(string, assembled, fragments);
	}
}
