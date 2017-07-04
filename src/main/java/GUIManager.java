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

import ipaaca.LocalMessageIU;
import ipaaca.OutputBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUIManager extends Application implements ITouchUI {

	/**
	 * available keys: translateX, translateY, width, height, marginX, marginY, <br>
	 * colorButton, colorPressed, colorBorder, font, fontSize, fontColor,<br>
	 * fontBold (true or false), opacity, maxRow, maxColumn
	 */
	Map<String, String> config;

	public static GUIManager gui;
	Stage primaryStage;
	
	Group root;
	
	Scene scene = null;
	List<Scene> scenes = null;
	List<Group> roots = null;
	List<Stage> stages = null;

	int id = 0;

	double xres = 1920;
	double yres = 1080;

	double translateX = 0.5 * xres;
	double translateY = 0.5 * yres;
	double width = 300;
	double height = 100;

	int imageWidth = 50;
	int imageHeight = 50;

	double marginX = 20;
	double marginY = 20;
	Color buttonColor = Color.GREEN;
	Color pressedColor = Color.BEIGE;
	Color borderColor = Color.BLACK;
	int fontSize = 24;
	Color fontColor = Color.WHITE;
	boolean bold = true;
	Font font = Font.font("Arial", FontWeight.BOLD, fontSize);
	double opacity = 1;
	int maxRow = 3;
	int maxColumn = 2;

	int minPos = 0;
	
	double minX = 0;
	double minY = 0;
	double maxX = 0;
	double maxY = 0;
	double maxXCustom = 0;
	double maxYCustom = 0;

	List<Rectangle> rectancles;
	List<Text> texts;
	List<StackPane> panes;

	List<Rectangle> rectanclesCustom;
	List<Text> textsCustom;
	List<StackPane> panesCustom;

	List<String> handles;
	OutputBuffer out = new OutputBuffer("touch_ui");

	public IpaacaControl ic;

	public void create() {

		GUIManager.launch();
	}

	public GUIManager() {
		gui = this;
	}

	@Override
	public void start(Stage stage) throws Exception {

		xres = Screen.getPrimary().getVisualBounds().getWidth();
		yres = Screen.getPrimary().getVisualBounds().getHeight();

		translateX = 0.5 * xres;
		translateY = 0.5 * yres;

		ic = new IpaacaControl(this);

		config = new HashMap<String, String>();
		rectancles = new ArrayList<Rectangle>();
		panes = new ArrayList<StackPane>();
		texts = new ArrayList<Text>();
		handles = new ArrayList<String>();
		rectanclesCustom = new ArrayList<Rectangle>();
		textsCustom = new ArrayList<Text>();
		panesCustom = new ArrayList<StackPane>();

		roots = new ArrayList<>();
		stages = new ArrayList<>();
		scenes = new ArrayList<>();
		
		primaryStage = stage;
		root = new Group();

//		scene = new Scene(root, xres, yres, Color.TRANSPARENT);
		scene = new Scene(root, 10, 10, Color.TRANSPARENT);
		
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("ButtonFactory");
		primaryStage.setScene(scene);
		primaryStage.setAlwaysOnTop(true);
		
//		stageCustom = new Stage();
//		stageCustom.initStyle(StageStyle.TRANSPARENT);
//		stageCustom.setTitle("ButtonFactory - Custom Buttons");
//		stageCustom.setScene(scene2);
//		stageCustom.setAlwaysOnTop(true);
//		stageCustom.setX(xres);
//		stageCustom.setY(yres);
//		stageCustom.setWidth(100);
//		stageCustom.setHeight(100);
		
		primaryStage.show();

	}

	public void addImageButton(String path, String handle, String imageWidthC, String imageHeightC) {

		if (imageWidthC == null) {
			imageWidthC = String.valueOf(imageWidth);
		}
		if (imageHeightC == null) {
			imageHeightC = String.valueOf(imageHeight);
		}

		int pos = minPos++;

		File f = new File(path);

		if (!f.exists()) {
			LocalMessageIU iu_out = new LocalMessageIU("touch_ui_reply");
			iu_out.getPayload().put("error", "request");
			iu_out.getPayload().put("log", "file: " + f.getAbsolutePath() + " does NOT exist");
			ic.error.add(iu_out);
		}

		path = f.getAbsolutePath();

		Image img = new Image("file://" + path, Integer.parseInt(imageWidthC), Integer.parseInt(imageHeightC), false, false);
		ImageView iv = new ImageView(img);

		try {

			final Rectangle r = new Rectangle(width, height, buttonColor);

			if (rectancles.size() <= pos) {

				r.setStroke(borderColor);

				rectancles.add(r);

				StackPane sp = new StackPane();

				double offsetX = 0;
				double offsetY = 0;

				offsetX = (pos % maxColumn) * (r.getWidth() + marginX);
				offsetY = (pos / maxColumn) * (r.getHeight() + marginY);

//				sp.setLayoutX(translateX + offsetX);
//				sp.setLayoutY(translateY + offsetY);

				sp.setLayoutX(offsetX);
				sp.setLayoutY(offsetY);
				

				primaryStage.setWidth(maxX - minX);
				primaryStage.setHeight(maxY - minY);
				primaryStage.setX(translateX);
				primaryStage.setY(translateY);
				
				sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(pos).setFill(buttonColor);
						handleButtonClicked(handle, pos);
					}
				});
				sp.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(pos).setFill(buttonColor);
					}
				});
				sp.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(pos).setFill(pressedColor);
					}
				});

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						root.getChildren().add(sp);
						sp.getChildren().addAll(r, iv);
					}
				});

				sp.setOpacity(opacity);
				iv.setVisible(true);

				sp.setVisible(true);

				// texts.add(iv);
				panes.add(sp);
			} else {

				Rectangle r2 = rectancles.get(pos);

				StackPane sp = (StackPane) root.getChildren().get(pos);

				double offsetX = 0;
				double offsetY = 0;

				offsetX = (pos % maxColumn) * (r2.getWidth() + marginX);
				offsetY = (pos / maxColumn) * (r2.getHeight() + marginY);

				sp.setLayoutX(translateX + offsetX);
				sp.setLayoutY(translateY + offsetY);

				sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(pos).setFill(buttonColor);
						handleButtonClicked(handle, pos);
					}
				});
				sp.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(pos).setFill(buttonColor);
					}
				});
				sp.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(pos).setFill(pressedColor);
					}
				});

				sp.setOpacity(opacity);
				sp.getChildren().clear();
				sp.getChildren().addAll(r2, iv);
				panes.add(sp);
			}

			StackPane temp = panes.get(panes.size() - 1);
			
			if(maxX < temp.getLayoutX() + r.getWidth()) {
				maxX = temp.getLayoutX() + r.getWidth() + 5;
			}
			if(maxY < temp.getLayoutY() + r.getHeight()) {
				maxY = temp.getLayoutY() + r.getHeight() + 5;
			}
			
			primaryStage.setWidth(maxX);
			primaryStage.setHeight(maxY);
			
			iv.setVisible(true);
			rectancles.get(pos).setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void updateButton(int id, String text) {

		try {

			if (text == null || text == "") {
				rectancles.get(id).setVisible(false);
				texts.get(id).setVisible(false);
				return;
			}

			Rectangle r = new Rectangle(width, height, buttonColor);

			if (rectancles.size() <= id) {

				r.setStroke(borderColor);

				rectancles.add(r);

				Text t = new Text();

				t.setText(text);
				t.setFill(fontColor);
				t.setFont(font);

				StackPane sp = new StackPane();

				double offsetX = 0;
				double offsetY = 0;

				offsetX = (id % maxColumn) * (r.getWidth() + marginX);
				offsetY = (id / maxColumn) * (r.getHeight() + marginY);

//				sp.setLayoutX(translateX + offsetX);
//				sp.setLayoutY(translateY + offsetY);
				
				sp.setLayoutX(offsetX);
				sp.setLayoutY(offsetY);

				primaryStage.setWidth(maxX - minX);
				primaryStage.setHeight(maxY - minY);
				primaryStage.setX(translateX);
				primaryStage.setY(translateY);

				sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int id = panes.indexOf(sp);
						rectancles.get(id).setFill(buttonColor);
						handleButtonClicked(texts.get(id).getText(), id);
					}
				});
				sp.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int id = panes.indexOf(sp);
						rectancles.get(id).setFill(buttonColor);
					}
				});
				sp.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int id = panes.indexOf(sp);
						rectancles.get(id).setFill(pressedColor);
					}
				});

				root.getChildren().add(sp);

				sp.setOpacity(opacity);

				sp.getChildren().addAll(r, t);

				texts.add(t);
				panes.add(sp);
			} else {

				r = rectancles.get(id);
				StackPane sp = panes.get(id);

				Text t = texts.get(id);

				t.setText(text);
				t.setFill(fontColor);
				t.setFont(font);

				double offsetX = 0;
				double offsetY = 0;

				offsetX = (id % maxColumn) * (r.getWidth() + marginX);
				offsetY = (id / maxColumn) * (r.getHeight() + marginY);

//				sp.setLayoutX(translateX + offsetX);
//				sp.setLayoutY(translateY + offsetY);

				sp.setLayoutX(offsetX);
				sp.setLayoutY(offsetY);

				primaryStage.setWidth(maxX - minX);
				primaryStage.setHeight(maxY - minY);
				primaryStage.setX(translateX);
				primaryStage.setY(translateY);
				
				sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(id).setFill(buttonColor);
						handleButtonClicked(texts.get(id).getText(), id);
					}
				});
				sp.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(id).setFill(buttonColor);
					}
				});
				sp.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(id).setFill(pressedColor);
					}
				});

				sp.setOpacity(opacity);

				// root.getChildren().add(sp);
				sp.getChildren().clear();
				sp.getChildren().addAll(r, t);
			}

			StackPane temp = panes.get(id);
			
			if(maxX < temp.getLayoutX() + r.getWidth()) {
				maxX = temp.getLayoutX() + r.getWidth() + 5;
			}
			if(maxY < temp.getLayoutY() + r.getHeight()) {
				maxY = temp.getLayoutY() + r.getHeight() + 5;
			}
			primaryStage.setWidth(maxX);
			primaryStage.setHeight(maxY);
			rectancles.get(id).setVisible(true);
			texts.get(id).setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleButtonClicked(String text, int position) {

		LocalMessageIU iu = new LocalMessageIU("touch_ui_reply");
		iu.getPayload().put("touch_input", text);
		iu.getPayload().put("position", String.valueOf(position));
		out.add(iu);
		System.out.println("payload: " + iu.getPayload());

	}

	public void handleButtonClicked(String text) {

		LocalMessageIU iu = new LocalMessageIU("touch_ui_reply");
		iu.getPayload().put("touch_input", text);
		out.add(iu);
		System.out.println("payload: " + iu.getPayload());

	}

	@Override
	public void setSetting(String key, String value) {

		double valueD;

		switch (key) {

		case "translateX":
			valueD = Double.parseDouble(value);
			if (valueD < 1) {
				translateX = valueD * xres;
			} else {
				translateX = valueD;
			}
			break;
		case "translateY":
			valueD = Double.parseDouble(value);
			if (valueD < 1) {
				translateY = valueD * yres;
			} else {
				translateY = valueD;
			}
			break;
		case "width":
			valueD = Double.parseDouble(value);
			width = valueD;
			break;
		case "height":
			valueD = Double.parseDouble(value);
			height = valueD;
			break;
		case "imageWidth":
			imageWidth = Integer.parseInt(value);
			break;
		case "imageHeight":
			imageHeight = Integer.parseInt(value);
			break;
		case "marginX":
			valueD = Double.parseDouble(value);
			marginX = valueD;
			break;
		case "marginY":
			valueD = Double.parseDouble(value);
			marginY = valueD;
			break;
		case "colorButton":
			if (value.contains("0x")) {
				double red = Long.decode(value.substring(0, 4)).doubleValue() / 255;
				double green = Long.decode("0x" + value.substring(4, 6)).doubleValue() / 255;
				double blue = Long.decode("0x" + value.substring(6, 8)).doubleValue() / 255;
				buttonColor = new Color(red, green, blue, opacity);
			} else {
				buttonColor = Color.web(value);
			}
			break;
		case "colorPressed":
			if (value.contains("0x")) {
				double red = Long.decode(value.substring(0, 4)).doubleValue() / 255;
				double green = Long.decode("0x" + value.substring(4, 6)).doubleValue() / 255;
				double blue = Long.decode("0x" + value.substring(6, 8)).doubleValue() / 255;
				pressedColor = new Color(red, green, blue, opacity);
			} else {
				pressedColor = Color.web(value);
			}
			break;
		case "colorBorder":
			if (value.contains("0x")) {
				double red = Long.decode(value.substring(0, 4)).doubleValue() / 255;
				double green = Long.decode("0x" + value.substring(4, 6)).doubleValue() / 255;
				double blue = Long.decode("0x" + value.substring(6, 8)).doubleValue() / 255;
				borderColor = new Color(red, green, blue, opacity);
			} else {
				borderColor = Color.web(value);
			}
			break;
		case "font":
			if (bold) {
				font = Font.font("Arial", FontWeight.BOLD, fontSize);
			} else {
				font = Font.font("Arial", FontWeight.NORMAL, fontSize);
			}
			break;
		case "fontSize":
			fontSize = Integer.parseInt(value);
			break;
		case "fontColor":
			if (value.contains("0x")) {
				double red = Long.decode(value.substring(0, 4)).doubleValue() / 255;
				double green = Long.decode("0x" + value.substring(4, 6)).doubleValue() / 255;
				double blue = Long.decode("0x" + value.substring(6, 8)).doubleValue() / 255;
				fontColor = new Color(red, green, blue, opacity);
			} else {
				fontColor = Color.web(value);
			}
			break;
		case "fontBold":
			bold = value.equals("true") ? true : false;
			break;
		case "opacity":
			opacity = Double.parseDouble(value);
			break;
		case "maxRow":
			maxRow = Integer.parseInt(value);
			break;
		case "maxColumn":
			maxColumn = Integer.parseInt(value);
			break;
		default:
			break;
		}

	}

	public Color getColor(String value) {
		Color result = null;
		if (value.contains("0x")) {
			double red = Long.decode(value.substring(0, 4)).doubleValue() / 255;
			double green = Long.decode("0x" + value.substring(4, 6)).doubleValue() / 255;
			double blue = Long.decode("0x" + value.substring(6, 8)).doubleValue() / 255;
			result = new Color(red, green, blue, opacity);
		} else {
			result = Color.web(value);
		}

		return result;
	}

	public void addButton(String text, int id, String width2, String height2, String colorButton, String colorPressed, String colorBorder,
			String font2, String fontSize2, String fontBold, String fontColor2, String opacity2, String marginX2, String marginY2) {

		
		if (width2 == null) {
			width2 = String.valueOf(width);
		}
		if (height2 == null) {
			height2 = String.valueOf(height);
		}
		if (colorButton == null) {
			colorButton = String.valueOf(buttonColor);
		}
		if (colorPressed == null) {
			colorPressed = String.valueOf(pressedColor);
		}
		if (colorBorder == null) {
			colorBorder = String.valueOf(borderColor);
		}
		if (font2 == null) {
			font2 = String.valueOf(font.getFamily());
		}
		if (fontSize2 == null) {
			fontSize2 = String.valueOf(fontSize);
		}
		if (fontBold == null) {
			fontBold = String.valueOf(bold);
		}
		if (fontColor2 == null) {
			fontColor2 = String.valueOf(fontColor);
		}
		if (opacity2 == null) {
			opacity2 = String.valueOf(opacity);
		}
		if (marginX2 == null) {
			marginX2 = String.valueOf(marginX);
		}
		if (marginY2 == null) {
			marginY2 = String.valueOf(marginY);
		}

		try {

			if (text == null || text == "") {
				rectancles.get(id).setVisible(false);
				texts.get(id).setVisible(false);
				return;
			}

			Rectangle r = new Rectangle(Double.parseDouble(width2), Double.parseDouble(height2), getColor(colorButton));
		
			
			if (rectancles.size() <= id) {

				r.setStroke(getColor(colorBorder));

				rectancles.add(r);

				Text t = new Text();

				t.setText(text);
				t.setFill(getColor(fontColor2));
				FontWeight bold = fontBold.equals("true") ? FontWeight.BOLD : FontWeight.NORMAL;

				t.setFont(Font.font(font2, bold, Double.parseDouble(fontSize2)));

				StackPane sp = new StackPane();

				double offsetX = 0;
				double offsetY = 0;

				offsetX = (id % maxColumn) * (r.getWidth() + Double.parseDouble(marginX2));
				offsetY = (id / maxColumn) * (r.getHeight() + Double.parseDouble(marginY2));

//				sp.setLayoutX(translateX + offsetX);
//				sp.setLayoutY(translateY + offsetY);
				sp.setLayoutX(offsetX);
				sp.setLayoutY(offsetY);

				primaryStage.setWidth(maxX - minX);
				primaryStage.setHeight(maxY - minY);
				primaryStage.setX(translateX);
				primaryStage.setY(translateY);
				
				Color buttonC = getColor(colorButton);
				Color pressedC = getColor(colorPressed);

				sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int id = panes.indexOf(sp);
						rectancles.get(id).setFill(buttonC);
						handleButtonClicked(texts.get(id).getText(), id);
					}
				});
				sp.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int id = panes.indexOf(sp);
						rectancles.get(id).setFill(buttonC);
					}
				});
				sp.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int id = panes.indexOf(sp);
						rectancles.get(id).setFill(pressedC);
					}
				});

				root.getChildren().add(sp);

				sp.setOpacity(Double.valueOf(opacity2));

				sp.getChildren().addAll(r, t);

				texts.add(t);
				panes.add(sp);

			} else {

				r = rectancles.get(id);
				StackPane sp = panes.get(id);

				Text t = texts.get(id);

				t.setText(text);
				t.setFill(getColor(fontColor2));
				FontWeight bold = fontBold.equals("true") ? FontWeight.BOLD : FontWeight.NORMAL;

				t.setFont(Font.font(font2, bold, Double.parseDouble(fontSize2)));

				double offsetX = 0;
				double offsetY = 0;

				offsetX = (id % maxColumn) * (r.getWidth() + Double.parseDouble(marginX2));
				offsetY = (id / maxColumn) * (r.getHeight() + Double.parseDouble(marginY2));

//				sp.setLayoutX(translateX + offsetX);
//				sp.setLayoutY(translateY + offsetY);

				sp.setLayoutX(offsetX);
				sp.setLayoutY(offsetY);

				primaryStage.setWidth(maxX - minX);
				primaryStage.setHeight(maxY - minY);
				primaryStage.setX(translateX);
				primaryStage.setY(translateY);
				
				Color buttonC = getColor(colorButton);
				Color pressedC = getColor(colorPressed);

				sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(id).setFill(buttonC);
						handleButtonClicked(texts.get(id).getText(), id);
					}
				});
				sp.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(id).setFill(buttonC);
					}
				});
				sp.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						rectancles.get(id).setFill(pressedC);
					}
				});

				sp.setOpacity(Double.valueOf(opacity2));

				// root.getChildren().add(sp);
				sp.getChildren().clear();
				sp.getChildren().addAll(r, t);
			}

			StackPane temp = panes.get(id);
			
			if(maxX < temp.getLayoutX() + r.getWidth()) {
				maxX = temp.getLayoutX() + r.getWidth() + 5;
			}
			if(maxY < temp.getLayoutY() + r.getHeight()) {
				maxY = temp.getLayoutY() + r.getHeight() + 5;
			}
			
			rectancles.get(id).setVisible(true);
			texts.get(id).setVisible(true);
			primaryStage.setWidth(maxX);
			primaryStage.setHeight(maxY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addButton(String text, String translateX2, String translateY2, String width2, String height2, String colorButton,
			String colorPressed, String colorBorder, String font2, String fontSize2, String fontBold, String fontColor2, String opacity2) {

		if (translateX2 == null) {
			translateX2 = String.valueOf(translateX);
		}
		if (translateY2 == null) {
			translateY2 = String.valueOf(translateY);
		}
		if (width2 == null) {
			width2 = String.valueOf(width);
		}
		if (height2 == null) {
			height2 = String.valueOf(height);
		}
		if (colorButton == null) {
			colorButton = String.valueOf(buttonColor);
		}
		if (colorPressed == null) {
			colorPressed = String.valueOf(pressedColor);
		}
		if (colorBorder == null) {
			colorBorder = String.valueOf(borderColor);
		}
		if (font2 == null) {
			font2 = String.valueOf(font.getFamily());
		}
		if (fontSize2 == null) {
			fontSize2 = String.valueOf(fontSize);
		}
		if (fontBold == null) {
			fontBold = String.valueOf(bold);
		}
		if (fontColor2 == null) {
			fontColor2 = String.valueOf(fontColor);
		}
		if (opacity2 == null) {
			opacity2 = String.valueOf(opacity);
		}

		Rectangle r = new Rectangle(Double.parseDouble(width2), Double.parseDouble(height2), getColor(colorButton));

		StackPane sp = new StackPane();
		r.setStroke(getColor(colorBorder));

		int idCustom = rectanclesCustom.size();

		Color buttonC = getColor(colorButton);
		Color pressedC = getColor(colorPressed);

		sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				rectanclesCustom.get(idCustom).setFill(buttonC);
				handleButtonClicked(textsCustom.get(idCustom).getText());
			}
		});
		sp.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				rectanclesCustom.get(idCustom).setFill(pressedC);
			}
		});

		rectanclesCustom.add(r);

		Text t = new Text();

		t.setText(text);
		t.setFill(getColor(fontColor2));
		FontWeight bold = fontBold.equals("true") ? FontWeight.BOLD : FontWeight.NORMAL;

		t.setFont(Font.font(font2, bold, Double.parseDouble(fontSize2)));

//		sp.setLayoutX(Double.parseDouble(translateX2));
//		sp.setLayoutY(Double.parseDouble(translateY2));

		Stage stageCustom = new Stage();
		Group rootC = new Group();
		Scene scene = new Scene(rootC, 10, 10, Color.TRANSPARENT);
		stageCustom.initStyle(StageStyle.TRANSPARENT);
		stageCustom.setTitle("ButtonFactory - Custom Button");
		stageCustom.setScene(scene);
		stageCustom.setAlwaysOnTop(true);
		stageCustom.setX(xres);
		stageCustom.setY(yres);
		stageCustom.setWidth(r.getWidth() + 5);
		stageCustom.setHeight(r.getHeight() + 5);
		
		stageCustom.setX(Double.parseDouble(translateX2));		
		stageCustom.setY(Double.parseDouble(translateY2));
		
		stages.add(stageCustom);
		scenes.add(scene);
		roots.add(rootC);
		
		
		r.setVisible(true);
		t.setVisible(true);
		sp.setVisible(true);

		sp.getChildren().addAll(r, t);

		rootC.getChildren().add(sp);
		panesCustom.add(sp);
		textsCustom.add(t);
		
		
		stageCustom.show();
		
		
	}

	@Override
	public void addButton(String text) {

		int autoPos = rectancles.size();
		for (int i = 0; i < autoPos; i++) {
			if (!rectancles.get(i).isVisible()) {
				autoPos = i;
			}
		}

		addButton(text, minPos++);

	}

	@Override
	public void addButton(String text, int id) {
		updateButton(id, text);
	}

	@Override
	public void removeButton(String text) {

		int index1 = -1;
		for (int i = 0; i < texts.size(); i++) {
			Text t = texts.get(i);
			if (t.getText().equals(text)) {
				index1 = i;
				break;
			}
		}

		final int toRemove = index1;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				root.getChildren().remove(toRemove);
			}
		});

		if (index1 < minPos) {
			minPos = index1;
		}

		texts.remove(index1);
		rectancles.remove(index1);
		panes.remove(index1);

		int index2 = -1;
		for (int i = 0; i < textsCustom.size(); i++) {
			Text t = textsCustom.get(i);
			if (t.getText().equals(text)) {
				index2 = i;
				break;
			}
		}
		final int toRemove2 = index2;
		if (index2 != -1) {
			root.getChildren().remove(toRemove2);
			textsCustom.remove(index2);
			rectanclesCustom.remove(index2);
			panesCustom.remove(index2);
		}

	}

	public void removeButton(int position) {

		minPos = 0;
		texts.remove(position);
		rectancles.remove(position);
		panes.remove(position);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				root.getChildren().remove(position);
			}
		});

	}

	@Override
	public void removeAll() {

		minPos = 0;
		texts.clear();
		rectancles.clear();
		textsCustom.clear();
		rectanclesCustom.clear();
		panes.clear();
		panesCustom.clear();	
		scenes.clear();
		stages.clear();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				root.getChildren().clear();
				for(Group r : roots) {
					r.getChildren().clear();
				}
				roots.clear();
			}
		});

	}

	@Override
	public void hideButton(String text) {

		int index = -1;
		for (int i = 0; i < texts.size(); i++) {
			Text t = texts.get(i);
			if (t.getText().equals(text)) {
				index = i;
				break;
			}
		}

		texts.get(index).setVisible(false);
		rectancles.get(index).setVisible(false);

		index = -1;
		for (int i = 0; i < textsCustom.size(); i++) {
			Text t = textsCustom.get(i);
			if (t.getText().equals(text)) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			textsCustom.get(index).setVisible(false);
			rectanclesCustom.get(index).setVisible(false);
		}

	}

	public void hideButton(int position) {

		texts.get(position).setVisible(false);
		rectancles.get(position).setVisible(false);

	}

	@Override
	public void showButton(String text) {

		int index = -1;
		for (int i = 0; i < texts.size(); i++) {
			Text t = texts.get(i);
			if (t.getText().equals(text)) {
				index = i;
				break;
			}
		}

		texts.get(index).setVisible(true);
		rectancles.get(index).setVisible(true);

		index = -1;
		for (int i = 0; i < textsCustom.size(); i++) {
			Text t = textsCustom.get(i);
			if (t.getText().equals(text)) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			textsCustom.get(index).setVisible(true);
			rectanclesCustom.get(index).setVisible(true);
		}

	}

	@Override
	public void hideAll() {

		for (int i = 0; i < rectancles.size(); i++) {
			rectancles.get(i).setVisible(false);
			texts.get(i).setVisible(false);
		}

		for (int i = 0; i < rectanclesCustom.size(); i++) {
			rectanclesCustom.get(i).setVisible(false);
			textsCustom.get(i).setVisible(false);
		}

	}

	@Override
	public void showAll() {

		for (int i = 0; i < rectancles.size(); i++) {
			rectancles.get(i).setVisible(true);
			texts.get(i).setVisible(true);
		}

		for (int i = 0; i < rectanclesCustom.size(); i++) {
			rectanclesCustom.get(i).setVisible(true);
			textsCustom.get(i).setVisible(true);
		}

	}

}
