<template>
  <div class="platform-comparison-chart">
    <v-chart :option="chartOption" :autoresize="true" class="chart" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册必要的组件
use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent
])

interface PlatformData {
  platform: string
  avgPrice: number
  minPrice: number
  maxPrice: number
  productCount: number
}

interface Props {
  data: PlatformData[]
  title?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '平台价格对比',
  height: '400px'
})

const chartOption = ref({})

// 生成图表配置
const generateChartOption = () => {
  const platforms = props.data.map(item => item.platform)
  const avgPrices = props.data.map(item => item.avgPrice)
  const minPrices = props.data.map(item => item.minPrice)
  const maxPrices = props.data.map(item => item.maxPrice)

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
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const data = params[0].data
        return `<div style="font-weight: bold; margin-bottom: 5px;">${params[0].name}</div>
                <div>平均价格: <span style="color: #409EFF; font-weight: bold;">¥${data.avgPrice}</span></div>
                <div>最低价格: <span style="color: #67C23A; font-weight: bold;">¥${data.minPrice}</span></div>
                <div>最高价格: <span style="color: #F56C6C; font-weight: bold;">¥${data.maxPrice}</span></div>
                <div>商品数量: <span style="color: #909399; font-weight: bold;">${data.productCount}</span></div>`
      }
    },
    legend: {
      data: ['平均价格', '最低价格', '最高价格'],
      top: '10%',
      textStyle: {
        color: '#666'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: platforms,
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLabel: {
        color: '#666',
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '价格(元)',
      nameTextStyle: {
        color: '#666'
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLabel: {
        color: '#666',
        formatter: (value: number) => `¥${value}`
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#eee'
        }
      }
    },
    series: [
      {
        name: '平均价格',
        type: 'bar',
        data: props.data.map(item => ({
          value: item.avgPrice,
          itemStyle: {
            color: '#409EFF'
          }
        })),
        barWidth: '30%',
        emphasis: {
          focus: 'series'
        }
      },
      {
        name: '最低价格',
        type: 'bar',
        data: props.data.map(item => ({
          value: item.minPrice,
          itemStyle: {
            color: '#67C23A'
          }
        })),
        barWidth: '30%',
        emphasis: {
          focus: 'series'
        }
      },
      {
        name: '最高价格',
        type: 'bar',
        data: props.data.map(item => ({
          value: item.maxPrice,
          itemStyle: {
            color: '#F56C6C'
          }
        })),
        barWidth: '30%',
        emphasis: {
          focus: 'series'
        }
      }
    ],
    dataZoom: [
      {
        type: 'inside',
        start: 0,
        end: 100
      },
      {
        start: 0,
        end: 100
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
.platform-comparison-chart {
  width: 100%;
  height: v-bind(height);
  
  .chart {
    width: 100%;
    height: 100%;
  }
}
</style>