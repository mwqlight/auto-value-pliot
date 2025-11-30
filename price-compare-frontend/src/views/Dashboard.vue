<template>
  <div class="dashboard-container">
    <!-- 导航栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <h1 class="logo">比价驾驶舱</h1>
        <nav class="nav-menu">
          <el-button 
            type="primary" 
            link 
            :class="{ active: activeNav === 'dashboard' }"
            @click="activeNav = 'dashboard'"
          >
            <el-icon><House /></el-icon>
            首页
          </el-button>
          <el-button 
            type="primary" 
            link 
            :class="{ active: activeNav === 'search' }"
            @click="$router.push('/search')"
          >
            <el-icon><Search /></el-icon>
            商品搜索
          </el-button>
          <el-button type="primary" link>
            <el-icon><Histogram /></el-icon>
            比价历史
          </el-button>
        </nav>
      </div>
      
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
            <span class="username">{{ userStore.nickname }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleProfile">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleSettings">设置</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="dashboard-main">
      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <div class="welcome-card">
          <h2>欢迎回来，{{ userStore.nickname }}！</h2>
          <p>开始您的商品比价之旅，发现最优购买方案</p>
          <el-button type="primary" size="large" @click="$router.push('/search')">
            <el-icon><Search /></el-icon>
            开始搜索
          </el-button>
        </div>
      </section>
      
      <!-- 数据统计 -->
      <section class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: #409eff;">
                <el-icon><ShoppingBag /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">1,234</div>
                <div class="stat-label">比价任务</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: #67c23a;">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">89.5%</div>
                <div class="stat-label">准确率</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: #e6a23c;">
                <el-icon><Coin /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥2,568</div>
                <div class="stat-label">平均节省</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: #f56c6c;">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">3.2s</div>
                <div class="stat-label">平均响应</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </section>
      
      <!-- 数据可视化 -->
      <section class="visualization-section">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>价格趋势分析</span>
                </div>
              </template>
              <PriceTrendChart :data="priceTrendData" height="300px" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>价格分布分析</span>
                </div>
              </template>
              <PriceDistributionChart :data="priceDistributionData" height="300px" />
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="24">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>平台价格对比</span>
                </div>
              </template>
              <PlatformComparisonChart :data="platformComparisonData" height="350px" />
            </el-card>
          </el-col>
        </el-row>
      </section>
      
      <!-- 最近比价 -->
      <section class="recent-section">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近比价记录</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>
          
          <el-table :data="recentTasks" style="width: 100%">
            <el-table-column prop="productName" label="商品" min-width="200" />
            <el-table-column prop="platform" label="平台" width="100">
              <template #default="{ row }">
                <el-tag :type="getPlatformType(row.platform)">
                  {{ row.platform }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="viewCompare(row.id)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import PriceTrendChart from '@/components/charts/PriceTrendChart.vue'
import PriceDistributionChart from '@/components/charts/PriceDistributionChart.vue'
import PlatformComparisonChart from '@/components/charts/PlatformComparisonChart.vue'

const router = useRouter()
const userStore = useUserStore()

// 状态
const activeNav = ref('dashboard')

// 模拟数据 - 价格趋势数据
const priceTrendData = ref([
  { date: '2024-01-01', price: 8999, platform: '淘宝' },
  { date: '2024-01-02', price: 8799, platform: '淘宝' },
  { date: '2024-01-03', price: 8699, platform: '淘宝' },
  { date: '2024-01-04', price: 8599, platform: '淘宝' },
  { date: '2024-01-05', price: 8499, platform: '淘宝' },
  { date: '2024-01-01', price: 9199, platform: '京东' },
  { date: '2024-01-02', price: 9099, platform: '京东' },
  { date: '2024-01-03', price: 8999, platform: '京东' },
  { date: '2024-01-04', price: 8899, platform: '京东' },
  { date: '2024-01-05', price: 8799, platform: '京东' },
  { date: '2024-01-01', price: 8699, platform: '拼多多' },
  { date: '2024-01-02', price: 8599, platform: '拼多多' },
  { date: '2024-01-03', price: 8499, platform: '拼多多' },
  { date: '2024-01-04', price: 8399, platform: '拼多多' },
  { date: '2024-01-05', price: 8299, platform: '拼多多' }
])

// 模拟数据 - 价格分布数据
const priceDistributionData = ref([
  { platform: '淘宝', price: 8499, count: 45 },
  { platform: '京东', price: 8799, count: 38 },
  { platform: '拼多多', price: 8299, count: 52 },
  { platform: '苏宁', price: 8699, count: 28 },
  { platform: '唯品会', price: 8599, count: 35 }
])

// 模拟数据 - 平台对比数据
const platformComparisonData = ref([
  { platform: '淘宝', avgPrice: 8499, minPrice: 7899, maxPrice: 9199, productCount: 45 },
  { platform: '京东', avgPrice: 8799, minPrice: 8199, maxPrice: 9399, productCount: 38 },
  { platform: '拼多多', avgPrice: 8299, minPrice: 7599, maxPrice: 8999, productCount: 52 },
  { platform: '苏宁', avgPrice: 8699, minPrice: 7999, maxPrice: 9299, productCount: 28 },
  { platform: '唯品会', avgPrice: 8599, minPrice: 7899, maxPrice: 9199, productCount: 35 }
])

// 模拟数据
const recentTasks = ref([
  {
    id: '1',
    productName: 'iPhone 15 Pro Max 256GB',
    platform: '淘宝',
    price: '¥8,999',
    status: '已完成',
    createTime: '2024-01-15 14:30:22'
  },
  {
    id: '2',
    productName: '小米14 Ultra 512GB',
    platform: '京东',
    price: '¥6,499',
    status: '进行中',
    createTime: '2024-01-15 13:15:10'
  },
  {
    id: '3',
    productName: '华为Mate 60 Pro 1TB',
    platform: '拼多多',
    price: '¥7,999',
    status: '已完成',
    createTime: '2024-01-14 16:45:33'
  }
])

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

const getStatusType = (status: string) => {
  const types: { [key: string]: string } = {
    '已完成': 'success',
    '进行中': 'warning',
    '失败': 'danger'
  }
  return types[status] || 'info'
}

const viewCompare = (taskId: string) => {
  router.push(`/compare/${taskId}`)
}

const handleProfile = () => {
  ElMessage.info('个人中心功能开发中...')
}

const handleSettings = () => {
  ElMessage.info('设置功能开发中...')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch {
    // 用户取消操作
  }
}

onMounted(() => {
  // 初始化用户信息
  userStore.initAuth()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 60px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  color: white;
  font-size: 24px;
  font-weight: 300;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.nav-menu {
  display: flex;
  gap: 10px;
  
  .el-button {
    color: rgba(255, 255, 255, 0.8) !important;
    font-size: 14px;
    
    &.active {
      color: white !important;
      background: rgba(255, 255, 255, 0.1) !important;
    }
    
    &:hover {
      color: white !important;
      background: rgba(255, 255, 255, 0.05) !important;
    }
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: white;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 6px;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
    
    .username {
      font-size: 14px;
    }
  }
}

.dashboard-main {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  padding: 40px;
  text-align: center;
  
  h2 {
    color: white;
    font-size: 32px;
    font-weight: 300;
    margin-bottom: 10px;
  }
  
  p {
    color: rgba(255, 255, 255, 0.8);
    font-size: 16px;
    margin-bottom: 30px;
  }
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  
  .stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      color: white;
      font-size: 24px;
    }
  }
  
  .stat-content {
    .stat-value {
      color: white;
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 4px;
    }
    
    .stat-label {
      color: rgba(255, 255, 255, 0.8);
      font-size: 14px;
    }
  }
}

.recent-section {
  .recent-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    :deep(.el-card__header) {
      background: rgba(255, 255, 255, 0.05);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      color: white;
      font-weight: 500;
    }
    
    :deep(.el-table) {
      background: transparent;
      
      th {
        background: rgba(255, 255, 255, 0.05) !important;
        color: white !important;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
      }
      
      td {
        background: transparent !important;
        color: rgba(255, 255, 255, 0.9) !important;
        border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
      }
      
      tr:hover td {
        background: rgba(255, 255, 255, 0.05) !important;
      }
    }
  }
}

.visualization-section {
  margin-bottom: 30px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  :deep(.el-card__header) {
    background: rgba(255, 255, 255, 0.05);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    color: white;
    font-weight: 500;
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>