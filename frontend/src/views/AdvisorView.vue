<template>
  <div class="page">
    <h1>AI Investment Advisor</h1>
    <p class="sub">
      Ask questions about budgeting, saving, and basic asset allocation. The response includes structured suggestions and risk notes.
    </p>

    <section class="card">
      <div class="card-title">Chat</div>

      <div class="row">
        <label>
          Username
          <input v-model="username" readonly />
        </label>

        <button class="btn" @click="logout">Logout</button>
      </div>

      <div class="chatbox">
        <div v-if="messages.length === 0" class="empty">
          No messages yet. Ask a question to start.
        </div>

        <div v-for="(m, idx) in messages" :key="idx" class="msg" :class="m.role">
          <div class="meta">{{ m.role === "user" ? "You" : "AI" }}</div>
          <div class="text pre">{{ m.text }}</div>
        </div>
      </div>

      <!-- 上传区域 -->
      <div class="uploadBox">
        <input type="file" @change="onFileChange" />
        <button class="btn" @click="uploadFile" :disabled="!selectedFile">
          Upload File
        </button>
      </div>

      <div v-if="uploadMessage" class="ok">
        {{ uploadMessage }}
      </div>

      <div class="inputRow">
        <input
          v-model="input"
          @keyup.enter="send"
          placeholder="Type your question... (press Enter to send)"
        />
        <button class="btn" @click="send" :disabled="sending || !canSend">
          {{ sending ? "Sending..." : "Send" }}
        </button>
      </div>

      <div v-if="error" class="error">{{ error }}</div>
    </section>

    <section class="card" v-if="lastAi">
      <div class="card-title">Latest Advice</div>

      <div class="block">
        <div class="label">Reply</div>
        <div class="text pre">{{ lastAi.reply }}</div>
      </div>

      <div class="grid2">
        <div class="block">
          <div class="label">Action Items</div>
          <ul>
            <li v-for="(a, i) in lastAi.actionItems" :key="i">
              <strong>{{ a.title }}:</strong> {{ a.detail }}
            </li>
          </ul>
        </div>

        <div class="block">
          <div class="label">Risk Notes</div>
          <ul>
            <li v-for="(r, i) in lastAi.riskNotes" :key="i">{{ r }}</li>
          </ul>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { http } from "@/api/http";

const router = useRouter();

const username = ref(localStorage.getItem("username") || "");
const input = ref("");
const sending = ref(false);
const error = ref("");

const messages = ref([]); // {role:'user'|'ai', text:string}[]
const lastAi = ref(null); // last AI response object

const selectedFile = ref(null);
const uploadMessage = ref("");

const canSend = computed(() => username.value.trim() && input.value.trim());

function onFileChange(event) {
  const file = event.target.files?.[0] || null;
  selectedFile.value = file;
}

async function uploadFile() {
  if (!selectedFile.value || !username.value.trim()) return;

  uploadMessage.value = "";

  try {
    const formData = new FormData();
    formData.append("file", selectedFile.value);
    formData.append("username", username.value.trim());

    const { data } = await http.post("/files/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    if (data?.success) {
      uploadMessage.value = `Uploaded: ${data.originalFilename}`;

      // 可选：把上传记录显示到聊天框
      messages.value.push({
        role: "user",
        text: `[Uploaded file: ${data.originalFilename}]`,
      });

      selectedFile.value = null;
    } else {
      uploadMessage.value = data?.message || "Upload failed";
    }
  } catch (e) {
    console.error(e);
    uploadMessage.value = "Upload failed";
  }
}

function logout() {
  localStorage.removeItem("username");
  router.push("/login");
}

async function loadHistory() {
  if (!username.value.trim()) return;

  try {
    const { data } = await http.get(`/ai/history/${encodeURIComponent(username.value.trim())}`);

    const raw = Array.isArray(data?.messages) ? data.messages : [];

    const parsed = raw
      .map((line) => {
        if (typeof line !== "string") return null;
        if (line.startsWith("USER: ")) return { role: "user", text: line.slice("USER: ".length) };
        if (line.startsWith("AI: ")) return { role: "ai", text: line.slice("AI: ".length) };
        return null;
      })
      .filter(Boolean);

    messages.value = parsed;
  } catch (e) {
    console.warn("Failed to load history:", e);
  }
}

async function send() {
  if (!canSend.value || sending.value) return;

  const msg = input.value.trim();
  input.value = "";
  error.value = "";

  messages.value.push({ role: "user", text: msg });

  try {
    sending.value = true;

    const { data } = await http.post("/ai/chat", {
      username: username.value.trim(),
      message: msg,
    });

    lastAi.value = data;
    messages.value.push({ role: "ai", text: data.reply });
  } catch (e) {
    console.error(e);
    error.value =
      "Failed to contact AI endpoint. Make sure Spring Boot is running on http://localhost:8080 and CORS allows http://localhost:5173.";
  } finally {
    sending.value = false;
  }
}

onMounted(() => {
  if (!username.value.trim()) {
    router.push("/login");
    return;
  }
  loadHistory();
});
</script>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 20px;
  font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
}

.sub {
  color: #555;
  margin-top: 6px;
  margin-bottom: 18px;
}

.card {
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 14px;
  margin-top: 14px;
}

.card-title {
  font-weight: 700;
  margin-bottom: 10px;
}

.row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.row label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-width: 260px;
  flex: 1;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 10px;
}

.chatbox {
  margin-top: 12px;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 12px;
  min-height: 220px;
  background: #fafafa;
}

.empty {
  color: #666;
}

.msg {
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #eee;
  background: #fff;
}

.msg.user {
  border-left: 4px solid #333;
}

.msg.ai {
  border-left: 4px solid #666;
}

.meta {
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

.uploadBox {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  align-items: center;
}

.ok {
  margin-top: 8px;
  color: #0a7a0a;
}

.inputRow {
  display: grid;
  grid-template-columns: 1fr 140px;
  gap: 10px;
  margin-top: 12px;
}

.btn {
  padding: 10px 12px;
  border: 1px solid #333;
  border-radius: 10px;
  background: #fff;
  cursor: pointer;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error {
  margin-top: 12px;
  color: #b00020;
}

.block .label {
  font-weight: 700;
  margin-bottom: 6px;
}

.grid2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 12px;
}

.pre {
  white-space: pre-wrap;
}

ul {
  margin: 0;
  padding-left: 18px;
}
</style>