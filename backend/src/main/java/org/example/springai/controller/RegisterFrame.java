package org.example.springai.controller;

import org.example.springai.service.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 账号注册界面
public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private final UserManager userManager;
    private final AccountSelectionFrame parentFrame;

    public RegisterFrame(UserManager userManager, AccountSelectionFrame parentFrame) {
        this.userManager = userManager;
        this.parentFrame = parentFrame;
        initUI();
    }

    private void initUI() {
        setTitle("注册新账号");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭当前窗口
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 标题
        JLabel titleLabel = new JLabel("创建新账号");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // 用户名
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel confirmLabel = new JLabel("确认密码:");
        confirmLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        mainPanel.add(confirmPasswordField, gbc);

        // 注册按钮
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("注册");
        registerButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        registerButton.setPreferredSize(new Dimension(100, 30));
        registerButton.addActionListener(new RegisterListener());
        mainPanel.add(registerButton, gbc);


        add(mainPanel);
    }

    private class RegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            // 输入验证
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "用户名和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 注册账号
            if (userManager.createUser(username, password)) {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 刷新账号选择界面的账号列表
                parentFrame.refreshUserList();
                // 关闭注册窗口
                dispose();
                parentFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(RegisterFrame.this,
                        "注册失败，用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
