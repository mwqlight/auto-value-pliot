import request from './index'
import type { LoginRequest, LoginResponse, ApiResponse } from '@/types/auth'

/**
 * 用户登录
 */
export const loginApi = (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
  return request.post('/v1/auth/login', data)
}

/**
 * 用户注册
 */
export const registerApi = (data: any): Promise<ApiResponse<any>> => {
  return request.post('/v1/auth/register', data)
}

/**
 * 获取用户信息
 */
export const getUserInfoApi = (): Promise<ApiResponse<any>> => {
  return request.get('/v1/user/info')
}