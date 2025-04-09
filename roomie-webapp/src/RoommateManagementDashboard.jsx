import React from 'react';
import './RoommateManagementDashboard.css';
import roomieLogo from './assets/roomie-favicon.svg';

const RoommateManagementDashboard = () => {
    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="logo">
                    <a href="/">
                        <img src={roomieLogo} alt="Roomie Logo" />
                    </a>
                </div>
                <div className="nav-links">
                    <a href="/Dashboard">EDIT PROFILE</a>
                    <a href="/">SIGN OUT.</a>
                </div>
            </header>

            <section className="hero-section">
                <h1>ROOMIE.</h1>
                <h2>Roommate Management.</h2>
                <p>Congratulations! It's a match.<br />
                Here you will now be able to manage your shared living space effectively and efficiently.</p>
            </section>

            <main className="main-content">
                <div className="left-section">
                    <div className="feature-box">
                        <div className="feature-emoji">
                            <p>📲</p>
                        </div>
                        <div className="feature-info">
                            <h3>Chat Room</h3>
                            <p>
                                Start a private conversation with your roommate. Discuss cleaning schedules, grocery lists, and more.
                            </p>
                            <div className="dots-row">
                                <span className="dot dot-purple"></span><span>You</span>
                                <span className="dot dot-green"></span><span>Emily</span>
                                <span className="dot dot-yellow"></span><span>Matthew</span>
                            </div>
                        </div>
                    </div>

                    <div className="feature-box">
                        <div className="feature-emoji">
                            <p>🗓️</p>
                        </div>
                        <div className="feature-info">
                            <h3>Access Shared Calendar</h3>
                            <p>
                                Share a calendar with your roommates. Input your individual class schedule, extracurricular schedule, events taking place at the residence, and more.
                            </p>
                            <div className="dots-row">
                                <span className="dot dot-purple"></span><span>You</span>
                                <span className="dot dot-green"></span><span>Emily</span>
                                <span className="dot dot-yellow"></span><span>Matthew</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="right-section">
                    <div className="right-option">⭐ Rate Your Roommate<br/><span>Give your roommate a rating out of 5</span></div>
                    <div className="right-option">🔔 Send Your Roommate an Alert<br/><span>Alert your roommate on important reminders</span></div>
                    <div className="right-option">🚨 Report a Roommate Issue<br/><span>Report a roommate issue and assign its priority</span></div>
                </div>
            </main>
        </div>
    );
};

export default RoommateManagementDashboard;
