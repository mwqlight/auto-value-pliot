<template>
  <div class="price-distribution-chart">
    <v-chart :option="chartOption" :autoresize="true" class="chart" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册必要的组件
use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

interface PriceData {
  platform: string
  price: number
  count: number
}

interface Props {
  data: PriceData[]
  title?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '价格分布分析',
  height: '400px'
})

const chartOption = ref({})

// 生成图表数据
const chartData = computed(() => {
  return props.data.map(item => ({
    name: item.platform,
    value: item.count,
    price: item.price
  }))
})

// 生成图表配置
const generateChartOption = () => {
  return {
    title: {
      text: props.title,
      left: 'center',
      textStyle: {
        color: '#333',
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        return `<div style="font-weight: bold;">${params.name}</div>
                <div>商品数量: ${params.value}</div>
                <div>平均价格: ¥${params.data.price}</div>
                <div>占比: ${params.percent}%</div>`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: '15%',
      textStyle: {
        color: '#666'
      }
    },
    series: [
      {
        name: '价格分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData.value
      }
    ]
  }
}

// 监听数据变化
watch(() => props.data, () => {
  chartOption.value = generateChartOption()
}, { deep: true })

onMounted(() => {
  chartOption.value = generateChartOption()
})
</script>

<style scoped lang="scss">
.price-distribution-chart {
  width: 100%;
  height: v-bind(height);
  
  .chart {
    width: 100%;
    height: 100%;
  }
}
</style>