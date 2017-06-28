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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import ipaaca.AbstractIU;
import ipaaca.HandlerFunctor;
import ipaaca.IUEventHandler;
import ipaaca.IUEventType;
import ipaaca.Initializer;
import ipaaca.InputBuffer;
import ipaaca.LocalMessageIU;
import ipaaca.OutputBuffer;
import javafx.application.Platform;

public class IpaacaControl {

	boolean filterQuotes = true;
	
	InputBuffer in;
	OutputBuffer error = new OutputBuffer("touch_ui");
	GUIManager gui;
	public boolean ready = false;

	final class MyEventHandler implements HandlerFunctor {
		@Override
		public void handle(AbstractIU iu, IUEventType type, boolean local) {
			Map<String, String> payload = iu.getPayload();
			if (iu.getCategory().equals("touch_ui_request")) {
				try {
					handleRequest(payload);
				} catch (Exception e) {
					LocalMessageIU iu_out = new LocalMessageIU("touch_ui_reply");
					iu_out.getPayload().put("error", "request");
					iu_out.getPayload().put("log", "see touch_ui terminal for stacktrace");
					error.add(iu_out);
					e.printStackTrace();
				}
			} else if (iu.getCategory().equals("touch_ui_config")) {
				try {
					updateConfig(payload);
				} catch (Exception e) {
					LocalMessageIU iu_out = new LocalMessageIU("touch_ui_reply");
					iu_out.getPayload().put("error", "config");
					iu_out.getPayload().put("log", "see touch_ui terminal for stacktrace");
					error.add(iu_out);
					e.printStackTrace();
				}
			}
		}
	}

	public IpaacaControl(GUIManager gui) {
		this.gui = gui;
		Initializer.initializeIpaacaRsb();
		EnumSet<IUEventType> types = EnumSet.allOf(IUEventType.class);
		Set<String> categories = new ImmutableSet.Builder<String>().add("touch_ui_request", "touch_ui_config").build();
		in = new InputBuffer("touch_ui", categories);
		in.registerHandler(new IUEventHandler(new MyEventHandler(), types, categories));
		ready = true;
	}

	public void handleRequest(Map<String, String> payload) {

		System.out.println(payload);
		
		if(filterQuotes) {
			
			Map<String, String> payloadTemp = new HashMap<>();			
			Set<String> keys = new HashSet<>();
			keys.addAll(payload.keySet());
			
			for(String key :  keys) {
				payloadTemp.put(key.replace("\"", ""), payload.get(key).replace("\"", ""));
			}
			
			payload = payloadTemp;
			
		}
		
		final Map<String, String> payloadFinal = payload;
		
		if (payloadFinal.get("cmd") != null) {
			if (payloadFinal.get("cmd").equals("hideAll")) {
				gui.hideAll();
			} else if (payloadFinal.get("cmd").equals("removeAll")) {
				gui.removeAll();
			} else if (payloadFinal.get("cmd").equals("showAll")) {
				gui.showAll();
			} else {
				LocalMessageIU iu_out = new LocalMessageIU("touch_ui_reply");
				iu_out.getPayload().put("error", "cmd");
				iu_out.getPayload().put("log", "allowed cmd values are: hideAll|removeAll|showAll");
				error.add(iu_out);
			}
		}

		if (payloadFinal.get("images") != null) {
			String[] images = payloadFinal.get("images").split("\\|");
			
			if(!payloadFinal.containsKey("handles")) {
				LocalMessageIU iu_out = new LocalMessageIU("touch_ui_reply");
				iu_out.getPayload().put("error", "request");
				iu_out.getPayload().put("log", "no handles found for images. add key=handles with value=H1|H2|...");
				error.add(iu_out);
				return;
			}
			
			String[] imageWidths = {};
			String[] imageHeights = {};
			if(payloadFinal.containsKey("imageWidths")) {
				imageWidths = payloadFinal.get("imageWidths").split("\\|");
			}
			if(payloadFinal.containsKey("imageHeights")) {
				imageHeights = payloadFinal.get("imageHeights").split("\\|");
			}
			
			String[] handles = payloadFinal.get("handles").split("\\|");
			for(int i = 0; i < images.length; i++) {
				String width = null;
				String height = null;
				
				if(imageWidths.length > i) {
					width = imageWidths[i];
				}			
				if(imageHeights.length > i) {
					height = imageHeights[i];
				}		
				gui.addImageButton(images[i], handles[i], width, height);
			}
		} else if (payloadFinal.get("texts") != null) {

			String[] texts = payloadFinal.get("texts").split("\\|");
			
			String[] positions = null;
			if (payloadFinal.get("positions") != null) {
				positions = payloadFinal.get("positions").split("\\|");
			}

			if (payloadFinal.get("remove") != null) {

				if (payloadFinal.containsKey("texts")) {
					for (String t : texts) {
						gui.removeButton(t);
					}
				}

				if (payloadFinal.containsKey("positions")) {
					
					for (String p : positions) {
						gui.removeButton(Integer.parseInt(p));
					}
				}

			} else if (payloadFinal.get("hide") != null) {

				if (payloadFinal.containsKey("texts")) {
					for (String t : texts) {
						gui.hideButton(t);
					}
				}

				if (payloadFinal.containsKey("positions")) {
					for (String p : positions) {
						gui.hideButton(Integer.parseInt(p));
					}
				}

			} else {
				for (int i = 0; i < texts.length; i++) {
					String text = texts[i];
					int position = -1;
					if (positions != null) {
						position = Integer.parseInt(positions[i]);
					}
					final int posFinal = position < gui.minPos ? position : gui.minPos;
					
						
					final int id = i;
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (posFinal != -1) {													
//								gui.addButton(text, posFinal);
								String width = payloadFinal.get("width");
								String height = payloadFinal.get("height");
								String colorButton = payloadFinal.get("colorButton");
								String colorPressed = payloadFinal.get("colorPressed");
								String colorBorder = payloadFinal.get("colorBorder");
								String font = payloadFinal.get("font");
								String fontSize = payloadFinal.get("fontSize");
								String fontBold = payloadFinal.get("fontBold");
								String fontColor = payloadFinal.get("fontColor");
								String opacity = payloadFinal.get("opacity");
								String marginX = payloadFinal.get("marginX");
								String marginY = payloadFinal.get("marginY");
								
								marginX = splitValue(marginX, texts.length)[id];									
								marginY = splitValue(marginY, texts.length)[id];
								width = splitValue(width, texts.length)[id];									
								height = splitValue(height, texts.length)[id];
								colorButton = splitValue(colorButton, texts.length)[id];
								colorPressed = splitValue(colorPressed, texts.length)[id];
								colorBorder = splitValue(colorBorder, texts.length)[id];
								font = splitValue(font, texts.length)[id];
								fontSize = splitValue(fontSize, texts.length)[id];
								fontBold = splitValue(fontBold, texts.length)[id];
								fontColor = splitValue(fontColor, texts.length)[id];
								opacity = splitValue(opacity, texts.length)[id];
								
								if(!payloadFinal.containsKey("colorButton") && payloadFinal.containsKey("color")) {
									colorButton = payloadFinal.get("color");
									colorButton = splitValue(colorButton, texts.length)[id];
								}
								
								gui.minPos++;
								
								gui.addButton(text, posFinal, width, height, colorButton,
										colorPressed, colorBorder, font, fontSize, fontBold, fontColor, opacity, marginX, marginY);
							} else {
								if (payloadFinal.containsKey("translateX") || payloadFinal.containsKey("translateY")) {

									String translateX = payloadFinal.get("translateX");
									String translateY = payloadFinal.get("translateY");
									String width = payloadFinal.get("width");
									String height = payloadFinal.get("height");
									String colorButton = payloadFinal.get("colorButton");
									String colorPressed = payloadFinal.get("colorPressed");
									String colorBorder = payloadFinal.get("colorBorder");
									String font = payloadFinal.get("font");
									String fontSize = payloadFinal.get("fontSize");
									String fontBold = payloadFinal.get("fontBold");
									String fontColor = payloadFinal.get("fontColor");
									String opacity = payloadFinal.get("opacity");
									String marginX = payloadFinal.get("marginX");
									String marginY = payloadFinal.get("marginY");
									
									translateX = splitValue(translateX, texts.length)[id];									
									translateY = splitValue(translateY, texts.length)[id];
									marginX = splitValue(marginX, texts.length)[id];									
									marginY = splitValue(marginY, texts.length)[id];
									width = splitValue(width, texts.length)[id];									
									height = splitValue(height, texts.length)[id];
									colorButton = splitValue(colorButton, texts.length)[id];
									colorPressed = splitValue(colorPressed, texts.length)[id];
									colorBorder = splitValue(colorBorder, texts.length)[id];
									font = splitValue(font, texts.length)[id];
									fontSize = splitValue(fontSize, texts.length)[id];
									fontBold = splitValue(fontBold, texts.length)[id];
									fontColor = splitValue(fontColor, texts.length)[id];
									opacity = splitValue(opacity, texts.length)[id];
									
									if(!payloadFinal.containsKey("colorButton") && payloadFinal.containsKey("color")) {
										colorButton = payloadFinal.get("color");
										colorButton = splitValue(colorButton, texts.length)[id];
									}
									
									gui.addButton(text, translateX, translateY, width, height, colorButton,
											colorPressed, colorBorder, font, fontSize, fontBold, fontColor, opacity);
								} else {
									int posFinal = gui.rectancles.size() < gui.minPos ? gui.rectancles.size() : gui.minPos;
									
									gui.minPos++;
									
									String width = payloadFinal.get("width");
									String height = payloadFinal.get("height");
									String colorButton = payloadFinal.get("colorButton");
									String colorPressed = payloadFinal.get("colorPressed");
									String colorBorder = payloadFinal.get("colorBorder");
									String font = payloadFinal.get("font");
									String fontSize = payloadFinal.get("fontSize");
									String fontBold = payloadFinal.get("fontBold");
									String fontColor = payloadFinal.get("fontColor");
									String opacity = payloadFinal.get("opacity");
									String marginX = payloadFinal.get("marginX");
									String marginY = payloadFinal.get("marginY");
									
									marginX = splitValue(marginX, texts.length)[id];									
									marginY = splitValue(marginY, texts.length)[id];

									width = splitValue(width, texts.length)[id];									
									height = splitValue(height, texts.length)[id];
									colorButton = splitValue(colorButton, texts.length)[id];
									colorPressed = splitValue(colorPressed, texts.length)[id];
									colorBorder = splitValue(colorBorder, texts.length)[id];
									font = splitValue(font, texts.length)[id];
									fontSize = splitValue(fontSize, texts.length)[id];
									fontBold = splitValue(fontBold, texts.length)[id];
									fontColor = splitValue(fontColor, texts.length)[id];
									opacity = splitValue(opacity, texts.length)[id];
									
									if(!payloadFinal.containsKey("colorButton") && payloadFinal.containsKey("color")) {
										colorButton = payloadFinal.get("color");
										colorButton = splitValue(colorButton, texts.length)[id];
									}
									
									gui.addButton(text, posFinal, width, height, colorButton,
											colorPressed, colorBorder, font, fontSize, fontBold, fontColor, opacity, marginX, marginY);
								}
							}
						}
					});
				}
			}
		} else {
			String[] positions = null;
			if (payloadFinal.get("positions") != null) {
				positions = payloadFinal.get("positions").split("\\|");
			}

			if (payloadFinal.get("remove") != null) {

				if (payloadFinal.containsKey("positions")) {
					
					for (String p : positions) {
						gui.removeButton(Integer.parseInt(p));
					}
				}

			}			
		}
	}
	
	public String[] splitValue(String value, int length) {
		
		String[] result = new String[length];
				
		if(value == null) {
			return result;
		} else if(!value.contains("|")) {
			for(int i = 0; i < length; i++) {
				result[i] = value;
			}
		} else {
			for(int i = 0; i < length; i++) {
				result[i] = value.split("\\|")[i];
			}
		} 	
		
		return result;
		
	}

	public void updateConfig(Map<String, String> payload) {
		String value;

		for (String key : payload.keySet()) {
			value = payload.get(key);
			gui.setSetting(key, value);
		}
	}

}
