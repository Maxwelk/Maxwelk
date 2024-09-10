package net.javadog.ocr.main;


import net.javadog.ocr.dialog.ChooseFileDialog;
import net.javadog.ocr.utils.OcrUtil;
import net.javadog.ocr.view.ScreenshotView;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 主容器
 **/
public class MainContainer extends JFrame {

    /**f
     * 顶部工具面板.
     */
    private JPanel topToolsPanel;

    /**
     * 中部展示面板.
     */
    private JSplitPane centerShowPanel;

    /**
     * 顶部工具按钮.
     * openBtn-打开图片
     * screenshotBtn-截图按钮
     * ocrBtn-ocr识别按钮
     */
    private JButton openBtn, screenshotBtn,ocrBtn;

    /**
     * 翻译结果.
     */
    private JTextArea translateResultRrea;

    /**
     * 预览标签.
     */
    private JLabel previewLabel;

    /**
     * ocr图片.
     */
    private BufferedImage ocrImage;

    /**
     * 定义进度条组件.
     */
    public JProgressBar progressbar;

    /**
     * 语言映射关系.
     */
    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        // 中文
        LANGUAGE_MAP.put("中文", "EZ-chi_sim SIM");
        // 英文
        LANGUAGE_MAP.put("英文", "ZK-eng");
    }

    public MainContainer() throws HeadlessException {
        // 初始化主容器
        this.initMainContainer();
        // 初始化顶部工具面板
        this.initTopToolsPanel();
        // 初始化顶部工具面板
        this.initCenterShowPanel();
        // 初始化最后handle
        this.initLastHandle();
    }

    /**
     * 初始化主容器
     */
    public void initMainContainer(){
        // 设置标题
        this.setTitle("ocr简易图像识别");
        // 设置主框架
        this.setSize(new Dimension(800, 600));
        // 窗口位置，如果为null则默认居中
        this.setLocationRelativeTo(null);
        // 默认关闭操作
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置iocn
        this.setIconImage(getLocalIconImage("5199953.png"));

    }

    /**
     * 初始化顶部工具面板
     */
    public void initTopToolsPanel(){
        topToolsPanel = new JPanel();
        // 设置边框
        topToolsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // 设置高度
        topToolsPanel.setPreferredSize(new Dimension(1240, 56));
        // 设置布局
        topToolsPanel.setLayout(new GridLayout(1, 6, 10, 10));

//        // 打开图片按钮布局
//        openBtn = new JButton("打开");
//        // 按钮字体字号
//        openBtn.setFont(new Font("宋体", Font.PLAIN, 14));
//        // 按钮设置是否聚焦-默认false
//        openBtn.setFocusable(false);
//        // 按钮点击事件
//        openBtn.addActionListener(e -> showOpenFileDialog());

        // 截图按钮
        screenshotBtn = new JButton("截图");
        // 设置按钮字体白色
        screenshotBtn.setForeground(Color.WHITE);
//        ImageIcon imageIcon = new ImageIcon(getLocalIconImage("img.png"));
//        Image image = imageIcon.getImage();
//        Image newimage = image.getScaledInstance(50, 40, Image.SCALE_SMOOTH);
//        ImageIcon imageIcon1 = new ImageIcon(newimage);
//        screenshotBtn.setIcon(imageIcon1);

        // 按钮字体字号
        screenshotBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        // 按钮设置是否聚焦-默认false
        screenshotBtn.setFocusable(false);
        // 按钮设置UI
        screenshotBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
        // 按钮点击事件
        screenshotBtn.addActionListener(e -> {
            // 主容器设置隐藏，进行截图
            this.setVisible(false);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            new ScreenshotView(this);//创建截图窗口
        });

        // 识别按钮
        ocrBtn = new JButton("OCR识别");
        // 设置按钮字体白色
        ocrBtn.setForeground(Color.WHITE);
        // 按钮字体字号
        ocrBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        // 按钮设置是否聚焦-默认false
        ocrBtn.setFocusable(false);
        // 按钮设置UI
        ocrBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        // 按钮点击事件
        ocrBtn.addActionListener(e -> ocrSense());

        // 顶部面板放入操作按钮
        //topToolsPanel.add(openBtn);
        topToolsPanel.add(screenshotBtn);
        topToolsPanel.add(ocrBtn);
        this.add(topToolsPanel, BorderLayout.NORTH);
    }

    /**
     * 初始化中间展示面板
     */
    public void initCenterShowPanel(){
        centerShowPanel = new JSplitPane();
        // 设置预览面板
        previewLabel = new JLabel();
        // 文字水平对齐方式居中
        previewLabel.setHorizontalTextPosition(JLabel.CENTER);
        previewLabel.setToolTipText("鼠标右键粘贴");
        previewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击选中
                if(e.getButton() == 3) {
                    BufferedImage clipboardString = getClipboardString();
                    setPreviewImage(clipboardString);
                }
            }
        });
        // 生成左侧滚动面板
        JScrollPane leftPane = new JScrollPane(previewLabel);
        // 设置边框为空
        leftPane.setBorder(null);
        // 放入左侧组件
        centerShowPanel.setLeftComponent(leftPane);
        // 设置右边结果显示面板
        translateResultRrea = new JTextArea();
        // 设置翻译结果字体字号
        translateResultRrea.setFont(new Font("宋体", Font.BOLD, 18));
        // 设置翻译结果区域无边框
        translateResultRrea.setBorder(null);
        // 设置翻译结果区域不可编辑
        translateResultRrea.setEditable(false);
        JScrollPane rightPane = new JScrollPane(translateResultRrea);
        // 设置边框为空
        rightPane.setBorder(null);
        //
        rightPane.setPreferredSize(new Dimension(200,200));
        // 放入右侧组件
        centerShowPanel.setRightComponent(rightPane);
        centerShowPanel.setBorder(null);
        this.add(centerShowPanel, BorderLayout.CENTER);
    }

    /**
     * 初始化最后handle
     */
    public void initLastHandle(){
        // 设置是否展示
        this.setVisible(true);
        // 分割线左右占比
        centerShowPanel.setDividerLocation(0.5);
    }

    /**
     * ocr图片识别
     */
    private void ocrSense(){
        final JDialog loading = new JDialog(this);
        loadingProgressbar();
        loading.setPreferredSize(new Dimension(150, 30));
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(progressbar, BorderLayout.CENTER);
        loading.setUndecorated(true);
        loading.getContentPane().add(p1);
        loading.pack();
        loading.setLocationRelativeTo(this);
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loading.setModal(true);
        loadingProgressbar();
        if(ocrImage==null){
            JOptionPane.showMessageDialog(null, "未选择图片!", "提示", JOptionPane. ERROR_MESSAGE);
            return;
        }

        SwingWorker<String, Void> worker = getStringVoidSwingWorker(loading);
        loading.setVisible(true);
        try {
            worker.get(); //此处父线程等待完成
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private SwingWorker<String, Void> getStringVoidSwingWorker(JDialog loading) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            String result = "";
            @Override
            protected String doInBackground()  {
                final String ocrStr = OcrUtil.ocrSense(ocrImage);
                StringBuilder stringBuilder = new StringBuilder(ocrStr);
                if (ocrStr !=null &&ocrStr.length()>1) {
                    for (int i = 1; i <stringBuilder.length()-1; i++) {
                        if (stringBuilder.charAt(i)==' '){
                            char b = stringBuilder.charAt(i-1);
                            char d = stringBuilder.charAt(i+1);
                            if ((b>=0x4e00&&b<=0x9fbb)||(d>=0x4e00&&d<=0x9fbb)){
                                stringBuilder.deleteCharAt(i);
                                i--;
                            }
                        }
                    }
                }
                result = stringBuilder.toString();
                translateResultRrea.setText(result);
                translateResultRrea.setMargin(new Insets(10,10,10,10));
                return null;
            }
            @Override
            protected void done() {
                loading.dispose();
            }
        };
        worker.execute(); //在这里进程线程启动
        return worker;
    }

    /**
     * 加载进度条
     */
    private void loadingProgressbar(){
        // 创建进度条
        progressbar = new JProgressBar();
        // 显示当前进度值信息
        progressbar.setStringPainted(true);
        // 设置进度条边框不显示
        progressbar.setBorderPainted(false);
        progressbar.setIndeterminate(false);
        progressbar.setBounds(0, 1280 - 15, 880, 15);
        progressbar.setString("OCR识别中,请等待...");
        progressbar.setValue(80);
        //this.add(progressbar);
    }

    /**
     * 选择图片Dialog并获取文件
     */
    private void showOpenFileDialog() {
        final String filePath = ChooseFileDialog.getFilePath(this);
        ImageIcon imageIcon = new ImageIcon(filePath);
        previewLabel.setIcon(imageIcon);
        try {
            ocrImage = ImageIO.read(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 预览图片
     * @param image
     */
    public void setPreviewImage(BufferedImage image) {
        if (image != null) {
            ImageIcon previewImage = new ImageIcon(image);
            previewLabel.setIcon(previewImage);
            ocrImage = image;
        }
    }

    /**
     * 获取icon
     * @return Image
     */
    private Image getLocalIconImage(String name){
        Image image = null;
        try {
            InputStream resourceAsStream = MainContainer.class.getClassLoader().getResourceAsStream("img/5199953.png");
            image = ImageIO.read(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 获取剪切板中的图片
     * @return
     */
    public BufferedImage getClipboardString() {
        //获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //获取剪贴板内容
        Transferable trans = clipboard.getContents(null);

        if(trans != null) {
            System.out.println(trans.isDataFlavorSupported(DataFlavor.imageFlavor));
            //判断剪贴板内容是否支持图片
            if(trans.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                BufferedImage clipboardImg = null;
                try {
                    //获取剪贴板的图片内容
                    clipboardImg = (BufferedImage)trans.getTransferData(DataFlavor.imageFlavor);
                } catch (UnsupportedFlavorException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return clipboardImg;
            }
        }
        return null;
    }
}
