export {}
declare global {
  interface R<T> {
    code: number
    message: string
    data: T
  }
  interface PageResponse<T> {
    records: T[]
    total: number
    page: number
    size: number
  }
}
