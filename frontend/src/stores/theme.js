import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(localStorage.getItem('theme') === 'dark')

  watch(isDark, (val) => {
    localStorage.setItem('theme', val ? 'dark' : 'light')
    document.documentElement.setAttribute('data-theme', val ? 'dark' : 'light')
  })

  function toggle() {
    isDark.value = !isDark.value
  }

  // Init
  if (isDark.value) {
    document.documentElement.setAttribute('data-theme', 'dark')
  }

  return { isDark, toggle }
})
