<template>
  <div class="compare-container">
    <!-- 页面头部 -->
    <header class="compare-header">
      <div class="header-content">
        <div class="back-button">
          <el-button type="primary" link @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
        <h1 class="title">比价结果</h1>
        <div class="product-info">
          <h2 class="product-name">{{ productName }}</h2>
          <p class="compare-time">比价时间：{{ compareTime }}</p>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="compare-main">
      <!-- 价格对比图表 -->
      <section class="chart-section">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>价格对比分析</span>
              <div class="chart-actions">
                <el-button size="small" @click="exportChart">
                  <el-icon><Download /></el-icon>
                  导出图表
                </el-button>
              </div>
            </div>
          </template>
          
          <div class="chart-container">
            <!-- 这里应该集成ECharts图表 -->
            <div class="chart-placeholder">
              <el-icon><TrendCharts /></el-icon>
              <p>价格对比图表</p>
              <p class="placeholder-desc">（实际项目中集成ECharts实现可视化）</p>
            </div>
          </div>
        </el-card>
      </section>
      
      <!-- 详细价格对比 -->
      <section class="detail-section">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>详细价格对比</span>
              <div class="filter-actions">
                <el-select v-model="sortBy" placeholder="排序方式" size="small">
                  <el-option label="价格从低到高" value="price_asc" />
                  <el-option label="价格从高到低" value="price_desc" />
                  <el-option label="平台排序" value="platform" />
                </el-select>
                <el-button size="small" @click="toggleViewMode">
                  <el-icon>{{ viewMode === 'list' ? 'Grid' : 'List' }}</el-icon>
                  {{ viewMode === 'list' ? '网格视图' : '列表视图' }}
                </el-button>
              </div>
            </div>
          </template>
          
          <!-- 列表视图 -->
          <div v-if="viewMode === 'list'" class="list-view">
            <el-table :data="sortedResults" style="width: 100%">
              <el-table-column prop="platform" label="平台" width="120">
                <template #default="{ row }">
                  <div class="platform-info">
                    <el-avatar :size="32" :src="row.platformLogo" />
                    <span class="platform-name">{{ row.platform }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="shopName" label="店铺" min-width="150" />
              <el-table-column prop="price" label="价格" width="120">
                <template #default="{ row }">
                  <span class="price-value" :class="{ 'lowest-price': row.isLowest }">
                    {{ row.price }}
                  </span>
                  <el-tag v-if="row.isLowest" type="success" size="small">最低</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="originalPrice" label="原价" width="120">
                <template #default="{ row }">
                  <span 
                    v-if="row.originalPrice" 
                    class="original-price"
                  >
                    {{ row.originalPrice }}
                  </span>
                  <span v-else class="no-data">-</span>
                </template>
              </el-table-column>
              <el-table-column prop="discount" label="优惠" width="100">
                <template #default="{ row }">
                  <el-tag 
                    v-if="row.discount" 
                    type="warning" 
                    size="small"
                  >
                    {{ row.discount }}
                  </el-tag>
                  <span v-else class="no-data">-</span>
                </template>
              </el-table-column>
              <el-table-column prop="sales" label="销量" width="100" />
              <el-table-column prop="rating" label="评分" width="100">
                <template #default="{ row }">
                  <div class="rating-info">
                    <el-rate
                      v-model="row.rating"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}"
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="delivery" label="配送" width="100" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="viewDetail(row)">
                    查看详情
                  </el-button>
                  <el-button type="success" size="small" @click="addToCart(row)">
                    加入购物车
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 网格视图 -->
          <div v-else class="grid-view">
            <el-row :gutter="20">
              <el-col 
                v-for="item in sortedResults" 
                :key="item.id" 
                :xs="24" 
                :sm="12" 
                :md="8"
              >
                <el-card class="product-card" shadow="hover">
                  <div class="product-header">
                    <div class="platform-info">
                      <el-avatar :size="32" :src="item.platformLogo" />
                      <div class="platform-details">
                        <div class="platform-name">{{ item.platform }}</div>
                        <div class="shop-name">{{ item.shopName }}</div>
                      </div>
                    </div>
                    <el-tag v-if="item.isLowest" type="success" size="small">最低价</el-tag>
                  </div>
                  
                  <div class="price-section">
                    <div class="current-price" :class="{ 'lowest-price': item.isLowest }">
                      {{ item.price }}
                    </div>
                    <div class="price-details">
                      <div 
                        v-if="item.originalPrice" 
                        class="original-price"
                      >
                        {{ item.originalPrice }}
                      </div>
                      <el-tag 
                        v-if="item.discount" 
                        type="warning" 
                        size="small"
                      >
                        {{ item.discount }}
                      </el-tag>
                    </div>
                  </div>
                  
                  <div class="product-meta">
                    <div class="meta-item">
                      <span class="label">销量：</span>
                      <span class="value">{{ item.sales }}</span>
                    </div>
                    <div class="meta-item">
                      <span class="label">评分：</span>
                      <el-rate
                        v-model="item.rating"
                        disabled
                        show-score
                        text-color="#ff9900"
                        score-template="{value}"
                        size="small"
                      />
                    </div>
                    <div class="meta-item">
                      <span class="label">配送：</span>
                      <span class="value">{{ item.delivery }}</span>
                    </div>
                  </div>
                  
                  <div class="product-actions">
                    <el-button type="primary" size="small" @click="viewDetail(item)">
                      查看详情
                    </el-button>
                    <el-button type="success" size="small" @click="addToCart(item)">
                      加入购物车
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </section>
      
      <!-- 价格趋势分析 -->
      <section class="trend-section">
        <el-card class="trend-card">
          <template #header>
            <div class="card-header">
              <span>价格趋势分析</span>
              <div class="trend-actions">
                <el-select v-model="trendPeriod" placeholder="时间范围" size="small">
                  <el-option label="最近7天" value="7d" />
                  <el-option label="最近30天" value="30d" />
                  <el-option label="最近90天" value="90d" />
                </el-select>
              </div>
            </div>
          </template>
          
          <div class="trend-container">
            <div class="trend-placeholder">
              <el-icon><DataAnalysis /></el-icon>
              <p>价格趋势图表</p>
              <p class="placeholder-desc">（实际项目中集成ECharts实现价格趋势分析）</p>
            </div>
          </div>
        </el-card>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()

// 状态管理
const productName = ref('Apple iPhone 15 Pro Max 256GB')
const compareTime = ref('2024-01-15 14:30:22')
const viewMode = ref<'list' | 'grid'>('list')
const sortBy = ref('price_asc')
const trendPeriod = ref('7d')

// 模拟比价结果数据
const compareResults = ref([
  {
    id: '1',
    platform: '淘宝',
    platformLogo: 'https://via.placeholder.com/32x32?text=TB',
    shopName: '苹果官方旗舰店',
    price: '¥8,999',
    originalPrice: '¥9,999',
    discount: '9折',
    sales: '2.5万+',
    rating: 4.9,
    delivery: '顺丰包邮',
    isLowest: true
  },
  {
    id: '2',
    platform: '京东',
    platformLogo: 'https://via.placeholder.com/32x32?text=JD',
    shopName: '京东自营',
    price: '¥9,199',
    originalPrice: '¥9,999',
    discount: '8.8折',
    sales: '3.2万+',
    rating: 4.8,
    delivery: '京东物流',
    isLowest: false
  },
  {
    id: '3',
    platform: '拼多多',
    platformLogo: 'https://via.placeholder.com/32x32?text=PDD',
    shopName: '百亿补贴官方',
    price: '¥8,799',
    originalPrice: '¥9,999',
    discount: '7.9折',
    sales: '1.8万+',
    rating: 4.7,
    delivery: '顺丰包邮',
    isLowest: false
  },
  {
    id: '4',
    platform: '苏宁',
    platformLogo: 'https://via.placeholder.com/32x32?text=SN',
    shopName: '苏宁自营',
    price: '¥9,299',
    originalPrice: '¥9,999',
    discount: '8.5折',
    sales: '8千+',
    rating: 4.6,
    delivery: '苏宁物流',
    isLowest: false
  }
])

// 计算属性
const sortedResults = computed(() => {
  const results = [...compareResults.value]
  
  switch (sortBy.value) {
    case 'price_asc':
      return results.sort((a, b) => {
        const priceA = parseFloat(a.price.replace(/[^\d.]/g, ''))
        const priceB = parseFloat(b.price.replace(/[^\d.]/g, ''))
        return priceA - priceB
      })
    case 'price_desc':
      return results.sort((a, b) => {
        const priceA = parseFloat(a.price.replace(/[^\d.]/g, ''))
        const priceB = parseFloat(b.price.replace(/[^\d.]/g, ''))
        return priceB - priceA
      })
    case 'platform':
      return results.sort((a, b) => a.platform.localeCompare(b.platform))
    default:
      return results
  }
})

// 方法
const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'list' ? 'grid' : 'list'
}

const exportChart = () => {
  ElMessage.success('图表导出功能开发中...')
}

const viewDetail = (item: any) => {
  ElMessage.info(`查看商品详情：${item.platform} - ${item.shopName}`)
  // 这里应该跳转到商品详情页面
}

const addToCart = (item: any) => {
  ElMessage.success(`已添加到购物车：${item.platform} - ${item.price}`)
  // 这里应该调用购物车API
}

onMounted(() => {
  // 从路由参数获取比价任务ID
  const taskId = route.params.id
  if (taskId) {
    // 根据任务ID加载比价结果
    loadCompareResult(taskId as string)
  }
})

const loadCompareResult = async (taskId: string) => {
  try {
    // 这里应该调用API加载比价结果
    // const result = await compareApi.getCompareResult(taskId)
    // productName.value = result.productName
    // compareResults.value = result.compareResults
  } catch (error) {
    ElMessage.error('加载比价结果失败')
  }
}
</script>

<style lang="scss" scoped>
.compare-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.compare-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  
  .header-content {
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .back-button {
    margin-bottom: 20px;
    
    .el-button {
      color: rgba(255, 255, 255, 0.8) !important;
      
      &:hover {
        color: white !important;
      }
    }
  }
  
  .title {
    font-size: 32px;
    font-weight: 300;
    margin-bottom: 10px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  }
  
  .product-info {
    .product-name {
      font-size: 20px;
      font-weight: 500;
      margin-bottom: 5px;
    }
    
    .compare-time {
      color: rgba(255, 255, 255, 0.8);
      font-size: 14px;
    }
  }
}

.compare-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
}

.chart-section,
.detail-section,
.trend-section {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container,
.trend-container {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .chart-placeholder,
  .trend-placeholder {
    text-align: center;
    color: #999;
    
    .el-icon {
      font-size: 80px;
      margin-bottom: 20px;
      color: #dcdfe6;
    }
    
    p {
      margin: 0;
      font-size: 16px;
    }
    
    .placeholder-desc {
      font-size: 12px;
      margin-top: 5px;
    }
  }
}

.list-view {
  :deep(.el-table) {
    th {
      background-color: #f8f9fa;
      font-weight: 600;
    }
    
    .platform-info {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .platform-name {
        font-weight: 500;
      }
    }
    
    .price-value {
      font-weight: bold;
      font-size: 16px;
      
      &.lowest-price {
        color: #67c23a;
      }
    }
    
    .original-price {
      color: #999;
      text-decoration: line-through;
      font-size: 12px;
    }
    
    .no-data {
      color: #999;
      font-style: italic;
    }
    
    .rating-info {
      :deep(.el-rate) {
        display: flex;
        align-items: center;
      }
    }
  }
}

.grid-view {
  .product-card {
    margin-bottom: 20px;
    
    .product-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 15px;
      
      .platform-info {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .platform-details {
          .platform-name {
            font-weight: 500;
            font-size: 14px;
          }
          
          .shop-name {
            font-size: 12px;
            color: #666;
          }
        }
      }
    }
    
    .price-section {
      margin-bottom: 15px;
      
      .current-price {
        font-size: 20px;
        font-weight: bold;
        color: #f56c6c;
        
        &.lowest-price {
          color: #67c23a;
        }
      }
      
      .price-details {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-top: 5px;
        
        .original-price {
          color: #999;
          text-decoration: line-through;
          font-size: 12px;
        }
      }
    }
    
    .product-meta {
      margin-bottom: 15px;
      
      .meta-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        font-size: 12px;
        
        .label {
          color: #666;
        }
        
        .value {
          color: #333;
          font-weight: 500;
        }
        
        :deep(.el-rate) {
          display: inline-flex;
        }
      }
    }
    
    .product-actions {
      display: flex;
      gap: 8px;
      
      .el-button {
        flex: 1;
      }
    }
  }
}

.filter-actions,
.trend-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.chart-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .compare-header {
    padding: 20px;
    
    .title {
      font-size: 24px;
    }
    
    .product-name {
      font-size: 16px;
    }
  }
  
  .compare-main {
    padding: 20px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .filter-actions,
  .trend-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>