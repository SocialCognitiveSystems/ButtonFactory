ButtonFactory via IPAACA:

Description: Creates AlwaysOnTop Buttons, listening to ipaaca categories.

How to compile and run: run 'ant' in this folder, or 'ant test'

IMPORTANT (if ButtonFactory won't run): 
java -version should print "Java(TM) SE Runtime Environment (build 1.8.***)"
The SE Runtime instead of openjdk is required because of javafx. This might change in the future.

WARNING: AlwaysOnTop functionality depends on window manager. Recommended: xfce4.

ipaaca inputbuffer category:		touch_ui_request
ipaaca inputbuffer-config category:	touch_ui_config
ipaaca outputbuffer category:		touch_ui_reply

Available uses as request (key,value):

Multiple values are allowed when separated by pipes, e.g.: "texts":"Ja|Nein|Vielleicht"

Quotes, semi-colons and square brackets need to be replaced.

"texts":"[buttontexts]"
"images":"[absolute paths to images, requires: handles]"
"handles":"[sets output of imagebuttons, like texts for normal buttons]"
"positions":"[start at 0, default=end of list]"
"remove":"[requires:texts or positions]"
"hide":"[requires:texts or positions]"
"show":"[requires:texts or positions]"
"cmd":["removeAll" OR "hideAll" OR "showAll"]


The following keys can be used to set the global config (by sending it to the touch_ui_config channel).
Most of them can also be used on touch_ui_request to modify a single button.
Warning: Using translateX|Y for a single button disconnects it from the grid and its ids.


"translateX":"[between 0..1 as percentage of screen, >1 in pixel, default=0.5]"
"translateY":"[between 0..1 as percentage of screen, >1 in pixel, default=0.5]"
"width":"[in pixel, default=300]"
"height":"[in pixel, default=100]"
"imageWidth":"[in pixel, default=50]"
"imageHeight":"[in pixel, default=50]"
"marginX":"[margin between two buttons in pixel, default=20]"
"marginY":"[margin between two buttons in pixel, default=20]"
"colorButton":"[color as string or 0xRRGGBB, default=green]"
"colorPressed":"[color as string or 0xRRGGBB, default=beige]"
"colorBorder":"[color as string or 0xRRGGBB, default=black]"
"font":"[string, default=arial]"
"fontSize":"[int, default=24]"
"fontColor":"[color as string or 0xRRGGBB, default=white]"
"fontBold":"[true or false, default=true]"
"opacity":"[0..1, default=1]"
"maxRow":"[int, default=3]"
"maxColumn":"[int, default=2]"

touch_ui_reply:

"text":"[text or handle]"
"position":"[position]"
"error":"[errorsource]"
"log":"[information]"

Examples and tests:
Running 'ant test' shows lots of usecases, how to create the IUs is shown in src/main/java/TestUp.java

Troubleshooting:

If you need quotes on a button, go to IpaacaControl.java and change 'filterQuotes' to false;
