package org.example.springai.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 账号管理器：负责账号的创建、存储、加载和验证
 */
@Service
public class UserManager {
    // Web 后端：并发安全
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userConversations = new ConcurrentHashMap<>();

    private static final String USER_FILE = "users.dat";
    private static final String CONVERSATION_FILE = "conversations.dat";

    public UserManager() {
        loadUsers();
        loadConversations();
    }

    public boolean createUser(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }

        username = username.trim();
        String pwd = password.trim();

        // 并发下更安全：putIfAbsent
        String prev = users.putIfAbsent(username, pwd);
        if (prev != null) return false;

        saveUsers();
        return true;
    }

    public boolean validateUser(String username, String password) {
        if (username == null || password == null) return false;
        String storedPassword = users.get(username.trim());
        return storedPassword != null && storedPassword.equals(password.trim());
    }

    public Set<String> getAllUsernames() {
        return users.keySet();
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) {
                users.putAll((Map<String, String>) obj);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载账号失败：" + e.getMessage());
            users.clear();
        }
    }

    private synchronized void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(new HashMap<>(users)); // 写入快照，避免并发变化
        } catch (IOException e) {
            System.err.println("保存账号失败：" + e.getMessage());
        }
    }

    public boolean deleteUser(String username) {
        if (username == null) return false;
        String key = username.trim();

        if (!users.containsKey(key)) return false;

        userConversations.remove(key);
        saveConversations();

        boolean removed = (users.remove(key) != null);
        if (removed) saveUsers();
        return removed;
    }

    public List<String> getConversation(String username) {
        if (username == null) return new ArrayList<>();
        return new ArrayList<>(userConversations.getOrDefault(username.trim(), new ArrayList<>()));
    }

    public void saveConversation(String username, List<String> conversation) {
        if (username == null) return;
        String key = username.trim();
        userConversations.put(key, new ArrayList<>(conversation));
        saveConversations();
    }

    @SuppressWarnings("unchecked")
    private void loadConversations() {
        File file = new File(CONVERSATION_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) {
                userConversations.putAll((Map<String, List<String>>) obj);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载对话历史失败：" + e.getMessage());
            userConversations.clear();
        }
    }

    private synchronized void saveConversations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONVERSATION_FILE))) {
            oos.writeObject(new HashMap<>(userConversations)); // 写入快照
        } catch (IOException e) {
            System.err.println("保存对话历史失败：" + e.getMessage());
        }
    }
}
