import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './pages/index.css'
import App from './App.jsx'
import 'bootstrap/dist/css/bootstrap.min.css';
import React from 'react';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)