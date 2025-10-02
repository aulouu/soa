import {createTheme} from "@mui/material";

const theme = createTheme({
    palette: {
        primary: {
            main: "#c62828",
            light: "#ff5f52",
            dark: "#8e0000",
        },
        secondary: {
            main: "#d32f2f",
        },
        background: {
            default: "#fafafa",
            paper: "#ffffff",
        },
    },
});

export default theme;
