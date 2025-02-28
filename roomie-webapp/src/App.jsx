import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Index from "./Index";
import Profile from "./Profile";
import Login from "./Login";
import Signup from "./SignUp";
import Registration from "./Registration";
import Dashboard from "./Dashboard";
import Matching from "./Matching";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Index />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/matching" element={<Matching />}/>
        <Route path="*" element={<Navigate to="/" replace />} />  {/* Redirect unknown routes */}
      </Routes>
    </Router>
  );
}

export default App;
