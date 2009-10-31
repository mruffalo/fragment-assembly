package gui;

import assembly.Fragment;
import assembly.FragmentPositionSource;
import assembly.SequenceAssembler;
import assembly.ShotgunSequenceAssembler;
import generator.Fragmentizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class FragmentDisplay
{
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
	
	JFrame frame;
	JList list;
	
	public FragmentDisplay(String orig, String assembled, List<Fragment> fragments,
		List<List<Fragment>> groupedFragments)
	{
		Image origImage = ImagePanel.getFragmentGroupImage(orig, groupedFragments,
			FragmentPositionSource.ORIGINAL_SEQUENCE);
		Image assembledImage = ImagePanel.getFragmentGroupImage(assembled, groupedFragments,
			FragmentPositionSource.ASSEMBLED_SEQUENCE);
		frame = new JFrame("Fragment Display");
		// frame.setBounds(25, 25, 320, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("Adding new ImagePanel");
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.ipadx = constraints.ipady = 2;
		frame.getContentPane().add(new ImagePanel(origImage), constraints);
		constraints.gridy = 1;
		frame.getContentPane().add(new ImagePanel(assembledImage), constraints);
		
		// TODO make this a JList instead
		constraints = new GridBagConstraints();
		constraints.ipadx = constraints.ipady = 2;
		constraints.gridheight = 2;
		constraints.gridx = 1;
		list = new JList(new FragmentListModel(fragments));
		
		JScrollPane listScroller = new JScrollPane(list);
		frame.getContentPane().add(listScroller, constraints);
		// frame.getContentPane().add(new JLabel("Test"));
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * ListModel that wraps a <code>List&lt;Fragment&gt;</code>
	 */
	private class FragmentListModel extends AbstractListModel
	{
		private List<Fragment> list;
		
		public FragmentListModel(List<Fragment> list_)
		{
			list = new ArrayList<Fragment>(list_);
		}
		
		@Override
		public Object getElementAt(int index)
		{
			return list.get(index);
		}
		
		@Override
		public int getSize()
		{
			return list.size();
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
		System.out.println();
		System.out.println(string);
		List<List<Fragment>> grouped = Fragmentizer.groupByLine(fragments, source);
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
		FragmentDisplay display = new FragmentDisplay(string, assembled, fragments, grouped);
	}
}