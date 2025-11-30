<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <!-- 错误代码 -->
      <div class="error-code">
        <span class="code-number">4</span>
        <span class="code-icon">
          <el-icon><Search /></el-icon>
        </span>
        <span class="code-number">4</span>
      </div>
      
      <!-- 错误信息 -->
      <div class="error-info">
        <h1 class="error-title">页面未找到</h1>
        <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
        
        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button 
            type="primary" 
            size="large" 
            @click="goBack"
          >
            <el-icon><ArrowLeft /></el-icon>
            返回上页
          </el-button>
          
          <el-button 
            type="success" 
            size="large" 
            @click="goHome"
          >
            <el-icon><House /></el-icon>
            返回首页
          </el-button>
          
          <el-button 
            type="info" 
            size="large" 
            @click="goSearch"
          >
            <el-icon><Search /></el-icon>
            去搜索
          </el-button>
        </div>
        
        <!-- 快速导航 -->
        <div class="quick-links">
          <p class="links-title">您可能想访问：</p>
          <div class="links-list">
            <el-link 
              v-for="link in quickLinks" 
              :key="link.path" 
              :type="link.type"
              :underline="false"
              @click="navigateTo(link.path)"
            >
              <el-icon><component :is="link.icon" /></el-icon>
              {{ link.title }}
            </el-link>
          </div>
        </div>
      </div>
      
      <!-- 背景装饰 -->
      <div class="background-decoration">
        <div class="decoration-item item-1"></div>
        <div class="decoration-item item-2"></div>
        <div class="decoration-item item-3"></div>
        <div class="decoration-item item-4"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

// 快速链接
const quickLinks = [
  {
    title: '商品搜索',
    path: '/search',
    icon: 'Search',
    type: 'primary' as const
  },
  {
    title: '比价历史',
    path: '/dashboard',
    icon: 'Histogram',
    type: 'success' as const
  },
  {
    title: '个人中心',
    path: '/dashboard',
    icon: 'User',
    type: 'warning' as const
  },
  {
    title: '系统设置',
    path: '/dashboard',
    icon: 'Setting',
    type: 'info' as const
  }
]

// 方法
const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const goHome = () => {
  router.push('/')
}

const goSearch = () => {
  router.push('/search')
}

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.not-found-content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 2;
  max-width: 600px;
  padding: 40px;
}

.error-code {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  font-size: 120px;
  font-weight: bold;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  
  .code-number {
    animation: float 3s ease-in-out infinite;
  }
  
  .code-icon {
    margin: 0 20px;
    animation: rotate 4s linear infinite;
    
    .el-icon {
      font-size: 100px;
    }
  }
  
  .code-number:nth-child(1) {
    animation-delay: 0s;
  }
  
  .code-number:nth-child(3) {
    animation-delay: 0.5s;
  }
}

.error-info {
  .error-title {
    font-size: 32px;
    font-weight: 300;
    margin-bottom: 16px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  }
  
  .error-desc {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 40px;
    line-height: 1.6;
  }
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  
  .el-button {
    padding: 12px 24px;
    border-radius: 25px;
    font-weight: 500;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
    }
    
    .el-icon {
      margin-right: 8px;
    }
  }
}

.quick-links {
  .links-title {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 16px;
  }
  
  .links-list {
    display: flex;
    gap: 20px;
    justify-content: center;
    flex-wrap: wrap;
    
    .el-link {
      color: white !important;
      padding: 8px 16px;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.2);
        transform: translateY(-1px);
      }
      
      .el-icon {
        margin-right: 6px;
        font-size: 14px;
      }
    }
  }
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  
  .decoration-item {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 6s ease-in-out infinite;
  }
  
  .item-1 {
    width: 100px;
    height: 100px;
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }
  
  .item-2 {
    width: 150px;
    height: 150px;
    top: 60%;
    right: 10%;
    animation-delay: 2s;
  }
  
  .item-3 {
    width: 80px;
    height: 80px;
    bottom: 20%;
    left: 20%;
    animation-delay: 4s;
  }
  
  .item-4 {
    width: 120px;
    height: 120px;
    top: 30%;
    right: 30%;
    animation-delay: 1s;
  }
}

// 动画定义
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .not-found-content {
    padding: 20px;
  }
  
  .error-code {
    font-size: 80px;
    
    .code-icon .el-icon {
      font-size: 70px;
    }
  }
  
  .error-title {
    font-size: 24px;
  }
  
  .error-desc {
    font-size: 14px;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
    
    .el-button {
      width: 200px;
    }
  }
  
  .links-list {
    flex-direction: column;
    align-items: center;
    
    .el-link {
      width: 200px;
      text-align: center;
    }
  }
  
  .background-decoration {
    .item-1, .item-2, .item-3, .item-4 {
      display: none;
    }
  }
}

@media (max-width: 480px) {
  .error-code {
    font-size: 60px;
    
    .code-icon .el-icon {
      font-size: 50px;
    }
  }
  
  .error-title {
    font-size: 20px;
  }
  
  .action-buttons .el-button {
    width: 100%;
    max-width: 280px;
  }
}
</style>