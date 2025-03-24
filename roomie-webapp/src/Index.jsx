/* main dashboard page */
import React from "react";
import { useNavigate } from "react-router-dom";
import "./index.css"

function Index() {
    const navigate = useNavigate();

    return (
        <>
            <section className="hero">
                <div className="hero-content">
                    <h1>roomie.</h1>
                    <p>Find your dream roommate.</p>
                    <button className="signup-button" onClick={() => navigate("/signup")}>
                        Join Now!
                    </button>

                    <div className="arrow-down">
                        <a href="#features">↓</a>
                    </div>
                </div>
            </section>

            <section id="features" className="features-section">
                <div className="column">
                    <div className="text-box">
                        <h3>MATCH.</h3>
                        <p>Short description of this feature will go here.</p>
                    </div>
                </div>
                <div className="column">
                    <div className="text-box">
                        <h3>CHAT.</h3>
                        <p>Short description of this feature will go here.</p>
                    </div>
                </div>
                <div className="column">
                    <div className="text-box">
                        <h3>BUILD LIFELONG CONNECTIONS.</h3>
                        <p>Short description of this feature will go here.</p>
                    </div>
                </div>
            </section>
        </>
    );
}

export default Index;