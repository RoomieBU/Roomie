
import { BrowserRouter as Router, Routes, Route, Link, Navigate, useLocation } from "react-router-dom";
import Index from "./Index";
import Profile from "./Profile";
import Login from "./Login";
import Signup from "./SignUp";
import Registration from "./Registration";
import Dashboard from "./Dashboard";
import Matching from "./Matching";
import FileSubmit from "./FileSubmit";
import Preferences from "./Preferences";
import TempImageDemo from "./TempImageDemo";
import Edit from "./Edit";
import About from './About';
import RoommateManagementDashboard from './RoommateManagementDashboard';
import roomieLogo from './assets/roomie-favicon.svg';

function AppWrapper() {
  const location = useLocation();
  // Header shown on certain pages
  const showHeader = [
    "/",           // Home
    "/about",      // About
    "/login",      // Login
    "/signup",     // Signup
  ].includes(location.pathname);

  return (
    <>
      {showHeader && (
        <header className="navbar">
          <div className="navbar-left">
            <div className="logo">
              <a href="/">
                <img src={roomieLogo} alt="Roomie Logo" />
              </a>
            </div>
          </div>
          <nav className="navbar-right">
            <ul>
              <li>
                <Link to="/login">Login</Link>
              </li>
              <li>
                <Link to="/about">About Us</Link>
              </li>
            </ul>
          </nav>
        </header>
      )}

      <Routes>
        <Route path="/" element={<Index />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/matching" element={<Matching />} />
        <Route path="/preferences" element={<Preferences />} />
        <Route path="/fileSubmit" element={<FileSubmit />} />
        <Route path="/user/images" element={<TempImageDemo />} />
        <Route path="/profile/edit" element={<Edit />} />
        <Route path="/About" element={<About />} />
        <Route path="/RoommateManagementDashboard" element={<RoommateManagementDashboard />} />
        {/* Redirect unknown routes to home */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}

function App() {
  return (
    <Router>
      <AppWrapper />
    </Router>
  );
}

export default App;