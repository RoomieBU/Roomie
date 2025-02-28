import React from "react";
import { useNavigate } from "react-router-dom";

function Index() {
    const navigate = useNavigate();

    return (
        <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
            <h1 className="fw-bold">Welcome to ROOMIE</h1>
            <p>Your go-to platform for finding and managing roommates.</p>

            <div className="d-flex gap-3 mt-4">
                <button className="btn btn-primary" onClick={() => navigate("/login")}>
                    Login
                </button>
                <button className="btn btn-secondary" onClick={() => navigate("/signup")}>
                    Sign Up
                </button>
            </div>
        </div>
    );
}

export default Index;
