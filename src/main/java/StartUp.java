/*
 * This file is part of ButtomFactory.
 *
 * Copyright (c) 2017-2017 Sören Klett
 *                         CITEC, Bielefeld University
 *
 *
 * This file may be licensed under the terms of of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package main.java;


public class StartUp {

	public static void main(String[] args) throws InterruptedException {
		

		System.out.println("starting touch_ui...");
		
		boot();		
		
		System.out.println("started touch_ui");
	
	}
	
	public static void boot() throws InterruptedException {
		
		GUIManager g = new GUIManager();
		
		new Thread() {
			public void run() {
				g.create();
			}
		}.start();
				
		while(GUIManager.gui == null || GUIManager.gui.ic == null || !GUIManager.gui.ic.ready) {
			Thread.sleep(10);
		}
	}

}
