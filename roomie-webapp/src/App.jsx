import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./Login";
import Signup from "./SignUp";
import Matching from "./Matching";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/matching" element={<Matching />}/>
        <Route path="*" element={<Navigate to="/" replace />} />  {/* Redirect unknown routes */}
      </Routes>
    </Router>
  );
}

export default App;
