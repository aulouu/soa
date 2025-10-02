import { Box, Container } from "@mui/material";
import HumanBeingsPage from "./pages/HumanBeingsPage";

function App() {
  return (
    <Box
      component='main'
      sx={{ py: 4 }}
    >
      <Container maxWidth='xl'>
        <HumanBeingsPage />
      </Container>
    </Box>
  );
}

export default App;
