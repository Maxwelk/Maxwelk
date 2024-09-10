
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class ImageUtil {
	
	/** 取得三色**/
	public static int[] decodeColor(int color, int rgb[]) {
		if(rgb == null) rgb = new int[3];
		rgb[0] = (color & 0x00ff0000) >> 16;
		rgb[1] = (color & 0x0000ff00) >> 8;
		rgb[2] = (color & 0x000000ff);	
		return rgb;
	}
	
	/** 合并三色**/
	public static int encodeColor(int rgb[]) {
		int color = (255 << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
		return color;
	}
	
}