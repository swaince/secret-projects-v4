import http from '@/utils/request'

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

export function fetchPosts(params: PostPageParams): Promise<PageResponse<PostDTO>> {
  return http.get<PageResponse<PostDTO>>('/posts', params)
}

export function createPost(data: Partial<PostDTO>): Promise<PostDTO> {
  return http.post<PostDTO>('/posts', data)
}

export function updatePost(postId: string, data: Partial<PostDTO>): Promise<PostDTO> {
  return http.put<PostDTO>(`/posts/${postId}`, data)
}

export function deletePost(postId: string): Promise<string> {
  return http.delete<string>(`/posts/${postId}`)
}

export function deletePosts(ids: string[]): Promise<string[]> {
  return http.delete<string[]>('/posts', ids)
}
