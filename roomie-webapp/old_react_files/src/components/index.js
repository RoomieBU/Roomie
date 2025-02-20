import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import App from "./App";
import SignUp from "./SignUp";

ReactDOM.render(
    <Router>
        <Routes>
            <Route path="/" element={<App />} />
            <Route path="/signup" element={<SignUp />} />
        </Routes>
    </Router>,
    document.getElementById("root")
);
