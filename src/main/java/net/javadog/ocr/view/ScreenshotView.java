package net.javadog.ocr.view;

import net.javadog.ocr.main.MainContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.awt.image.RescaleOp;
import java.util.List;

/**
 * 截图视图
 *
 **/
public class ScreenshotView extends JWindow {

    /**
     * 主容器
     */
    private MainContainer mainContainer;

    /**
     * x坐标开始
     */
    private int startX;

    /**
     * y坐标开始
     */
    private int startY;

    /**
     * x坐标结束
     */
    private int endX;

    private double multiple;

    /**
     * y坐标结束
     */
    private int endY;

    /**
     * 截图图片
     */
    private BufferedImage captureImage;

    /**
     * 临时图片
     */
    private BufferedImage tempImage;

    /**
     * 选择图片
     */
    private BufferedImage selectedImage;

    /**
     * 截图中标识-默认false
     */
    private boolean screenshotting = false;

    /**
     * 构造方法
     */

    public static final Logger log = LoggerFactory.getLogger(ScreenshotView.class);

    public ScreenshotView(MainContainer mainContainer) {
        // 主
        this.mainContainer = mainContainer;
        // 获取屏幕尺寸
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsDevice defaultScreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = defaultScreenDevice.getDisplayMode().getWidth();
        int height = defaultScreenDevice.getDisplayMode().getHeight();
        // 设置边界
        this.setBounds(0, 0, width, height);
        //截取屏幕
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        MultiResolutionImage multiResolutionScreenCapture = robot.createMultiResolutionScreenCapture(new Rectangle(width, height));
        List<Image> resolutionVariants = multiResolutionScreenCapture.getResolutionVariants();
        if (resolutionVariants.size()>1){
            captureImage = (BufferedImage)resolutionVariants.get(1);
        }else {
            captureImage = (BufferedImage)resolutionVariants.get(0);
        }
        multiple= (double) captureImage.getWidth() /width;

        // 截取图片
       // captureImage = robot.createScreenCapture(new Rectangle(0, 0, dimension.width, dimension.height));
        // 添加鼠标监听事件
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // 右键取消截图
                if (e.getButton() == MouseEvent.BUTTON3) {
                    exit();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // 鼠标左键按下时记录横纵坐标
                    startX = e.getX();
                    startY = e.getY();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // 右键取消截图
                    exit();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (screenshotting) {
                        // 鼠标松开时，进行图片处理
                        screenshotting = false;
                        mainContainer.setPreviewImage(selectedImage);//设置选中的图片
                        exit();
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                screenshotting = true;
                // 鼠标拖动时，记录坐标并重绘窗口
                endX = e.getX();
                endY = e.getY();
                // 临时图像
                BufferedImage bufferImage = new BufferedImage(ScreenshotView.this.getWidth(), ScreenshotView.this.getHeight(),BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bufferImage.createGraphics();
                g.drawImage(tempImage.getScaledInstance(getWidth(),
                        getHeight(), Image.SCALE_SMOOTH), 0, 0,getWidth(),getHeight(), null);
                int x = Math.min(startX, endX);
                int y = Math.min(startY, endY);
                int width = Math.abs(endX - startX) + 1;
                int height = Math.abs(endY - startY) + 1;
                g.setColor(new Color(0, 147, 250));
                g.drawRect((x - 1), (y - 1), width + 1, height + 1);
                //selectedImage = ImageHelper.getSubImage(captureImage,(int) Math.round(x*multiple),(int)  Math.round(y*multiple),(int)  Math.round(width*multiple),(int)  Math.round(height*multiple));
                selectedImage = captureImage.getSubimage((int) Math.round(x*multiple),(int)  Math.round(y*multiple),(int)  Math.round(width*multiple),(int)  Math.round(height*multiple));
                g.drawImage(selectedImage, x, y,width,height, null);
                ScreenshotView.this.getGraphics().drawImage(bufferImage, 0, 0,getWidth(),getHeight(),null);
                g.dispose();

            }
        });
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        RescaleOp ro = new RescaleOp(1f, 0, null);
        tempImage = ro.filter(captureImage, null);
        g.drawImage(tempImage, 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * 退出
     */
    public void exit() {
        // 截图框关闭
        this.dispose();
        // 显示主容器
        mainContainer.setVisible(true);
    }

}
