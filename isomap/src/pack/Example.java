/*
 * Copyright (C) 2012-2013 Martin Steiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pack; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class Example
{
	final static Logger log = LoggerFactory.getLogger(Example.class);

	public static void main(String[] args) throws Exception
	{
		final MyComponent comp = new MyComponent();

		Timer timer = new Timer(333, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				comp.animate();
			}
		});
		
		timer.start();
		
		JFrame frame = new JFrame();
		frame.setTitle("Simple example");
		frame.setSize(950, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(comp);
		frame.setVisible(true);
	}
}
