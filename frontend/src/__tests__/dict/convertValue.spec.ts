import { describe, expect, it } from 'vitest'
import { convertValue } from '@/stores/dict'

describe('convertValue', () => {
  it('STRING 保持原值', () => {
    expect(convertValue('hello', 'STRING')).toBe('hello')
  })

  it('NUMBER 转数字', () => {
    expect(convertValue('42', 'NUMBER')).toBe(42)
    expect(convertValue('3.14', 'NUMBER')).toBe(3.14)
  })

  it('BOOLEAN true', () => {
    expect(convertValue('true', 'BOOLEAN')).toBe(true)
    expect(convertValue('1', 'BOOLEAN')).toBe(true)
  })

  it('BOOLEAN false', () => {
    expect(convertValue('false', 'BOOLEAN')).toBe(false)
    expect(convertValue('0', 'BOOLEAN')).toBe(false)
  })

  it('OBJECT 解析 JSON', () => {
    expect(convertValue('{"a":1}', 'OBJECT')).toEqual({ a: 1 })
  })

  it('ARRAY 解析 JSON', () => {
    expect(convertValue('[1,2,3]', 'ARRAY')).toEqual([1, 2, 3])
  })

  it('OBJECT 解析失败降级为 string', () => {
    expect(convertValue('not json', 'OBJECT')).toBe('not json')
  })

  it('ARRAY 解析失败降级为 string', () => {
    expect(convertValue('not json', 'ARRAY')).toBe('not json')
  })
})
