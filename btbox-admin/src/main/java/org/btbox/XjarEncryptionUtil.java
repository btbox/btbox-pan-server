package org.btbox;

import io.xjar.XKit;
import io.xjar.boot.XBoot;
import io.xjar.key.XKey;
import org.btbox.common.core.utils.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 加密工具，针对java开发项目jar包进行加密，jar加密后，大小会翻倍，同时swagger无法访问，接口暂时未发现问题
 */
public class XjarEncryptionUtil {

    public static void main(String[] args) {
        createJFrame();
    }

    /**
     * 创建面板，这个类似于 HTML 的 div 标签,我们可以创建多个面板并在 JFrame 中指定位置,面板中我们可以添加文本字段，按钮及其他组件。
     */
    public static void createJFrame() {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("XJar加密，防止反编译");
        // 设置窗口大小
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建面板，这个类似于 HTML 的 div 标签,可以创建多个面板并在 JFrame 中指定位置,
        // 面板中可以添加文本字段，按钮及其他组件。
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        // 调用用户定义的方法并添加组件到面板
        addComponents(panel);
        // 设置界面可见
        frame.setVisible(true);
    }

    /**
     * 调用用户定义的方法并添加组件到面板
     */
    private static void addComponents(JPanel panel) {
        // 布局部分我们这边不多做介绍,这边设置布局为 null
        panel.setLayout(null);

        // 创建 需要加密的jar JLabel
        JLabel fromLabel = new JLabel("选择加密的jar：");
        fromLabel.setBounds(10, 20, 120, 30);
        panel.add(fromLabel);

        // 用于记录未加密jar的文本域
        final JTextField fromText = new JTextField(20);
        fromText.setBounds(100, 20, 300, 30);
        panel.add(fromText);

        // 创建选择按钮
        JButton fromButton = new JButton("选择");
        fromButton.setBounds(420, 20, 80, 30);
        panel.add(fromButton);

        // 加密后jar要保存的位置
        JLabel toLabel = new JLabel("选择保存位置：");
        toLabel.setBounds(10, 50, 120, 30);
        panel.add(toLabel);

        // 文本域用于记录保存路径
        final JTextField toText = new JTextField(20);
        toText.setBounds(100, 50, 300, 30);
        panel.add(toText);

        // 创建选择按钮
        JButton toButton = new JButton("选择");
        toButton.setBounds(420, 50, 80, 30);
        panel.add(toButton);

        // 输入密码的文本域
        JLabel passwordLabel = new JLabel("加密密码：");
        passwordLabel.setBounds(10, 80, 80, 30);
        panel.add(passwordLabel);

        // 这个类似用于输入的文本域,但是输入的信息会以点号代替，用于包含密码的安全性
        final JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 80, 300, 30);
        panel.add(passwordText);

        // 创建开始按钮
        JButton startButton = new JButton("加密");
        startButton.setBounds(100, 110, 80, 30);
        panel.add(startButton);
        // 创建开始按钮
        JButton endButton = new JButton("解密");
        endButton.setBounds(200, 110, 80, 30);
        panel.add(endButton);

        // 日志显示框
        final JTextArea textArea = new JTextArea("");
        textArea.setBounds(10, 150, 550, 150);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        panel.add(textArea);

        // 选择jar按钮监听事件
        fromButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {  // 按钮点击事件
                JFileChooser chooser = new JFileChooser();             // 设置选择器
                chooser.setMultiSelectionEnabled(false);             // 设为单选
                int returnVal = chooser.showOpenDialog(null);        // 是否打开文件选择框
                if (returnVal == JFileChooser.APPROVE_OPTION) {          // 如果符合文件类型
                    String filepath = chooser.getSelectedFile().getAbsolutePath();      // 获取绝对路径
                    if (!".jar".equals(filepath.substring(filepath.length() - 4))) {
                        JOptionPane.showMessageDialog(null, "文件格式不正确，请选择jar文件！", "文件格式错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        fromText.setText(filepath);
                    }
                    System.out.println(filepath);
                }
            }
        });

        // 选择加密后jar保存路径按钮监听事件
        toButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {  // 按钮点击事件
                JFileChooser chooser = new JFileChooser();             // 设置选择器
                chooser.setMultiSelectionEnabled(false);             // 设为单选
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 设置只选目录
                chooser.setDialogTitle("选择加密后jar保存位置");
                int returnVal = chooser.showOpenDialog(null);        // 是否打开文件选择框
                if (returnVal == JFileChooser.APPROVE_OPTION) {          // 如果符合文件类型
                    String filepath = chooser.getSelectedFile().getAbsolutePath();      // 获取绝对路径
                    toText.setText(filepath);
                    System.out.println(filepath);
                }
            }
        });

        // 选择开始径按钮的监听事件
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fromJarPath = fromText.getText();
                String toJarPath = toText.getText() + "\\encrypt" + getNowDateTime() + ".jar";
                String password = new String(passwordText.getPassword());
                System.out.println("fromJarPath=" + fromJarPath);
                System.out.println("toJarPath=" + toJarPath);
                System.out.println("password=" + password);
                if (StringUtils.isEmpty(fromJarPath)) {
                    JOptionPane.showMessageDialog(null, "jar文件不能为空！", "jar文件不能为空！", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (StringUtils.isEmpty(toJarPath)) {
                    JOptionPane.showMessageDialog(null, "保存路径不能为空!", "保存路径不能为空！", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (StringUtils.isEmpty(toJarPath)) {
                    JOptionPane.showMessageDialog(null, "请输入加密的密码！", "密码不能为空！", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 打印输入日志
                StringBuilder builder = new StringBuilder();
                builder.append("fromJarPath=" + fromJarPath + "\n");
                builder.append("toJarPath=" + toJarPath + "\n");
                textArea.setText(builder.toString());
                // 开始加密文件
                encryptJar(fromJarPath, toJarPath, password);
                textArea.append("jar加密成功！\n请测试接口是否正常（注意：Swagger不可用）");
            }
        });
        // 选择开始径按钮的监听事件
        endButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fromJarPath = fromText.getText();
                String toJarPath = toText.getText() + "\\encrypt" + getNowDateTime() + ".jar";
                String password = new String(passwordText.getPassword());
                System.out.println("fromJarPath=" + fromJarPath);
                System.out.println("toJarPath=" + toJarPath);
                System.out.println("password=" + password);
                if (StringUtils.isEmpty(fromJarPath)) {
                    JOptionPane.showMessageDialog(null, "jar文件不能为空！", "jar文件不能为空！", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (StringUtils.isEmpty(toJarPath)) {
                    JOptionPane.showMessageDialog(null, "保存路径不能为空!", "保存路径不能为空！", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (StringUtils.isEmpty(toJarPath)) {
                    JOptionPane.showMessageDialog(null, "请输入加密的密码！", "密码不能为空！", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 打印输入日志
                StringBuilder builder = new StringBuilder();
                builder.append("fromJarPath=" + fromJarPath + "\n");
                builder.append("toJarPath=" + toJarPath + "\n");
                textArea.setText(builder.toString());
                // 开始加密文件
                decryptJar(fromJarPath, toJarPath, password);
                textArea.append("jar加密成功！\n请测试接口是否正常（注意：Swagger不可用）");
            }
        });
    }

    /**
     * 获取当前格式化时间的方法
     */
    private static String getNowDateTime() {
        String dateNow = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        dateNow = format.format(date);
        return dateNow;
    }

    /**
     * jar包加密，防止反编译
     * 此编译方式在运行jar包时需要输入密码
     * 运行方式一 ：
     * // 命令行运行JAR 然后在提示输入密码的时候输入密码后按回车即可正常启动
     * java -jar /path/to/encrypted.jar
     * 运行方式二：
     * // 也可以通过传参的方式直接启动，不太推荐这种方式，因为泄露的可能性更大！
     * java -jar /path/to/encrypted.jar --xjar.password=PASSWORD
     * 运行方式三：
     * // 对于 nohup 或 javaw 这种后台启动方式，无法使用控制台来输入密码，推荐使用指定密钥文件的方式启动
     * nohup java -jar /path/to/encrypted.jar --xjar.keyfile=/path/to/xjar.key
     * xjar.key 文件说明：
     * 格式：
     * password: PASSWORD
     * algorithm: ALGORITHM
     * keysize: KEYSIZE
     * ivsize: IVSIZE
     * hold: HOLD
     * 参数说明：
     * password    密码  无   密码字符串
     * algorithm   密钥算法    AES     支持JDK所有内置算法，如AES / DES ...
     * keysize     密钥长度    128     根据不同的算法选取不同的密钥长度。
     * ivsize  向量长度    128     根据不同的算法选取不同的向量长度。
     * hold    是否保留    false   读取后是否保留密钥文件。
     *
     * @param fromJarPath 需要加密的jar
     * @param toJarPath   加密后的jar
     * @param password    加密密码
     */
    public static void encryptJar(String fromJarPath, String toJarPath, String password) {

        try {
            // Spring-Boot Jar包加密
            XKey xKey = XKit.key(password);
            XBoot.encrypt(fromJarPath, toJarPath, xKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * jar包解密
     *
     * @param fromJarPath 已通过Xjar加密的jar文件路径
     * @param toJarPath   解密后的jar文件
     * @param password    密码
     */
    public static void decryptJar(String fromJarPath, String toJarPath, String password) {
        try {
            XKey xKey = XKit.key(password);
            XBoot.decrypt(fromJarPath, toJarPath, xKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
