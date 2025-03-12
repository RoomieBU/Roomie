import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
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
        <Route path="/preferences" element={<Preferences />}/>
        <Route path = "/fileSubmit" element={<FileSubmit />} />
        <Route path = "/user/images" element={<TempImageDemo /> } />
        <Route path = "/profile/edit" element={<Edit /> } />
        <Route path="*" element={<Navigate to="/" replace />} />  {/* Redirect unknown routes */}
      </Routes>
    </Router>
  );
}

export default App;
