/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        display: ["Poppins", "ui-sans-serif", "system-ui", "sans-serif"],
        body: ["Plus Jakarta Sans", "ui-sans-serif", "system-ui", "sans-serif"]
      },
      boxShadow: {
        glass: "0 18px 50px rgba(2, 8, 23, 0.12)"
      }
    }
  },
  plugins: []
};
