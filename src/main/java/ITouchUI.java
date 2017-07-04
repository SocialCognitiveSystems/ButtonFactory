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

public interface ITouchUI {


	public void setSetting(String key, String value);
	public void addButton(String text, String translateX2, String translateY2, String width2, String height2,
			String colorButton, String colorPressed, String colorBorder, String font2, String fontSize2,
			String fontBold, String fontColor2, String opacity2);
	public void addButton(String title);
	public void addButton(String title, int id);
	public void removeButton(String title);	
	public void removeAll();
	public void hideButton(String title);
	public void showButton(String title);
	public void hideAll();
	public void showAll();
	
}
