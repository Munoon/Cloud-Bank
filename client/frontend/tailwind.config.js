module.exports = {
  purge: {
    enabled: true,
    content: ['./src/**/*.{js,jsx,ts,tsx}']
  },
  darkMode: false,
  theme: {
    extend: {
      colors: {
        primary: '#7b75d4',
        secondary: '#cdcaf9',
        disabled: '#899dd2',
        success: '#89e1ae'
      }
    }
  },
  variants: {
    extend: {
      backgroundColor: ['disabled'],
    }
  },
  plugins: [],
}
