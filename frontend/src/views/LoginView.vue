<template>
  <div class="page">
    <h1>Login</h1>
    <p class="sub">Sign in to access Bank, Stocks, and AI Advisor modules.</p>

    <section class="card">
      <div class="row">
        <label>
          Username
          <input v-model="username" placeholder="e.g. test" autocomplete="username" />
        </label>
      </div>

      <div class="row">
        <label>
          Password
          <input
            v-model="password"
            type="password"
            placeholder="Your password"
            autocomplete="current-password"
            @keyup.enter="login"
          />
        </label>
      </div>

      <div class="actions">
        <button class="btn primary" @click="login" :disabled="loading || !canSubmit">
          {{ loading ? "Signing in..." : "Sign in" }}
        </button>
        <button class="btn" @click="goRegister" :disabled="loading">
          Create account
        </button>
      </div>

      <div v-if="error" class="error">{{ error }}</div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { http } from "@/api/http";
import { onMounted } from "vue";

const router = useRouter();

const username = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

const canSubmit = computed(() => username.value.trim() && password.value.trim());

onMounted(() => {
  const u = localStorage.getItem("username");
  if (u) router.push("/");
});

async function login() {
  if (!canSubmit.value || loading.value) return;

  error.value = "";
  loading.value = true;

  try {
    // 后端：POST /api/v1/auth/login
    const { data } = await http.post("/auth/login", {
      username: username.value.trim(),
      password: password.value,
    });

    if (data?.success) {
      // 登录成功：保存用户名（你也可以保存 token，但目前轻量版不用）
      localStorage.setItem("username", username.value.trim());

      // 跳去主页/大面板（你如果还没做 dashboard，就先去 advisor）
      router.push("/");
    } else {
      error.value = data?.message || "Invalid username or password.";
    }
  } catch (e) {
    console.error(e);
    const status = e?.response?.status;
    const body = e?.response?.data;
    error.value = `Login failed. status=${status}, body=${JSON.stringify(body)}`;
  } finally {
    loading.value = false;
  }
}

function goRegister() {
  router.push("/register");
}
</script>

<style scoped>
.page {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px;
  font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
}

.sub {
  color: #555;
  margin-top: 6px;
  margin-bottom: 18px;
}

.card {
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 16px;
  margin-top: 14px;
}

.row {
  margin-top: 12px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

input {
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 10px;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.btn {
  padding: 10px 12px;
  border: 1px solid #333;
  border-radius: 10px;
  background: #fff;
  cursor: pointer;
}

.btn.primary {
  background: #111;
  color: #fff;
  border-color: #111;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error {
  margin-top: 12px;
  color: #b00020;
  word-break: break-word;
}
</style>