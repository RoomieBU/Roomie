import React from "react";
import { useNavigate } from "react-router-dom";

import "./index.css"

function Index() {
    const navigate = useNavigate();

    return (
        <div className="manBun">
            <div className="container d-flex flex-column align-items-center vw-100 vh-100 justify-content-center">
                <h1 className="dashboard-h1">Roomie.</h1>
                <p className="dashboard-p">Find your perfect roommate.</p>

                <div className="d-flex gap-3 mt-4">
                    <button className="button" onClick={() => navigate("/login")}>
                        Login
                    </button>
                    <button className="button" onClick={() => navigate("/signup")}>
                        Sign Up
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Index;
