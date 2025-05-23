import React, { useState, useEffect } from 'react';
import './RoommateRating.css';
import RoommateNavBar from '../components/RoommateNavBar';

const RoommateRating = () => {
    const [roommates, setRoommates] = useState([]);
    const [selectedRoommate, setSelectedRoommate] = useState('');
    const [rating, setRating] = useState(0);
    const [hovered, setHovered] = useState(0);
    const [submitted, setSubmitted] = useState(false);
    const [feedback, setFeedback] = useState('');
    const [userEmail, setUserEmail] = useState('');
    const [groupChats, setGroupChats] = useState([]);
    const [loading, setLoading] = useState(false);

    // Get what groupchats the user is part of
    const fetchUserEmail = async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/profile/getUserEmail", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });

            if (!response.ok) throw new Error("Failed to fetch user email");
            const result = await response.json();
            setUserEmail(result.email);
        } catch (error) {
            console.error("Error fetching user email: ", error);
        }
    };

    const fetchChats = async () => {
        setLoading(true);
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getConfirmedRoommates", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });
    
            if (!response.ok) throw new Error("Failed to fetch Roommates");
            const result = await response.json();
            setGroupChats(result);
            
            // Process the groupChats to extract unique emails
            const uniqueEmails = new Set();
            result.forEach(chat => {
                if (chat.email1) uniqueEmails.add(chat.email1);
                if (chat.email2) uniqueEmails.add(chat.email2);
                if (chat.email3) uniqueEmails.add(chat.email3);
                if (chat.email4) uniqueEmails.add(chat.email4);
                if (chat.email5) uniqueEmails.add(chat.email5);
                if (chat.email6) uniqueEmails.add(chat.email6);
            });
            
            // Convert Set to array, filter out the current user's email, and remove empty values
            const roommateEmails = Array.from(uniqueEmails)
                .filter(email => email && email.trim() !== "" && email !== userEmail);
            setRoommates(roommateEmails);
        } catch (error) {
            console.error("Error fetching Roommates", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUserEmail();
    }, []);

    useEffect(() => {
        if (userEmail) {
            fetchChats();
        }
    }, [userEmail]);
    

    const handleRating = (value) => {
        setRating(value);
    };

    const handleSubmit = async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/rating/submit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    token: localStorage.getItem("token"),
                    rated_user: selectedRoommate,
                    rating_value: rating,
                    comment: feedback
                })
            });

            if (!response.ok) throw new Error("Failed to submit rating");
            const result = await response.json();
            console.log(result.message);
            setSubmitted(true);
        } catch (error) {
            console.error("Error submitting rating: ", error);
        }
    };

    return (
        <div className="rating-container">
            <div className="dashboard-wrapper">
                <RoommateNavBar/>
            </div>
            
            <header className="rating-header">
                <h1>Rate Your "ROOMIE."</h1>
                <p>
                    Let us know how your roommate matching experience has been with "ROOMIE.", and help us
                    with any potential improvements to our match-making techniques in the future.
                </p>
            </header>
            <div className="rating-box">
                {!submitted ? (
                    <>
                        <p className="question-text">
                            Did you find that our application matched you with your "dream" roommate? Let us know
                            below.
                        </p>

                        {/* Dropdown to choose which roommate to rate */}
                        <div className="roommate-select">
                            <label htmlFor="roommate">Who are you rating?</label>
                            <select
                                id="roommate"
                                value={selectedRoommate}
                                onChange={e => setSelectedRoommate(e.target.value)}
                            >
                                <option value="" disabled>— Select a roommate —</option>
                                {roommates.map((email, index) => (
                                    <option key={index} value={email}>
                                        {email}
                                    </option>
                                ))}
                            </select>
                        </div>
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