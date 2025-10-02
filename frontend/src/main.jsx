import { ThemeProvider } from "@mui/material";
import ReactDOM from "react-dom/client";
import { ToastContainer } from "react-toastify";
import App from "./App.jsx";
import theme from "./theme.js";

ReactDOM.createRoot(document.getElementById("root")).render(
    <ThemeProvider theme={theme}>
        <App />
        <ToastContainer
            position="top-right"
            autoClose={3000}
            limit={2}
            hideProgressBar={false}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            theme="light"
        />
    </ThemeProvider>
);

