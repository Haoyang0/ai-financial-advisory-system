<template>
  <div class="page">
    <h1>AI Asset Management Dashboard</h1>
    <p class="sub">Select a module to continue.</p>

    <div class="grid">
      <div class="card" @click="go('/bank')">
        <div class="title">Bank</div>
        <div class="desc">
          View deposit rates from major banks and calculate interest for fixed-term deposits.
        </div>
        <div class="hint">Go to Bank →</div>
      </div>

      <div class="card" @click="go('/stocks')">
        <div class="title">Stocks</div>
        <div class="desc">
          Search listed companies and view price/return charts (to be implemented).
        </div>
        <div class="hint">Go to Stocks →</div>
      </div>

      <div class="card" @click="go('/advisor')">
        <div class="title">AI Advisor</div>
        <div class="desc">
          Chat with the AI assistant for budgeting, saving, and basic asset allocation guidance.
        </div>
        <div class="hint">Go to AI Advisor →</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

function go(path) {
  router.push(path);
}

onMounted(() => {
  const u = localStorage.getItem("username");
  if (!u) {
    router.push("/login");
  }
});
</script>

<style scoped>
.page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 28px 20px;
  font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
}

.sub {
  color: #555;
  margin-top: 8px;
  margin-bottom: 20px;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 14px;
}

.card {
  border: 1px solid #ddd;
  border-radius: 14px;
  padding: 16px;
  cursor: pointer;
  background: #fff;
  transition: transform 0.06s ease, box-shadow 0.06s ease;
  min-height: 160px;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
}

.title {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 8px;
}

.desc {
  color: #444;
  line-height: 1.4;
}

.hint {
  margin-top: 14px;
  font-weight: 700;
}

@media (max-width: 900px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
