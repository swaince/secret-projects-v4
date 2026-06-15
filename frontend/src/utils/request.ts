import axios, { type AxiosInstance, type AxiosResponse, type CreateAxiosDefaults } from 'axios'
import { toast } from 'vue-sonner'
import { useLoadingStore } from '@/stores/loading'

declare module 'axios' {
  interface AxiosRequestConfig {
    silent?: boolean
  }
  interface InternalAxiosRequestConfig {
    silent?: boolean
  }
}

export interface RequestOptions {
  silent?: boolean
}

class HttpClient {
  private instance: AxiosInstance

  constructor(config?: CreateAxiosDefaults) {
    this.instance = axios.create(config)

    this.instance.interceptors.request.use((config) => {
      if (!config.silent) {
        useLoadingStore().start()
      }
      return config
    })

    this.instance.interceptors.response.use(
      (response: AxiosResponse<R<unknown>>) => {
        if (!response.config.silent) {
          useLoadingStore().end()
        }
        const body = response.data
        if (body.code === 200) return body.data as any
        toast.error(body.message || '请求失败')
        return Promise.reject(body)
      },
      (error) => {
        if (!error.config?.silent) {
          useLoadingStore().end()
        }
        const msg = error.response?.data?.message || error.message || '网络错误'
        toast.error(msg)
        return Promise.reject(error)
      },
    )
  }

  get<T, D = T>(url: string, params?: object, options?: RequestOptions): Promise<D> {
    return this.instance.get<T, D>(url, { params, silent: options?.silent })
  }

  post<T, D = T>(url: string, data?: unknown, options?: RequestOptions): Promise<D> {
    return this.instance.post<T, D>(url, data, { silent: options?.silent })
  }

  put<T, D = T>(url: string, data?: unknown, options?: RequestOptions): Promise<D> {
    return this.instance.put<T, D>(url, data, { silent: options?.silent })
  }

  delete<T, D = T>(url: string, data?: unknown, options?: RequestOptions): Promise<D> {
    return this.instance.delete<T, D>(url, { data, silent: options?.silent })
  }
}

const http = new HttpClient({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

export default http
