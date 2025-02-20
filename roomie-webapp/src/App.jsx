import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Login"; // Import the Login component
import SignUp from "./SignUp"; // Import the SignUp component


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<h1>Hello World</h1>} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
      </Routes>
    </Router>
  );
}

export default App
