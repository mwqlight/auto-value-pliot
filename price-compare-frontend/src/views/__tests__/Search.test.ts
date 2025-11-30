import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import Search from '../Search.vue'
import { ElInput, ElButton, ElMessage } from 'element-plus'

// 模拟Element Plus组件
vi.mock('element-plus', () => ({
  ElInput: { name: 'ElInput', template: '<div><input /></div>' },
  ElButton: { name: 'ElButton', template: '<button></button>' },
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn()
  }
}))

// 模拟API调用
const mockSearchApi = vi.fn()
vi.mock('@/api', () => ({
  default: {
    get: mockSearchApi
  }
}))

describe('Search.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockSearchApi.mockClear()
  })

  it('渲染搜索组件', () => {
    const wrapper = mount(Search)
    
    expect(wrapper.findComponent(ElInput).exists()).toBe(true)
    expect(wrapper.findComponent(ElButton).exists()).toBe(true)
    expect(wrapper.text()).toContain('全网比价')
  })

  it('处理搜索输入', async () => {
    const wrapper = mount(Search)
    const input = wrapper.find('input')
    
    await input.setValue('iPhone 15')
    
    expect(wrapper.vm.keyword).toBe('iPhone 15')
  })

  it('验证搜索关键词不能为空', async () => {
    const wrapper = mount(Search)
    const searchButton = wrapper.findComponent(ElButton)
    
    await searchButton.trigger('click')
    
    // 验证显示错误消息
    expect(ElMessage.error).toHaveBeenCalledWith('请输入搜索关键词')
  })

  it('成功搜索商品', async () => {
    const mockResponse = {
      data: {
        code: 200,
        message: '成功',
        data: {
          products: [
            {
              id: 1,
              title: 'iPhone 15',
              price: 5999,
              platformCode: 'taobao',
              platformProductId: 'taobao_001'
            }
          ],
          summary: {
            totalCount: 1,
            minPrice: 5999,
            maxPrice: 5999,
            avgPrice: 5999
          }
        }
      }
    }
    
    mockSearchApi.mockResolvedValue(mockResponse)
    
    const wrapper = mount(Search)
    const input = wrapper.find('input')
    const searchButton = wrapper.findComponent(ElButton)
    
    await input.setValue('iPhone 15')
    await searchButton.trigger('click')
    
    // 验证API调用
    expect(mockSearchApi).toHaveBeenCalledWith('/api/compare/search', {
      params: { keyword: 'iPhone 15' }
    })
    
    // 验证路由跳转
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.$router.push).toHaveBeenCalledWith({
      name: 'CompareResult',
      query: { keyword: 'iPhone 15' }
    })
  })

  it('处理搜索失败', async () => {
    mockSearchApi.mockRejectedValue(new Error('网络错误'))
    
    const wrapper = mount(Search)
    const input = wrapper.find('input')
    const searchButton = wrapper.findComponent(ElButton)
    
    await input.setValue('iPhone 15')
    await searchButton.trigger('click')
    
    // 验证错误处理
    expect(ElMessage.error).toHaveBeenCalledWith('搜索失败，请稍后重试')
  })

  it('验证平台选择功能', async () => {
    const wrapper = mount(Search)
    
    // 测试平台选择
    await wrapper.setData({
      selectedPlatforms: ['taobao', 'jd']
    })
    
    expect(wrapper.vm.selectedPlatforms).toEqual(['taobao', 'jd'])
  })

  it('验证搜索历史功能', async () => {
    const wrapper = mount(Search)
    
    // 模拟搜索历史
    const searchHistory = ['iPhone 15', 'MacBook Pro', 'AirPods']
    await wrapper.setData({ searchHistory })
    
    expect(wrapper.vm.searchHistory).toHaveLength(3)
    expect(wrapper.vm.searchHistory).toContain('iPhone 15')
  })

  it('清除搜索历史', async () => {
    const wrapper = mount(Search)
    
    await wrapper.setData({
      searchHistory: ['iPhone 15', 'MacBook Pro']
    })
    
    // 清除历史
    await wrapper.vm.clearSearchHistory()
    
    expect(wrapper.vm.searchHistory).toHaveLength(0)
  })

  it('从历史记录搜索', async () => {
    const wrapper = mount(Search)
    
    await wrapper.setData({
      searchHistory: ['iPhone 15']
    })
    
    // 从历史记录搜索
    await wrapper.vm.searchFromHistory('iPhone 15')
    
    expect(wrapper.vm.keyword).toBe('iPhone 15')
    expect(mockSearchApi).toHaveBeenCalled()
  })
})