/*
 * This file is part of ButtonFactory.
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

import java.util.Scanner;

import ipaaca.LocalMessageIU;
import ipaaca.OutputBuffer;

public class TestUp {

	public static void main(String[] args) throws InterruptedException {
		

		System.out.println("starting touch_ui...");
		
		boot();		
		
		System.out.println("started touch_ui");
		
		Scanner s = new Scanner(System.in);	
				
		OutputBuffer out = new OutputBuffer("touch_ui_reply");
		LocalMessageIU iu = new LocalMessageIU("touch_ui_config");
		iu.getPayload().put("translateX", "0.5");
		iu.getPayload().put("translateY", "0.5");
		iu.getPayload().put("width", "300");
		iu.getPayload().put("height", "100");	
		iu.getPayload().put("imageWidth", "300");
		iu.getPayload().put("imageHeight", "50");	
		iu.getPayload().put("marginX", "20");
		iu.getPayload().put("marginY", "20");	
		iu.getPayload().put("colorButton", "green");
		iu.getPayload().put("colorPressed", "0xffffff");	
		iu.getPayload().put("colorBorder", "black");
		iu.getPayload().put("font", "Arial");
		iu.getPayload().put("fontSize", "24");
		iu.getPayload().put("fontBold", "true");	
		iu.getPayload().put("fontColor", "white");
		iu.getPayload().put("opacity", "1");
		iu.getPayload().put("maxColumn", "2");
		out.add(iu);
		
		out = new OutputBuffer("touch_ui_reply");
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("\"texts\"", "\"A|B|C|D\"");	
		iu.getPayload().put("color", "red|orange|green|0xeeeeee");
		iu.getPayload().put("width", "200|100|100|200");
		// adjusting margins because of different widths
		iu.getPayload().put("marginX", "0|120|0|-80");
		out.add(iu);
				
		System.out.println("changing some config fields...");
		System.out.println("adding four buttons");
		
		System.out.println("pressing enter inside the console will start the next test");
		System.out.println("next step removes old buttons and adds four buttons with image");
		
		s.nextLine();
		
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("cmd", "removeAll");
		// full paths are recommended, but filenames also work if image is in working directory
		iu.getPayload().put("images", "test.png|test.png|test.png|test.png");		
		iu.getPayload().put("imageWidths", "100|100|100|100");
//		iu.getPayload().put("imageHeights", "40|40|0|40");	
		iu.getPayload().put("handles", "TEST|A|B|C");
		out.add(iu);
		
		System.out.println("next step removes and adds text buttons");
		s.nextLine();
				
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("cmd", "removeAll");
		iu.getPayload().put("texts", "A|D|C|E");
		out.add(iu);
		
		System.out.println("next step removes buttons A and C");
		s.nextLine();
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("remove", "nop");		
		iu.getPayload().put("texts", "A|C");
		out.add(iu);

		System.out.println("next step adds text buttons");
		s.nextLine();
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("texts", "TEST|A|E|C");
		out.add(iu);
		System.out.println("next step hides TEST, A and C");
		s.nextLine();
		
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("hide", "nop");	
		iu.getPayload().put("texts", "TEST|A|C");	
		out.add(iu);
		System.out.println("next step hides all buttons");
		s.nextLine();		
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("cmd", "hideAll");		
		out.add(iu);
		System.out.println("next step shows all buttons");
		s.nextLine();		
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("cmd", "showAll");		
		out.add(iu);
		
		System.out.println("next step removes all buttons");
		s.nextLine();
				
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("cmd", "removeAll");		
		out.add(iu);
		System.out.println("next step adds two free buttons and four in the grid, two ius needed");
		s.nextLine();
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("texts", "TEST|A");		
		iu.getPayload().put("translateX", "100|110");
		iu.getPayload().put("translateY", "200|700");
		iu.getPayload().put("colorButton", "red|orange");
		out.add(iu);
		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("texts", "A|B|C|D");	
		out.add(iu);

		System.out.println("next step removes all and adds five buttons");
		s.nextLine();

		
		iu = new LocalMessageIU("touch_ui_request");
		iu.getPayload().put("cmd", "removeAll");		
		iu.getPayload().put("texts", "A|B|C|D|E");		
		out.add(iu);
		
		
		
		System.out.println("end of test, ENTER exits");
		s.nextLine();
		s.close();
		System.exit(0);
		
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
