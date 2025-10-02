"use client"

import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import MainPage from "./MainPage.jsx";

export default function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage/>}/>
            </Routes>
        </Router>
    );
}
