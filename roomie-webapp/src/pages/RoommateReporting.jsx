import React, { useState } from 'react';
import './RoommateReporting.css';
import roomieLogo from '../assets/roomie-favicon.svg';

const RoommateReporting = () => {
    const [feedback, setFeedback] = useState("");
    const [submitted, setSubmitted] = useState(false);
    const [isAnonymous, setIsAnonymous] = useState(false);

    const handleSubmit = () => {
        setSubmitted(true);
    };

    return (
        <div className="rating-container">
            <div className="dashboard-wrapper">
                <header className="dashboard-header">
                    <div className="logo">
                        <a href="/RoommateManagementDashboard"><img src={roomieLogo} alt="Roomie Logo" /></a>
                    </div>
                    <div className="nav-links">
                        <a href="/dashboard">EDIT PROFILE</a>
                        <a href="/">SIGN OUT.</a>
                    </div>
                </header>
            </div>

            <header className="reporting-header">
                <h1>Report a Roommate Issue.</h1>
                <h2>Report any roommate issue in this section, choose whether you'd like to remain anonymous or not.</h2>
            </header>
            <div className="reporting-box">
                {!submitted ? (
                    <>
                        <p className="reporting-text">
                            REPORT ROOMMATE ISSUES:
                        </p>
                        <div className="anonymous-checkbox">
                            <input
                                type="checkbox"
                                id="anonymous"
                                checked={isAnonymous}
                                onChange={(e) => setIsAnonymous(e.target.checked)}
                            />
                            <label htmlFor="anonymous">Remain Anonymous</label>
                        </div>
                        <div className="specific-issue">
                            <label>Report Issue Below:</label>
                            <textarea
                                placeholder="Add roommate issue you would like to submit for further review here."
                                value={feedback}
                                onChange={(e) => setFeedback(e.target.value)}
                            />
                        </div>
                        <button className="submit-button" onClick={handleSubmit}>
                            Submit
                        </button>
                    </>
                ) : (
                    <div className="confirmation">
                        <h3>Reporting Submission Successful</h3>
                        <p>You successfully reported your issue.</p>
                        <a href="/RoommateManagementDashboard" className="dashboard-link">
                            Click here to go back to your Roommate Management Dashboard.
                        </a>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RoommateReporting;