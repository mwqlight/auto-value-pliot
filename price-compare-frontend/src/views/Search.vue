<template>
  <div class="search-container">
    <!-- 搜索头部 -->
    <header class="search-header">
      <div class="header-content">
        <h1 class="title">商品搜索</h1>
        <p class="subtitle">输入商品关键词，一键比价各大电商平台</p>
        
        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入商品名称、型号或关键词..."
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button 
                type="primary" 
                :loading="searchLoading"
                @click="handleSearch"
              >
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
        
        <!-- 平台选择 -->
        <div class="platform-select">
          <div class="platform-label">选择平台：</div>
          <el-checkbox-group v-model="selectedPlatforms">
            <el-checkbox 
              v-for="platform in platforms" 
              :key="platform.value" 
              :label="platform.value"
            >
              {{ platform.label }}
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
    </header>
    
    <!-- 搜索结果 -->
    <main class="search-results" v-if="showResults">
      <!-- 搜索状态 -->
      <div class="search-status">
        <div class="status-info">
          <span class="keyword">搜索关键词：{{ searchKeyword }}</span>
          <span class="count">共找到 {{ searchResults.length }} 条结果</span>
        </div>
        <div class="sort-options">
          <el-select v-model="sortBy" placeholder="排序方式" size="small">
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="销量优先" value="sales" />
            <el-option label="好评优先" value="rating" />
          </el-select>
        </div>
      </div>
      
      <!-- 商品列表 -->
      <div class="product-list">
        <el-row :gutter="20">
          <el-col 
            v-for="product in searchResults" 
            :key="product.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
          >
            <el-card class="product-card" shadow="hover">
              <div class="product-image">
                <el-image 
                  :src="product.image" 
                  :alt="product.name"
                  fit="cover"
                  style="width: 100%; height: 200px;"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>图片加载失败</span>
                    </div>
                  </template>
                </el-image>
                <div class="platform-tag">
                  <el-tag :type="getPlatformType(product.platform)" size="small">
                    {{ product.platform }}
                  </el-tag>
                </div>
              </div>
              
              <div class="product-info">
                <h3 class="product-name" :title="product.name">
                  {{ product.name }}
                </h3>
                
                <div class="product-price">
                  <span class="current-price">{{ product.price }}</span>
                  <span class="original-price" v-if="product.originalPrice">
                    {{ product.originalPrice }}
                  </span>
                  <span 
                    class="discount" 
                    v-if="product.discount"
                  >
                    {{ product.discount }}
                  </span>
                </div>
                
                <div class="product-meta">
                  <div class="meta-item" v-if="product.sales">
                    <el-icon><ShoppingCart /></el-icon>
                    <span>销量：{{ product.sales }}</span>
                  </div>
                  <div class="meta-item" v-if="product.rating">
                    <el-icon><Star /></el-icon>
                    <span>评分：{{ product.rating }}</span>
                  </div>
                </div>
                
                <div class="product-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="comparePrice(product)"
                  >
                    比价
                  </el-button>
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="viewDetail(product)"
                  >
                    查看详情
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 分页 -->
      <div class="pagination" v-if="searchResults.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 48, 96]"
          :total="totalResults"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <!-- 无结果提示 -->
      <div class="no-results" v-if="searchResults.length === 0 && showResults">
        <el-empty description="暂无搜索结果">
          <template #image>
            <el-icon size="100"><Search /></el-icon>
          </template>
          <p>建议您：</p>
          <ul>
            <li>检查关键词拼写是否正确</li>
            <li>尝试使用更简单的关键词</li>
            <li>选择更多电商平台进行搜索</li>
          </ul>
        </el-empty>
      </div>
    </main>
    
    <!-- 热门搜索 -->
    <aside class="hot-search" v-if="!showResults">
      <el-card class="hot-search-card">
        <template #header>
          <div class="card-header">
            <span>热门搜索</span>
          </div>
        </template>
        
        <div class="hot-keywords">
          <el-tag
            v-for="keyword in hotKeywords"
            :key="keyword"
            class="hot-tag"
            effect="dark"
            @click="searchKeyword = keyword; handleSearch()"
          >
            {{ keyword }}
          </el-tag>
        </div>
      </el-card>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

// 搜索状态
const searchKeyword = ref('')
const searchLoading = ref(false)
const showResults = ref(false)

// 平台选择
const platforms = ref([
  { label: '淘宝', value: 'taobao' },
  { label: '京东', value: 'jd' },
  { label: '拼多多', value: 'pdd' },
  { label: '苏宁', value: 'suning' },
  { label: '唯品会', value: 'vip' }
])
const selectedPlatforms = ref(['taobao', 'jd', 'pdd'])

// 排序和分页
const sortBy = ref('price_asc')
const currentPage = ref(1)
const pageSize = ref(12)
const totalResults = ref(0)

// 热门搜索关键词
const hotKeywords = ref([
  'iPhone 15', '小米14', '华为Mate 60', 'MacBook Pro',
  '戴尔显示器', '索尼耳机', '佳能相机', '耐克运动鞋',
  '阿迪达斯', '李宁', '美的空调', '格力冰箱'
])

// 模拟搜索结果
const searchResults = ref([
  {
    id: '1',
    name: 'Apple iPhone 15 Pro Max 256GB 原色钛金属',
    platform: '淘宝',
    price: '¥8,999',
    originalPrice: '¥9,999',
    discount: '9折',
    image: 'https://via.placeholder.com/300x300?text=iPhone+15',
    sales: '2.5万+',
    rating: '4.9'
  },
  {
    id: '2',
    name: '小米14 Ultra 512GB 白色 骁龙8 Gen3',
    platform: '京东',
    price: '¥6,499',
    originalPrice: '¥6,999',
    discount: '7.2折',
    image: 'https://via.placeholder.com/300x300?text=小米14',
    sales: '1.8万+',
    rating: '4.8'
  },
  {
    id: '3',
    name: '华为Mate 60 Pro 1TB 雅川青 卫星通话',
    platform: '拼多多',
    price: '¥7,999',
    image: 'https://via.placeholder.com/300x300?text=华为Mate60',
    sales: '3.2万+',
    rating: '4.7'
  }
])

// 计算属性
const sortedResults = computed(() => {
  const results = [...searchResults.value]
  
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
    default:
      return results
  }
})

// 方法
const getPlatformType = (platform: string) => {
  const types: { [key: string]: string } = {
    '淘宝': 'danger',
    '京东': 'primary',
    '拼多多': 'warning',
    '苏宁': 'success',
    '唯品会': 'info'
  }
  return types[platform] || 'info'
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  if (selectedPlatforms.value.length === 0) {
    ElMessage.warning('请至少选择一个电商平台')
    return
  }
  
  searchLoading.value = true
  
  try {
    // 模拟搜索延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 这里应该调用实际的搜索API
    // const response = await searchApi.searchProducts({
    //   keyword: searchKeyword.value,
    //   platforms: selectedPlatforms.value,
    //   page: currentPage.value,
    //   size: pageSize.value
    // })
    
    showResults.value = true
    totalResults.value = searchResults.value.length
    
    ElMessage.success(`找到 ${searchResults.value.length} 条相关结果`)
  } catch (error) {
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    searchLoading.value = false
  }
}

const comparePrice = (product: any) => {
  ElMessage.info(`开始比价：${product.name}`)
  // 这里应该跳转到比价页面或触发比价任务
}

const viewDetail = (product: any) => {
  ElMessage.info(`查看详情：${product.name}`)
  // 这里应该跳转到商品详情页面
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  handleSearch()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  handleSearch()
}
</script>

<style lang="scss" scoped>
.search-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.search-header {
  padding: 60px 30px 40px;
  text-align: center;
  
  .header-content {
    max-width: 800px;
    margin: 0 auto;
  }
  
  .title {
    color: white;
    font-size: 36px;
    font-weight: 300;
    margin-bottom: 10px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  }
  
  .subtitle {
    color: rgba(255, 255, 255, 0.8);
    font-size: 16px;
    margin-bottom: 30px;
  }
}

.search-box {
  margin-bottom: 30px;
  
  :deep(.el-input-group__append) {
    background: #409eff;
    border-color: #409eff;
    
    .el-button {
      color: white;
    }
  }
}

.platform-select {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  
  .platform-label {
    color: white;
    font-size: 14px;
  }
  
  :deep(.el-checkbox-group) {
    display: flex;
    gap: 20px;
    
    .el-checkbox {
      color: white;
      
      .el-checkbox__label {
        color: white;
      }
    }
  }
}

.search-results {
  background: white;
  min-height: calc(100vh - 300px);
  padding: 30px;
}

.search-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  
  .status-info {
    display: flex;
    gap: 20px;
    
    .keyword {
      font-weight: 500;
      color: #333;
    }
    
    .count {
      color: #666;
    }
  }
}

.product-list {
  margin-bottom: 30px;
}

.product-card {
  height: 100%;
  transition: transform 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
  
  .product-image {
    position: relative;
    
    .image-error {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 200px;
      color: #999;
      
      .el-icon {
        font-size: 48px;
        margin-bottom: 10px;
      }
    }
    
    .platform-tag {
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }
  
  .product-info {
    padding: 15px;
    
    .product-name {
      font-size: 14px;
      line-height: 1.4;
      height: 40px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      margin-bottom: 10px;
      color: #333;
    }
    
    .product-price {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 10px;
      
      .current-price {
        font-size: 18px;
        font-weight: bold;
        color: #f56c6c;
      }
      
      .original-price {
        font-size: 12px;
        color: #999;
        text-decoration: line-through;
      }
      
      .discount {
        font-size: 12px;
        color: #67c23a;
        background: #f0f9ff;
        padding: 2px 6px;
        border-radius: 3px;
      }
    }
    
    .product-meta {
      display: flex;
      gap: 15px;
      margin-bottom: 15px;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #666;
        
        .el-icon {
          font-size: 12px;
        }
      }
    }
    
    .product-actions {
      display: flex;
      gap: 8px;
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.no-results {
  text-align: center;
  padding: 60px 0;
  
  p {
    color: #666;
    margin-bottom: 10px;
  }
  
  ul {
    text-align: left;
    display: inline-block;
    color: #999;
    
    li {
      margin-bottom: 5px;
    }
  }
}

.hot-search {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 30px 60px;
  
  .hot-search-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    :deep(.el-card__header) {
      background: rgba(255, 255, 255, 0.05);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      color: white;
      font-weight: 500;
    }
  }
}

.hot-keywords {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  cursor: pointer;
  transition: transform 0.2s ease;
  
  &:hover {
    transform: scale(1.05);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>