/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          ink: '#0F172A',
          deep: '#102A43',
          ocean: '#1B6CA8',
          aurora: '#2EC4B6',
          copper: '#F4A259',
        },
      },
      boxShadow: {
        premium: '0 24px 70px rgba(15, 23, 42, 0.35)',
      },
    },
  },
  plugins: [],
}
