// 认证相关类型定义

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  userId: number
  username: string
  nickname: string
  accessToken: string
  tokenType: string
  expiresIn: number
}

export interface UserInfo {
  userId: number
  username: string
  nickname: string
  email?: string
  phone?: string
  avatar?: string
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}