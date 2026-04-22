<template>
  <div class="page">
    <h1>Create account</h1>
    <p class="sub">Register a new user account.</p>

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
            placeholder="Choose a password"
            autocomplete="new-password"
            @keyup.enter="register"
          />
        </label>
      </div>

      <div class="actions">
        <button class="btn primary" @click="register" :disabled="loading || !canSubmit">
          {{ loading ? "Creating..." : "Create account" }}
        </button>
        <button class="btn" @click="goLogin" :disabled="loading">
          Back to login
        </button>
      </div>

      <div v-if="ok" class="ok">{{ ok }}</div>
      <div v-if="error" class="error">{{ error }}</div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { http } from "@/api/http";

const router = useRouter();

const username = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");
const ok = ref("");

const canSubmit = computed(() => username.value.trim() && password.value.trim());

async function register() {
  if (!canSubmit.value || loading.value) return;

  error.value = "";
  ok.value = "";
  loading.value = true;

  try {
    // 后端：POST /api/v1/auth/register
    const { data } = await http.post("/auth/register", {
      username: username.value.trim(),
      password: password.value,
    });

    if (data?.success) {
      ok.value = "Account created. Redirecting to login...";
      setTimeout(() => router.push("/login"), 600);
    } else {
      error.value = data?.message || "Registration failed.";
    }
  } catch (e) {
    console.error(e);
    const status = e?.response?.status;
    const body = e?.response?.data;
    error.value = `Registration failed. status=${status}, body=${JSON.stringify(body)}`;
  } finally {
    loading.value = false;
  }
}

function goLogin() {
  router.push("/login");
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

.ok {
  margin-top: 12px;
  color: #0a7a0a;
}

.error {
  margin-top: 12px;
  color: #b00020;
  word-break: break-word;
}
</style>
