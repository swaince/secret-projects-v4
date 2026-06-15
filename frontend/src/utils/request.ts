import axios, { type AxiosInstance, type AxiosResponse, type CreateAxiosDefaults } from 'axios'
import { toast } from 'vue-sonner'

class HttpClient {
  private instance: AxiosInstance

  constructor(config?: CreateAxiosDefaults) {
    this.instance = axios.create(config)

    this.instance.interceptors.response.use(
      (response: AxiosResponse<R<unknown>>) => {
        const body = response.data
        if (body.code === 200) return body.data as any
        toast.error(body.message || '请求失败')
        return Promise.reject(body)
      },
      (error) => {
        const msg = error.response?.data?.message || error.message || '网络错误'
        toast.error(msg)
        return Promise.reject(error)
      },
    )
  }

  get<T, D=T>(url: string, params?: object): Promise<D> {
    return this.instance.get<T, D>(url, { params })
  }

  post<T, D=T>(url: string, data?: unknown): Promise<D> {
    return this.instance.post<T, D>(url, data)
  }

  put<T, D=T>(url: string, data?: unknown): Promise<D> {
    return this.instance.put<T, D>(url, data)
  }

  delete<T, D=T>(url: string, data?: unknown): Promise<D> {
    return this.instance.delete<T, D>(url, { data })
  }
}

const http = new HttpClient({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

export default http
