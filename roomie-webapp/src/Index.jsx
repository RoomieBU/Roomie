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
                    <h1>ROOMIE.</h1>
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
                        <p>Our advanced matching system allows you to be matched with those you <em>truly</em> get along with.</p>
                    </div>
                </div>
                <div className="column">
                    <div className="text-box">
                        <h3>CHAT.</h3>
                        <p>Use our platform to not only match with potential roommates, but to chat with them prior to and after the matching process is complete.</p>
                    </div>
                </div>
                <div className="column">
                    <div className="text-box">
                        <h3>BUILD.</h3>
                        <p>Build meaningful university connections and friendships that will last a lifetime.</p>
                    </div>
                </div>
            </section>
        </>
    );
}

export default Index;