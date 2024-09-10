package net.javadog.ocr;

import net.javadog.ocr.main.MainContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;


import javax.swing.*;

/**
 * 启动类
 *
 */
public class Application {

    public static final Logger log = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        // 初始化
        init();
        // 窗口
        new MainContainer();
    }

    static void init(){
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        UIManager.put("RootPane.setupButtonVisible", false);
        try {
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            log.info("启动异常",e);
        }
    }

}
