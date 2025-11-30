import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import PlatformComparisonChart from '../PlatformComparisonChart.vue'

// 模拟ECharts
vi.mock('vue-echarts', () => ({
  default: {
    name: 'VChart',
    template: '<div class="mock-chart">Mock Chart</div>'
  }
}))

describe('PlatformComparisonChart.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('渲染平台对比图表', () => {
    const wrapper = mount(PlatformComparisonChart, {
      props: {
        data: [
          { platform: 'taobao', count: 40, avgPrice: 5999 },
          { platform: 'jd', count: 35, avgPrice: 5899 },
          { platform: 'pdd', count: 25, avgPrice: 5799 }
        ]
      }
    })
    
    expect(wrapper.find('.mock-chart').exists()).toBe(true)
    expect(wrapper.text()).toContain('平台对比')
  })

  it('验证图表选项配置', () => {
    const mockData = [
      { platform: 'taobao', count: 40, avgPrice: 5999 },
      { platform: 'jd', count: 35, avgPrice: 5899 },
      { platform: 'pdd', count: 25, avgPrice: 5799 }
    ]
    
    const wrapper = mount(PlatformComparisonChart, {
      props: { data: mockData }
    })
    
    const chartOptions = wrapper.vm.chartOptions
    
    // 验证图表配置
    expect(chartOptions.title.text).toBe('平台对比')
    expect(chartOptions.tooltip.trigger).toBe('axis')
    expect(chartOptions.xAxis.type).toBe('category')
    expect(chartOptions.yAxis[0].type).toBe('value')
    
    // 验证系列数据
    expect(chartOptions.series).toHaveLength(2) // 商品数量和平均价格两个系列
    expect(chartOptions.series[0].name).toBe('商品数量')
    expect(chartOptions.series[1].name).toBe('平均价格')
  })

  it('处理空数据', () => {
    const wrapper = mount(PlatformComparisonChart, {
      props: { data: [] }
    })
    
    const chartOptions = wrapper.vm.chartOptions
    
    // 验证空数据处理
    expect(chartOptions.series[0].data).toEqual([])
    expect(chartOptions.series[1].data).toEqual([])
  })

  it('验证数据转换', () => {
    const mockData = [
      { platform: 'taobao', count: 40, avgPrice: 5999 },
      { platform: 'jd', count: 35, avgPrice: 5899 }
    ]
    
    const wrapper = mount(PlatformComparisonChart, {
      props: { data: mockData }
    })
    
    const chartOptions = wrapper.vm.chartOptions
    
    // 验证X轴数据
    expect(chartOptions.xAxis.data).toEqual(['淘宝', '京东'])
    
    // 验证系列数据
    expect(chartOptions.series[0].data).toEqual([40, 35]) // 商品数量
    expect(chartOptions.series[1].data).toEqual([5999, 5899]) // 平均价格
  })

  it('验证平台名称映射', () => {
    const mockData = [
      { platform: 'taobao', count: 40, avgPrice: 5999 },
      { platform: 'jd', count: 35, avgPrice: 5899 },
      { platform: 'pdd', count: 25, avgPrice: 5799 }
    ]
    
    const wrapper = mount(PlatformComparisonChart, {
      props: { data: mockData }
    })
    
    const platformNames = wrapper.vm.getPlatformName('taobao')
    
    expect(platformNames).toBe('淘宝')
    expect(wrapper.vm.getPlatformName('jd')).toBe('京东')
    expect(wrapper.vm.getPlatformName('pdd')).toBe('拼多多')
    expect(wrapper.vm.getPlatformName('unknown')).toBe('未知平台')
  })

  it('验证图表响应式', async () => {
    const initialData = [
      { platform: 'taobao', count: 40, avgPrice: 5999 }
    ]
    
    const wrapper = mount(PlatformComparisonChart, {
      props: { data: initialData }
    })
    
    const newData = [
      { platform: 'taobao', count: 45, avgPrice: 5900 },
      { platform: 'jd', count: 38, avgPrice: 5800 }
    ]
    
    // 更新props数据
    await wrapper.setProps({ data: newData })
    
    const chartOptions = wrapper.vm.chartOptions
    
    // 验证数据已更新
    expect(chartOptions.xAxis.data).toEqual(['淘宝', '京东'])
    expect(chartOptions.series[0].data).toEqual([45, 38])
    expect(chartOptions.series[1].data).toEqual([5900, 5800])
  })

  it('验证图表事件处理', async () => {
    const mockData = [
      { platform: 'taobao', count: 40, avgPrice: 5999 }
    ]
    
    const wrapper = mount(PlatformComparisonChart, {
      props: { data: mockData }
    })
    
    // 模拟图表点击事件
    const mockEvent = {
      componentType: 'series',
      componentSubType: 'bar',
      componentIndex: 0,
      seriesIndex: 0,
      seriesName: '商品数量',
      name: '淘宝',
      dataIndex: 0,
      data: 40,
      value: 40
    }
    
    // 触发图表事件
    wrapper.vm.onChartClick(mockEvent)
    
    // 验证事件被触发
    expect(wrapper.emitted('platform-click')).toBeTruthy()
    expect(wrapper.emitted('platform-click')[0]).toEqual(['taobao'])
  })

  it('验证图表尺寸自适应', () => {
    const wrapper = mount(PlatformComparisonChart, {
      props: {
        data: [{ platform: 'taobao', count: 40, avgPrice: 5999 }],
        width: '600px',
        height: '400px'
      }
    })
    
    // 验证图表尺寸设置
    const chartStyle = wrapper.vm.chartStyle
    expect(chartStyle.width).toBe('600px')
    expect(chartStyle.height).toBe('400px')
  })

  it('验证默认尺寸', () => {
    const wrapper = mount(PlatformComparisonChart, {
      props: {
        data: [{ platform: 'taobao', count: 40, avgPrice: 5999 }]
      }
    })
    
    // 验证默认尺寸
    const chartStyle = wrapper.vm.chartStyle
    expect(chartStyle.width).toBe('100%')
    expect(chartStyle.height).toBe('400px')
  })
})