<template>
  <Bar :data="chartData" :options="chartOptions" />
</template>

<script setup>
import { Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
);

const props = defineProps({
  data: Object,
});

const chartData = {
  labels: ["탄수화물", "단백질", "지방", "당", "콜레스테롤"],
  datasets: [
    {
      label: "g (또는 mg)",
      data: [
        props.data.carbohydrate,
        props.data.protein,
        props.data.fat,
        props.data.sugar,
        props.data.cholesterol,
      ],
      backgroundColor: "#ffb6b6",
    },
  ],
};

const chartOptions = {
  responsive: true,
  scales: {
    y: {
      beginAtZero: true,
    },
  },
};
</script>
