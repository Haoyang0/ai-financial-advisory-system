
package org.example.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.client.RestTemplate;
import org.example.springai.service.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 账号选择界面：显示所有已注册账号，支持选择账号登录或创建新账号
 */
public class AccountSelectionFrame extends JFrame {
    private final UserManager userManager;
    private final ChatClient chatClient;
    private JList<String> userList;
    private String selectedUsername;

    private final RestTemplate restTemplate;

    public AccountSelectionFrame(UserManager userManager, ChatClient chatClient,RestTemplate restTemplate) {
        this.userManager = userManager;
        this.chatClient = chatClient;
        this.restTemplate = restTemplate;
        initUI();
    }

    private void initUI() {
        // 窗口设置
        setTitle("选择账号");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("请选择账号或创建新账号");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 账号列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        userManager.getAllUsernames().forEach(listModel::addElement);

        userList = new JList<>(listModel);
        userList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击账号直接登录
                    selectedUsername = userList.getSelectedValue();
                    openLoginFrame();
                }
            }
        });

        // 列表选择事件
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedUsername = userList.getSelectedValue();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(userList);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 登录按钮
        JButton loginButton = new JButton("登录所选账号");
        loginButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        loginButton.addActionListener(this::loginSelected);

        // 创建新账号按钮
        JButton createButton = new JButton("创建新账号");
        createButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        createButton.addActionListener(this::openRegisterFrame);

        JButton deleteButton = new JButton("删除所选账号");
        deleteButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        deleteButton.addActionListener(this::deleteSelectedUser);

        buttonPanel.add(loginButton);
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // 登录所选账号
    private void loginSelected(ActionEvent e) {
        if (selectedUsername == null || selectedUsername.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择一个账号", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openLoginFrame();
    }

    // 打开登录界面
    private void openLoginFrame() {
        this.setVisible(false);
        new LoginFrame(userManager, chatClient, selectedUsername,restTemplate).setVisible(true);
    }

    // 打开注册界面
    private void openRegisterFrame(ActionEvent e) {
        this.setVisible(false);
        new RegisterFrame(userManager, this).setVisible(true);
    }

    // 刷新账号列表（注册新账号后调用）
    public void refreshUserList() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        userManager.getAllUsernames().forEach(listModel::addElement);
        userList.setModel(listModel);
    }
    // 在AccountSelectionFrame中新增方法
    private void deleteSelectedUser(ActionEvent e) {
        if (selectedUsername == null || selectedUsername.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的账号", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 显示确认对话框
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除账号 '" + selectedUsername + "' 吗？\n删除后数据将无法恢复！",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // 调用UserManager删除账号
            boolean isDeleted = userManager.deleteUser(selectedUsername);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "账号 '" + selectedUsername + "' 已成功删除", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshUserList(); // 刷新账号列表
                selectedUsername = null; // 重置选中状态
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
