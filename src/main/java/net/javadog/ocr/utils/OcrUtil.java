package net.javadog.ocr.utils;

import com.benjaminwan.ocrlibrary.OcrResult;
import io.github.mymonstercat.Model;
import io.github.mymonstercat.ocr.InferenceEngine;

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
            InferenceEngine engine = InferenceEngine.getInstance(Model.ONNX_PPOCR_V4);
            ImageIO.write(targetImage,"png", new File(property+"\\ocrdata\\temp.png"));
             ocrResult = engine.runOcr(property+"\\ocrdata\\temp.png");
            log.info("ocr result = {}", ocrResult.getStrRes());
        } catch (Exception e) {
            log.error("ocrSense error",e);
        }
        return ocrResult.getStrRes();

    }
}
