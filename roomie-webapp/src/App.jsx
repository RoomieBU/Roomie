import { BrowserRouter as Router, Routes, Route, Link, Navigate, useLocation } from "react-router-dom";
import Index from "./pages/Index";
import Profile from "./pages/Profile";
import Login from "./pages/Login";
import Signup from "./pages/SignUp";
import Registration from "./pages/Registration";
import Dashboard from "./pages/Dashboard";
import Matching from "./components/Matching";
import FileSubmit from "./components/FileSubmit";
import Preferences from "./pages/Preferences";
import TempImageDemo from "./TempImageDemo";
import Edit from "./pages/Edit";
import About from './pages/About';
import RoommateManagementDashboard from './pages/RoommateManagementDashboard';
import roomieLogo from './assets/roomie-favicon.svg';
import RoommateRating from './pages/RoommateRating';
import RoommateReporting from './pages/RoommateReporting';
import HousingOptions from "./pages/HousingOptions";
import RoommateChat from "./pages/RoommateChat";
import SharedSupply from "./pages/SharedSupply";
import SharedCalendar from "./pages/SharedCalendar";

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
        <Route path="/RoommateRating" element={<RoommateRating />} />
        <Route path="/RoommateReporting" element={<RoommateReporting />} />
        <Route path="/housingOptions" element={<HousingOptions/>}/>
        <Route path="/RoommateChat" element={<RoommateChat />} />
        <Route path="/SharedCalendar" element={<SharedCalendar />} />
        <Route path="/SharedSupply" element={<SharedSupply />} />
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