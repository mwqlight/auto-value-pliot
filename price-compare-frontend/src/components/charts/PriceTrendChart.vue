<template>
  <div class="price-trend-chart">
    <v-chart :option="chartOption" :autoresize="true" class="chart" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  MarkLineComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册必要的组件
use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  MarkLineComponent
])

interface PriceData {
  date: string
  price: number
  platform: string
}

interface Props {
  data: PriceData[]
  title?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '价格趋势分析',
  height: '400px'
})

const chartOption = ref({})

// 按平台分组数据
const groupedData = computed(() => {
  const platforms = [...new Set(props.data.map(item => item.platform))]
  const result: { [key: string]: any[] } = {}
  
  platforms.forEach(platform => {
    result[platform] = props.data
      .filter(item => item.platform === platform)
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
  })
  
  return result
})

// 生成图表配置
const generateChartOption = () => {
  const platforms = Object.keys(groupedData.value)
  const series = platforms.map(platform => ({
    name: platform,
    type: 'line',
    data: groupedData.value[platform].map(item => [item.date, item.price]),
    smooth: true,
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: {
      width: 3
    },
    emphasis: {
      focus: 'series'
    }
  }))

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
      formatter: (params: any) => {
        let result = `<div style="font-weight: bold; margin-bottom: 5px;">${params[0].axisValue}</div>`
        params.forEach((param: any) => {
          result += `<div style="display: flex; align-items: center; margin: 5px 0;">
            <span style="display: inline-block; width: 10px; height: 10px; background: ${param.color}; border-radius: 50%; margin-right: 5px;"></span>
            <span style="flex: 1;">${param.seriesName}</span>
            <span style="font-weight: bold; color: #409EFF;">¥${param.value[1]}</span>
          </div>`
        })
        return result
      }
    },
    legend: {
      data: platforms,
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
      type: 'time',
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLabel: {
        color: '#666',
        formatter: (value: string) => {
          return new Date(value).toLocaleDateString()
        }
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
    series: series
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
.price-trend-chart {
  width: 100%;
  height: v-bind(height);
  
  .chart {
    width: 100%;
    height: 100%;
  }
}
</style>