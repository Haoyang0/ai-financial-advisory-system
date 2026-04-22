<template>
  <div class="page">
    <h1>Stocks Module</h1>
    <p class="sub">Select a ticker and view a price chart (mock data from backend).</p>

    <section class="card">
      <div class="row">
        <label>
          Ticker
          <select v-model="ticker" @change="loadSeries">
            <option v-for="t in tickers" :key="t" :value="t">{{ t }}</option>
          </select>
        </label>

        <label>
          Days
          <select v-model.number="days" @change="loadSeries">
            <option :value="30">30</option>
            <option :value="90">90</option>
            <option :value="180">180</option>
          </select>
        </label>

        <button class="btn" @click="loadSeries">Refresh</button>
      </div>

      <div v-if="error" class="error">{{ error }}</div>
      <div v-if="loading">Loading chart...</div>

      <div ref="chartEl" class="chart"></div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import * as echarts from "echarts";
import { http } from "@/api/http";

const tickers = ref([]);
const ticker = ref("AAPL");
const days = ref(30);

const loading = ref(false);
const error = ref("");

const chartEl = ref(null);
let chart = null;

async function loadTickers() {
  const { data } = await http.get("/stocks/tickers");
  tickers.value = data.tickers || [];
  if (tickers.value.length && !tickers.value.includes(ticker.value)) {
    ticker.value = tickers.value[0];
  }
}

function renderChart(dates, prices) {
  if (!chart) chart = echarts.init(chartEl.value);

  chart.setOption({
    title: { text: `${ticker.value} Price (Mock)` },
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: dates },
    yAxis: { type: "value" },
    series: [
      {
        type: "line",
        data: prices,
        smooth: true,
      },
    ],
  });
}

async function loadSeries() {
  try {
    error.value = "";
    loading.value = true;

    const { data } = await http.get("/stocks/series", {
      params: { ticker: ticker.value, days: days.value },
    });

    renderChart(data.dates, data.prices);
  } catch (e) {
    error.value =
      "Failed to load stock data. Make sure Spring Boot is running on http://localhost:8080 and CORS is enabled.";
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await loadTickers();
  await loadSeries();

  // 响应式 resize
  window.addEventListener("resize", () => chart && chart.resize());
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
}
.row {
  display: flex;
  gap: 12px;
  align-items: end;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 8px;
  min-width: 160px;
}
.btn {
  padding: 10px 14px;
  border: 1px solid #333;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
}
.chart {
  width: 100%;
  height: 420px;
  margin-top: 12px;
}
.error {
  color: #b00020;
  margin-bottom: 8px;
}
</style>
