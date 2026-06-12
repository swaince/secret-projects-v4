export interface PostDTO {
  postId: string
  postName: string
  postCode: string
  postLevel: number
  sortOrder: number
  remark: string
  status: number
  builtIn: number
  createdAt: string
}

export interface PostPageParams {
  page: number
  size: number
  postName?: string
  postCode?: string
  postLevel?: number
}

import type { PageResponse, R } from '@/api/dict'

const BASE = '/api'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${url}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  const json: R<T> = await res.json()
  if (json.code !== 200) throw new Error(json.message)
  return json.data
}

export function fetchPosts(params: PostPageParams): Promise<PageResponse<PostDTO>> {
  const query = new URLSearchParams({
    page: String(params.page),
    size: String(params.size),
    ...(params.postName ? { postName: params.postName } : {}),
    ...(params.postCode ? { postCode: params.postCode } : {}),
    ...(params.postLevel ? { postLevel: String(params.postLevel) } : {}),
  })
  return request<PageResponse<PostDTO>>(`/posts?${query}`)
}

export function createPost(data: Partial<PostDTO>): Promise<PostDTO> {
  return request<PostDTO>('/posts', { method: 'POST', body: JSON.stringify(data) })
}

export function updatePost(postId: string, data: Partial<PostDTO>): Promise<PostDTO> {
  return request<PostDTO>(`/posts/${postId}`, { method: 'PUT', body: JSON.stringify(data) })
}

export function deletePost(postId: string): Promise<string> {
  return request<string>(`/posts/${postId}`, { method: 'DELETE' })
}

export function deletePosts(ids: string[]): Promise<string[]> {
  return request<string[]>('/posts', { method: 'DELETE', body: JSON.stringify(ids) })
}
