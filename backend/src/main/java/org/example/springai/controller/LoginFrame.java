package org.example.springai.controller;

import org.example.springai.service.UserManager;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    // 移除硬编码的账号密码，改用UserManager管理
    private final UserManager userManager;
    private final String username; // 统一变量名为username（与其他类保持一致）
    private final ChatClient chatClient;

    private JTextField usernameField;
    private JPasswordField passwordField;

    private final RestTemplate restTemplate;

    // 构造方法：接收UserManager、ChatClient和选中的用户名
    public LoginFrame(UserManager userManager, ChatClient chatClient, String username,RestTemplate restTemplate) {
        this.userManager = userManager;
        this.chatClient = chatClient;
        this.username = username; // 接收从账号选择界面传来的用户名
        this.restTemplate = restTemplate;
        initUI();
    }

    private void initUI() {
        // 窗口设置
        setTitle("AI assistant Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 标题
        JLabel titleLabel = new JLabel("please login");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // 用户名标签
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userLabel = new JLabel("username:");
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(userLabel, gbc);

        // 用户名输入框：自动填充选中的用户名，且不可编辑
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        usernameField.setText(username); // 显示选中的用户名
        usernameField.setEditable(false); // 禁止修改（因为是从账号列表选择的）
        mainPanel.add(usernameField, gbc);

        // 密码标签
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("password:");
        passLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(passLabel, gbc);

        // 密码输入框
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton backButton = new JButton("back");
        backButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        backButton.addActionListener(this::backToAccountSelection); // 绑定返回事件
        mainPanel.add(backButton, gbc);

        // 登录按钮
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("login");
        loginButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.addActionListener(new LoginListener());
        mainPanel.add(loginButton, gbc);

        add(mainPanel);
    }
    private void backToAccountSelection(ActionEvent e) {
        this.setVisible(false); // 隐藏当前登录界面
        // 重新显示账号选择界面
        AccountSelectionFrame accountFrame = new AccountSelectionFrame(userManager, chatClient,restTemplate);
        accountFrame.setVisible(true);
    }

    // 登录事件监听器：使用UserManager验证密码
    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 密码从输入框获取，用户名为选中的username（无需再从输入框读取）
            String password = new String(passwordField.getPassword()).trim();

            // 验证输入
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "password cannot be empty", "prompt", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 关键：使用UserManager验证账号密码（而非硬编码）
            if (userManager.validateUser(username, password)) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "success，" + username + "！", "success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 关闭登录窗口

                // 打开聊天界面
                SwingChatClient chatClient = new SwingChatClient(LoginFrame.this.chatClient,userManager,username,restTemplate);
                SwingUtilities.invokeLater(() -> {
                    chatClient.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "wrong password", "wrong", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // 清空密码框
            }
        }
    }
}