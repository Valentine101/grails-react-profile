import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  // Relative asset URLs also work when a WAR is deployed below the server root.
  base: './',
  build: {
    outDir: '../src/main/resources/public',
    assetsDir: '_app',
    emptyOutDir: true,
    manifest: true,
    sourcemap: false,
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
