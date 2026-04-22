package org.example.springai.controller;

import org.example.springai.service.UserManager;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.List;


public class SwingChatClient extends JFrame {

    private final ChatClient chatClient;
    private final UserManager userManager; // 新增：用于登录界面的用户验证
    private final String currentUser;
    private JTextArea chatArea;
    private JTextField inputField;
    private List<String> conversationHistory;

    private static final int CHAT_FONT_SIZE = 16;
    private static final int INPUT_FONT_SIZE = 16;

    private final RestTemplate restTemplate;


    // 修正：注入ChatClient和UserManager（需要Spring支持自动注入UserManager）
    public SwingChatClient(ChatClient chatClient, UserManager userManager, String username ,RestTemplate restTemplate) {
        this.chatClient = chatClient;
        this.userManager = userManager; // 初始化userManager
        this.currentUser = username;
        this.restTemplate = restTemplate;
        this.conversationHistory = userManager.getConversation(currentUser);
        initUI();
    }

    public void initUI() {
        setTitle("AI " + currentUser);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 新增：创建主面板（解决mainPanel未定义问题）
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 添加返回登录按钮（放在顶部）
        JButton backToLoginButton = new JButton("返回登录");
        backToLoginButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        backToLoginButton.addActionListener(e -> backToLogin());
        mainPanel.add(backToLoginButton, BorderLayout.NORTH); // 按钮放在主面板顶部

        // 聊天记录区域
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, CHAT_FONT_SIZE));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER); // 聊天区域放在主面板中间

        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        inputField.setFont(new Font("Microsoft YaHei", Font.PLAIN, INPUT_FONT_SIZE));
        JButton sendButton = new JButton("发送");
        sendButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, INPUT_FONT_SIZE));
        sendButton.addActionListener(this::sendMessage);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(null);
                }
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(inputPanel, BorderLayout.SOUTH); // 输入区域放在主面板底部

        // 将主面板添加到窗口
        add(mainPanel);

        // 初始消息
        if (!conversationHistory.isEmpty()) {
            // 显示历史记录（格式化展示）
            for (String history : conversationHistory) {
                if (history.startsWith("user:")) {
                    chatArea.append("你：" + history.replace("user:", "") + "\n");
                } else if (history.startsWith("assistant:")) {
                    chatArea.append("AI：" + history.replace("assistant:", "") + "\n\n");
                }
            }
        } else {
            chatArea.append("AI：你好！有什么可以帮助你的吗？\n\n");
        }
    }

    // 实现返回登录界面的逻辑
    private void backToLogin() {
        userManager.saveConversation(currentUser, conversationHistory);
        this.setVisible(false); // 隐藏当前AI界面
        // 跳转到账号选择界面（而非直接登录界面，符合之前的流程）
        AccountSelectionFrame accountFrame = new AccountSelectionFrame(userManager, chatClient,restTemplate);
        accountFrame.setVisible(true); // 显示账号选择界面
    }

    // 发送消息方法（保持不变）
    private void sendMessage(ActionEvent e) {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        conversationHistory.add("user:" + userInput);
        chatArea.append("你：" + userInput + "\n");
        inputField.setText("");

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("请基于以下对话历史，回复用户最新的问题：\n");
        for (String history : conversationHistory) {
            promptBuilder.append(history).append("\n");
        }
        String fullPrompt = promptBuilder.toString();

        chatArea.append("AI：正在思考...\n");
        try {
            String aiResponse = restTemplate.getForObject(
                    "http://localhost:8080/ai/chat?message={prompt}",
                    String.class,fullPrompt
            );

            conversationHistory.add("assistant:" + aiResponse);
            userManager.saveConversation(currentUser,conversationHistory);
            String currentText = chatArea.getText();
            currentText = currentText.replace("AI：正在思考...\n", "");
            chatArea.setText(currentText);
            chatArea.append("AI：" + aiResponse + "\n\n");
        } catch (Exception ex) {
            chatArea.append("AI：抱歉，处理请求时出错了，请重试！\n\n");
            ex.printStackTrace();
        }

        chatArea.setCaretPosition(chatArea.getDocument().getLength());

    }
}