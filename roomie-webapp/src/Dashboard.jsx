import React from "react";
import { useNavigate } from "react-router-dom";

function Dashboard() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token"); // Remove authentication token
        navigate("/login"); // Redirect to login page
    };

    return (
        <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
            <h1 className="fw-bold">Welcome to Your Dashboard</h1>
            <p>This is your landing page after logging in.</p>

            <button className="btn btn-primary mt-3" onClick={() => navigate("/profile")}>
                Go to Profile
            </button>

            <button className="btn btn-danger mt-3" onClick={handleLogout}>
                Logout
            </button>
        </div>
    );
}

export default Dashboard;
