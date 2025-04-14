import React, { useState } from 'react';
import './RoommateRating.css';
import roomieLogo from '../assets/roomie-favicon.svg';

const RoommateRating = () => {
    const [rating, setRating] = useState(0);
    const [hovered, setHovered] = useState(0);
    const [submitted, setSubmitted] = useState(false);
    const [feedback, setFeedback] = useState('');

    const handleRating = (value) => {
        setRating(value);
    };

    const handleSubmit = () => {
        setSubmitted(true);
    };

    return (
        <div className="rating-container">
            <div className="dashboard-wrapper">
                <header className="dashboard-header">
                    <div className="logo">
                        <a href="/"><img src={roomieLogo} alt="Roomie Logo" /></a>
                    </div>
                    <div className="nav-links">
                        <a href="/dashboard">EDIT PROFILE</a>
                        <a href="/">SIGN OUT.</a>
                    </div>
                </header>
            </div>
            
            <header className="rating-header">
                <h1>Rate Your “ROOMIE.”</h1>
                <p>
                    Let us know how your roommate matching experience has been with “ROOMIE.”, and help us
                    with any potential improvements to our match-making techniques in the future.
                </p>
            </header>
            <div className="rating-box">
                {!submitted ? (
                    <>
                        <p className="question-text">
                            Did you find that our application matched you with your “dream” roommate? Let us know
                            below.
                        </p>
                        <div className="stars">
                            {[1, 2, 3, 4, 5].map((value) => (
                                <span
                                    key={value}
                                    className={`star ${value <= (hovered || rating) ? 'filled' : ''}`}
                                    onClick={() => handleRating(value)}
                                    onMouseEnter={() => setHovered(value)}
                                    onMouseLeave={() => setHovered(0)}
                                > ★
                                </span>
                            ))}
                        </div>
                        <div className="feedback">
                            <label>Can you tell us more?</label>
                            <textarea
                                placeholder="Add more in-depth roommate feedback here."
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
                        <h3>Rating Submission Successful</h3>
                        <p>
                            You rated your roommate <strong>{rating}</strong> star{rating > 1 ? 's' : ''}.
                        </p>
                        <a href="/RoommateManagementDashboard" className="dashboard-link">
                            Click here to go back to your Roommate Management Dashboard.
                        </a>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RoommateRating;