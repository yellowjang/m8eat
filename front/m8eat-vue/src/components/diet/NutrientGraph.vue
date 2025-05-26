<template>
  <div class="chart-wrapper">
    <canvas ref="chartRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip } from "chart.js";
import ChartDataLabelsPlugin from "chartjs-plugin-datalabels";

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip, ChartDataLabelsPlugin);

const props = defineProps({
  data: Object, // { carbohydrate, protein, fat, sugar }
  max: Object, // { carbohydrate, protein, fat, sugar }
});

const chartRef = ref(null);
let chartInstance;

const labels = ["탄수화물", "단백질", "지방", "당류"];
const nutrientKeys = ["carbohydrate", "protein", "fat", "sugar"];

const buildChart = () => {
  if (chartInstance) chartInstance.destroy();

  const values = nutrientKeys.map((k) => props.data[k] / props.max[k]);
  const bgColors = nutrientKeys.map((k, idx) => (props.data[k] > props.max[k] ? "#f87171" : "#fca5a5"));

  chartInstance = new Chart(chartRef.value, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          data: values,
          backgroundColor: bgColors,
          borderRadius: 6,
        },
      ],
    },
    options: {
      layout: {
        padding: {
          top: 30,
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          max: 1.1,
          ticks: {
            callback: (value) => `${Math.round(value * 100)}%`,
          },
        },
      },
      plugins: {
        datalabels: {
          display: false,
        },
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => {
              const key = nutrientKeys[ctx.dataIndex];
              const actual = props.data[key];
              const maxVal = props.max[key];
              return `${actual.toFixed(1)}g / ${maxVal}g`;
            },
          },
        },
      },
    },
    plugins: [ChartDataLabelsPlugin],
  });
};

onMounted(buildChart);
watch(() => props.data, buildChart, { deep: true });
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  z-index: 0;
}
canvas {
  max-width: 100%;
  z-index: 1;
  position: relative;
}
</style>
