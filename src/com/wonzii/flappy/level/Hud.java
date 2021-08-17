package com.wonzii.flappy.level;

public class Hud {

    private static final int FONT_COLS = 16;
    
    private static final int FONT_ROWS = 16;

    private static final String FONT_TEXTURE = "res/font_texture.png";


    private final TextItem statusTextItem;


    public TextItem getStatusTextItem() {
		return statusTextItem;
	}


	public Hud(String statusText) throws Exception {
        this.statusTextItem = new TextItem(statusText, FONT_TEXTURE, FONT_COLS, FONT_ROWS);
     
    }
	
}
