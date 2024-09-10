package net.javadog.ocr.utils;

import com.benjaminwan.ocrlibrary.OcrResult;
import io.github.mymonstercat.Model;
import io.github.mymonstercat.ocr.InferenceEngine;

import io.github.mymonstercat.ocr.config.ParamConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 百度ocr识别工具
 *
 **/
public class OcrUtil {
    public static final Logger log = LoggerFactory.getLogger(OcrUtil.class);
    /**
     * 识别
     */
    public static String ocrSense(BufferedImage targetImage) {
        String result = "";
        OcrResult ocrResult = null;
        try {
              String property = System.getProperty("user.dir");
            //tesseract.setDatapath(property+"\\ocrdata\\");
            //tesseract.setDatapath("C:\\wxy11\\ocrdata");
            //tesseract.setLanguage("eng+chi_sim");
           // tesseract.setTessVariable("user_defined_dpi","300");
            //result = tesseract.doOCR(bufferedImage);
            //BufferedImage bufferedImage = image_Heighten(binarizeImage(targetImage));
            InferenceEngine engine = InferenceEngine.getInstance(Model.ONNX_PPOCR_V4);

            ImageIO.write(targetImage,"png", new File(property+"\\ocrdata\\temp.png"));
            //ImageIO.write(targetImage,"png", new File("C:\\wxy11"+"\\ocrdata\\temp.png"));
            ParamConfig paramConfig = new ParamConfig();
            //paramConfig.setMaxSideLen(2);
            paramConfig.setMostAngle(true);
            paramConfig.setDoAngle(true);
            ocrResult = engine.runOcr(property+"\\ocrdata\\temp.png",paramConfig);
            //ocrResult = engine.runOcr("C:\\wxy11"+"\\ocrdata\\temp.png",paramConfig);
            log.info("ocr result = {}", ocrResult.getStrRes());
        } catch (Exception e) {
            log.error("ocrSense error",e);
        }
        return ocrResult.getStrRes();

    }

    public static BufferedImage image_Heighten(BufferedImage leftImage) {
        int width = leftImage.getWidth();
        int height = leftImage.getHeight();
        int srcRGBs[] = leftImage.getRGB(0, 0, width, height, null, 0, width);
        int rgb[] = new int[3];
        BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int fmin = 0, fmax = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                ImageUtil.decodeColor(srcRGBs[j * width + i], rgb);
                if (j == 0 && i == 0) {
                    fmin = rgb[1];
                    fmax = rgb[2];
                }
                if (rgb[1] < fmin) {
                    fmin = rgb[1];
                }
                if (rgb[1] > fmax) {
                    fmax = rgb[1];
                }
            }
        }
        double a = 255.0 / (fmax - fmin);
        double b = 255.0 - a * fmax;
        System.out.println("fmax=" + fmax + " fmin=" + fmin + " a=" + a + " b=" + b);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                ImageUtil.decodeColor(srcRGBs[j * width + i], rgb);
                rgb[0] = (int) (a * rgb[0] + b);
                rgb[1] = (int) (a * rgb[1] + b);
                rgb[2] = (int) (a * rgb[2] + b);
                destImage.setRGB(i, j, ImageUtil.encodeColor(rgb));
            }
        }
        return destImage;
    }

    private static BufferedImage binarizeImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // 设置阈值
        int threshold = 128;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取像素颜色
                int rgb = originalImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // 转换为灰度值
                int gray = (red + green + blue) / 3;
                // 二值化处理
                if (gray < threshold) {
                    binaryImage.setRGB(x, y, 0xFF000000); // 黑色
                } else {
                    binaryImage.setRGB(x, y, 0xFFFFFFFF); // 白色
                }
            }
        }

        return binaryImage;
    }



}
