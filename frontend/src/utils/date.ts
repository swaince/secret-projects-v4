import { CalendarDate } from '@internationalized/date'

export function parseCalendarDate(str: string | null | undefined): CalendarDate | undefined {
  if (!str) return undefined
  const d = new Date(str)
  if (Number.isNaN(d.getTime())) return undefined
  return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate())
}

export function formatCalendarDate(date: CalendarDate | undefined): string {
  if (!date) return ''
  return `${date.year}-${String(date.month).padStart(2, '0')}-${String(date.day).padStart(2, '0')} 00:00:00`
}
