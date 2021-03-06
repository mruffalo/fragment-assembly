package gui;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import assembly.*;

public class ImagePanel extends JPanel implements Scrollable
{
	/**
	 * Generated by Eclipse
	 */
	private static final long serialVersionUID = 3006769532505931833L;
	
	private Image img;
	private FragmentDisplay fragmentDisplay;
	
	public ImagePanel(FragmentDisplay fragmentDisplay_, Image img_)
	{
		img = img_;
		fragmentDisplay = fragmentDisplay_;
		setBackground(fragmentDisplay.getSettings().colors.get(FragmentDisplayColor.BACKGROUND));
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.drawImage(img, 0, 0, null);
	}
	
	public void setImage(Image image)
	{
		img = image;
		setBackground(fragmentDisplay.getSettings().colors.get(FragmentDisplayColor.BACKGROUND));
		setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
	}
	
	public static Image getFragmentGroupImage(FragmentDisplaySettings settings, String sequence,
		List<List<Fragment>> fragmentGroups, Fragment selected, FragmentPositionSource source)
	{
		int width = sequence.length() * settings.scale;
		if (width == 0)
		{
			width = 1;
		}
		int height = (fragmentGroups.size() * 2 + 1) * settings.scale;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = image.createGraphics();
		
		g2d.setColor(settings.colors.get(FragmentDisplayColor.BACKGROUND));
		g2d.fillRect(0, 0, width, height);
		
		g2d.setColor(settings.colors.get(FragmentDisplayColor.SEQUENCE));
		g2d.fill(new Rectangle2D.Float(0, 0, sequence.length() * settings.scale, settings.scale));
		g2d.dispose();
		
		int i = 0;
		for (List<Fragment> list : fragmentGroups)
		{
			for (Fragment fragment : list)
			{
				g2d = image.createGraphics();
				Color color = fragment.equals(selected) ? settings.colors.get(FragmentDisplayColor.SELECTED)
						: settings.colors.get(FragmentDisplayColor.FRAGMENT);
				g2d.setColor(color);
				
				g2d.fill(new Rectangle2D.Float(fragment.getPosition(source) * settings.scale, (i + 1) * 2
						* settings.scale, fragment.string.length() * settings.scale, settings.scale));
				g2d.dispose();
			}
			i++;
		}
		
		return image;
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 10;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 1;
	}
	
	public Image getImage()
	{
		return img;
	}
}
