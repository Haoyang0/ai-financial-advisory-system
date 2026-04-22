<template>
  <div class="page">
    <h1>Bank Module</h1>
    <p class="sub">
      Shows deposit interest rates (mock data) and provides a simple deposit interest calculator.
    </p>

    <section class="card">
      <div class="card-title">Deposit Rates</div>

      <div v-if="loading">Loading rates...</div>

      <div v-else>
        <div class="meta">
          Last updated: <strong>{{ new Date(updatedAt * 1000).toLocaleString() }}</strong>
        </div>

        <table class="table">
          <thead>
            <tr>
              <th>Bank</th>
              <th v-for="t in termsMonths" :key="t">{{ t }} months (APR)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="b in banks" :key="b.name">
              <td>{{ b.name }}</td>
              <td v-for="t in termsMonths" :key="t">
                {{ formatApr(b.aprByTerm[String(t)]) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="card">
      <div class="card-title">Deposit Calculator</div>

      <div class="grid">
        <label>
          Bank
          <select v-model="calc.bank">
            <option v-for="b in banks" :key="b.name" :value="b.name">{{ b.name }}</option>
          </select>
        </label>

        <label>
          Term (months)
          <select v-model.number="calc.termMonths">
            <option v-for="t in termsMonths" :key="t" :value="t">{{ t }}</option>
          </select>
        </label>

        <label>
          Principal
          <input v-model.number="calc.principal" type="number" min="1" />
        </label>

        <button class="btn" @click="calculate" :disabled="!banks.length || !termsMonths.length">
          Calculate
        </button>
      </div>

      <div v-if="calcResult" class="result">
        <div><strong>APR used:</strong> {{ formatApr(calcResult.aprUsed) }}</div>
        <div><strong>Interest:</strong> {{ calcResult.interest }}</div>
        <div><strong>Total:</strong> {{ calcResult.total }}</div>
      </div>

      <div v-if="error" class="error">
        {{ error }}
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { http } from "@/api/http";

const loading = ref(true);
const error = ref("");

const updatedAt = ref(0);
const termsMonths = ref([]);
const banks = ref([]);

const calc = ref({ bank: "HSBC", termMonths: 12, principal: 10000 });
const calcResult = ref(null);

function formatApr(x) {
  if (x === undefined || x === null) return "-";
  return (x * 100).toFixed(2) + "%";
}

async function loadRates() {
  try {
    error.value = "";
    loading.value = true;

    const { data } = await http.get("/banks/rates");
    updatedAt.value = data.updatedAt;
    termsMonths.value = data.termsMonths;
    banks.value = data.banks;

    // set defaults after loading
    if (banks.value.length > 0) calc.value.bank = banks.value[0].name;
    if (termsMonths.value.length > 0) calc.value.termMonths = termsMonths.value[termsMonths.value.length - 1];
  } catch (e) {
    error.value =
      "Failed to load bank rates. Make sure Spring Boot is running on http://localhost:8080 and CORS is enabled.";
  } finally {
    loading.value = false;
  }
}

async function calculate() {
  try {
    error.value = "";
    const { data } = await http.post("/banks/calc", calc.value);
    calcResult.value = data;
  } catch (e) {
    error.value = "Calculation failed. Please check your input and backend status.";
  }
}

onMounted(loadRates);
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

.meta {
  margin-bottom: 10px;
  color: #444;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  border: 1px solid #eee;
  padding: 10px;
  text-align: left;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  align-items: end;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

select,
input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.btn {
  padding: 10px 14px;
  border: 1px solid #333;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.result {
  margin-top: 12px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fafafa;
}

.error {
  margin-top: 12px;
  color: #b00020;
}
</style>
