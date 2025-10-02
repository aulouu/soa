import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
    base: './',
    plugins: [
        react()
    ],
    logLevel: 'warning',
    resolve: {
        alias: {
            // Add these aliases
            'sockjs-client': 'sockjs-client/dist/sockjs.min.js',
        }
    },
    define: {
        global: 'window',
    },
    optimizeDeps: {
        include: ['sockjs-client']
    }
});
