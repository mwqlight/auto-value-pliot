import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import Dashboard from '../Dashboard.vue'
import { ElCard, ElStatistic, ElRow, ElCol } from 'element-plus'

// 模拟Element Plus组件
vi.mock('element-plus', () => ({
  ElCard: { name: 'ElCard', template: '<div><slot /></div>' },
  ElStatistic: { name: 'ElStatistic', template: '<div><slot /></div>' },
  ElRow: { name: 'ElRow', template: '<div><slot /></div>' },
  ElCol: { name: 'ElCol', template: '<div><slot /></div>' }
}))

// 模拟API调用
const mockDashboardApi = vi.fn()
vi.mock('@/api', () => ({
  default: {
    get: mockDashboardApi
  }
}))

describe('Dashboard.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockDashboardApi.mockClear()
  })

  it('渲染仪表板组件', () => {
    const wrapper = mount(Dashboard)
    
    expect(wrapper.findComponent(ElCard).exists()).toBe(true)
    expect(wrapper.findComponent(ElStatistic).exists()).toBe(true)
    expect(wrapper.text()).toContain('全网比价驾驶舱')
  })

  it('加载仪表板数据', async () => {
    const mockData = {
      data: {
        code: 200,
        message: '成功',
        data: {
          totalSearches: 1500,
          totalProducts: 50000,
          avgPriceDifference: 15.5,
          platformDistribution: {
            taobao: 40,
            jd: 35,
            pdd: 25
          },
          popularKeywords: ['iPhone 15', 'MacBook Pro', 'AirPods', 'iPad', 'Apple Watch']
        }
      }
    }
    
    mockDashboardApi.mockResolvedValue(mockData)
    
    const wrapper = mount(Dashboard)
    
    // 等待数据加载
    await wrapper.vm.$nextTick()
    
    expect(mockDashboardApi).toHaveBeenCalledWith('/api/dashboard/stats')
    expect(wrapper.vm.dashboardData.totalSearches).toBe(1500)
    expect(wrapper.vm.dashboardData.totalProducts).toBe(50000)
  })

  it('处理数据加载失败', async () => {
    mockDashboardApi.mockRejectedValue(new Error('网络错误'))
    
    const wrapper = mount(Dashboard)
    
    // 等待数据加载
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.loading).toBe(false)
    expect(wrapper.vm.dashboardData.totalSearches).toBe(0)
  })

  it('验证统计卡片显示', async () => {
    const wrapper = mount(Dashboard)
    
    await wrapper.setData({
      dashboardData: {
        totalSearches: 1500,
        totalProducts: 50000,
        avgPriceDifference: 15.5
      }
    })
    
    const statistics = wrapper.findAllComponents(ElStatistic)
    expect(statistics).toHaveLength(3)
    
    // 验证统计值显示
    expect(wrapper.text()).toContain('1500')
    expect(wrapper.text()).toContain('50000')
    expect(wrapper.text()).toContain('15.5%')
  })

  it('验证平台分布图表', async () => {
    const wrapper = mount(Dashboard)
    
    await wrapper.setData({
      dashboardData: {
        platformDistribution: {
          taobao: 40,
          jd: 35,
          pdd: 25
        }
      }
    })
    
    // 验证平台分布数据
    expect(wrapper.vm.platformDistributionData).toEqual([
      { value: 40, name: '淘宝' },
      { value: 35, name: '京东' },
      { value: 25, name: '拼多多' }
    ])
  })

  it('验证热门关键词显示', async () => {
    const wrapper = mount(Dashboard)
    
    const popularKeywords = ['iPhone 15', 'MacBook Pro', 'AirPods', 'iPad', 'Apple Watch']
    
    await wrapper.setData({
      dashboardData: {
        popularKeywords
      }
    })
    
    // 验证热门关键词显示
    expect(wrapper.vm.dashboardData.popularKeywords).toEqual(popularKeywords)
    
    // 验证每个关键词都显示
    popularKeywords.forEach(keyword => {
      expect(wrapper.text()).toContain(keyword)
    })
  })

  it('验证数据刷新功能', async () => {
    const wrapper = mount(Dashboard)
    
    // 模拟初始数据
    await wrapper.setData({
      dashboardData: {
        totalSearches: 1000,
        totalProducts: 30000
      }
    })
    
    // 刷新数据
    await wrapper.vm.refreshData()
    
    expect(mockDashboardApi).toHaveBeenCalledTimes(1)
  })

  it('验证加载状态', async () => {
    const wrapper = mount(Dashboard)
    
    // 初始加载状态
    expect(wrapper.vm.loading).toBe(true)
    
    // 模拟数据加载完成
    await wrapper.setData({
      loading: false
    })
    
    expect(wrapper.vm.loading).toBe(false)
  })

  it('验证错误处理', async () => {
    mockDashboardApi.mockRejectedValue(new Error('服务不可用'))
    
    const wrapper = mount(Dashboard)
    
    // 等待错误处理
    await wrapper.vm.$nextTick()
    
    expect(wrapper.vm.loading).toBe(false)
    expect(wrapper.vm.dashboardData.totalSearches).toBe(0)
  })

  it('验证组件生命周期', async () => {
    const wrapper = mount(Dashboard)
    
    // 验证组件挂载时调用数据加载
    expect(mockDashboardApi).toHaveBeenCalledTimes(1)
    
    // 模拟组件卸载
    wrapper.unmount()
    
    // 验证清理操作
    expect(wrapper.vm.loading).toBe(false)
  })
})