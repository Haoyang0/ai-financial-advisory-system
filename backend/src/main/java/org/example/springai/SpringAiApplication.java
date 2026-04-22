package org.example.springai;

import org.example.springai.controller.AccountSelectionFrame; // 导入账号选择界面
import org.example.springai.service.UserManager; // 修正UserManager的包路径
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

@SpringBootApplication
public class SpringAiApplication {

    public static void main(String[] args) {
        // 禁用无头模式，支持图形界面
        new SpringApplicationBuilder(SpringAiApplication.class)
                .headless(false)
                .run(args);
    }

    // 启动时显示账号选择界面（而非直接显示登录界面）
    @Bean
    CommandLineRunner startApplication(UserManager userManager, ChatClient chatClient, RestTemplate restTemplate) {
        return args -> {
            SwingUtilities.invokeLater(() -> {
                // 初始化账号管理器并显示账号选择界面
                new AccountSelectionFrame(userManager, chatClient,restTemplate).setVisible(true);
            });
        };
    }
}