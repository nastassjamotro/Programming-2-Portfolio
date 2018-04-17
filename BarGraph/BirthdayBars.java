import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class BirthdayBars {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		
		frame.setSize(900, 1000);
		frame.setTitle("Birthday 'Fun'");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DrawerComp dc = new DrawerComp();
		frame.add(dc);
		
		frame.setVisible(true);
	}
	
	public static class DrawerComp extends JComponent {
		public void paintComponent (Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			for(int i = 850; i > 50; i -= 50) {
				g2.setColor(Color.BLACK);
				g2.drawString(String.valueOf((850 - i) / 2), 20, i);
				Line2D.Double grid = new Line2D.Double(10, i, 890, i);
				g2.draw(grid);
			}
			
			Day today = new Day();
			Color c = new Color(250, 0, 0);
			Day alexDay = new Day(today.getYear() + 1, 2, 5);
			Day momDay = new Day(today.getYear() , 10, 18);
			Day dadDay = new Day(today.getYear() , 8, 22);
			Day meDay = new Day(today.getYear() , 6, 25);
			
			Rectangle alexBirth = new Rectangle(150, 850 - alexDay.daysFrom(today) * 2, 100, alexDay.daysFrom(today) * 2);
			g2.setColor(c);
			g2.fill(alexBirth);
			g2.draw(alexBirth);
			g2.drawString("Alex: " + alexDay.daysFrom(today), 150, 870);
			
			Rectangle momBirth = new Rectangle(350, 850 - momDay.daysFrom(today) * 2, 100, momDay.daysFrom(today) * 2);
			c = new Color(0, 0, 250);
			g2.setColor(c);
			g2.fill(momBirth);
			g2.draw(momBirth);
			g2.drawString("Mom: " + momDay.daysFrom(today), 350, 870);
			
			Rectangle dadBirth = new Rectangle(550, 850 - dadDay.daysFrom(today) * 2, 100, dadDay.daysFrom(today) * 2);
			c = new Color(0, 250, 0);
			g2.setColor(c);
			g2.fill(dadBirth);
			g2.draw(dadBirth);
			g2.drawString("Dad: " + dadDay.daysFrom(today), 550, 870);
			
			Rectangle meBirth = new Rectangle(750, 850 - meDay.daysFrom(today) * 2, 100, meDay.daysFrom(today) * 2);
			c = new Color(100, 0, 100);
			g2.setColor(c);
			g2.fill(meBirth);
			g2.draw(meBirth);
			g2.drawString("Me: " + meDay.daysFrom(today), 750, 870);
			
		}
	}
}
