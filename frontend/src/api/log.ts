import http from '@/utils/request'

export interface AccessLogDTO {
  logId: string
  requestId: string
  requestUrl: string
  requestMethod: string
  requestHeaders: string | null
  requestParams: string | null
  requestBody: string | null
  responseStatus: number | null
  durationMs: number | null
  clientIp: string | null
  userAgent: string | null
  userId: string | null
  username: string | null
  exceptionStack: string | null
  createdAt: string
}

export interface OperationLogDTO extends AccessLogDTO {
  controllerClass: string | null
  controllerMethod: string | null
}

export interface LogPageParams {
  page: number
  size: number
  requestUrl?: string
  username?: string
  responseStatus?: number
  requestMethod?: string
  startTime?: string
  endTime?: string
}

export interface OperationLogPageParams extends LogPageParams {
  controllerMethod?: string
}

export function fetchAccessLogs(params: LogPageParams): Promise<PageResponse<AccessLogDTO>> {
  return http.get<PageResponse<AccessLogDTO>>('/logs/access', params)
}

export function fetchAccessLogDetail(logId: string): Promise<AccessLogDTO> {
  return http.get<AccessLogDTO>(`/logs/access/${logId}`)
}

export function fetchOperationLogs(
  params: OperationLogPageParams,
): Promise<PageResponse<OperationLogDTO>> {
  return http.get<PageResponse<OperationLogDTO>>('/logs/operation', params)
}

export function fetchOperationLogDetail(logId: string): Promise<OperationLogDTO> {
  return http.get<OperationLogDTO>(`/logs/operation/${logId}`)
}
