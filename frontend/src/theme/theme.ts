import { createTheme } from "@mui/material/styles";

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
  typography: {
    h4: {
      fontWeight: 600,
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
      defaultProps: {
        elevation: 1,
      },
    },
  },
});

export default theme;
