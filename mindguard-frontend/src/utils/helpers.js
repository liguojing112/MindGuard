export function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString()
}
