import "./Matching.css";
import "bootstrap-icons/font/bootstrap-icons.min.css";
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

function Matching() {
    const [roommate, setRoommate] = useState(null); // Store roommate data
    const [isFront, setIsFront] = useState(true); // Controls front/back swap

    const navigate = useNavigate();

    // Verify that the user is currently logged in and has a valid token
    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("http://roomie.ddns.net:8080/auth/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Invalid token");
                }
            } catch (error) {
                console.log("Redirecting to login due to invalid token.");
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    // Fetch the next potential roommate
    useEffect(() => {
        const getPotentialRoommate = async () => {
            try {
                const response = await fetch("http://roomie.ddns.net:8080/matches/getPotentialRoommate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Invalid token");
                }

                const result = await response.json();
                setName(result.getItem("name"))
                setAge(result.getItem("date_of_birth"))
                setUniversity("Bloomsburg University") // university??
                setBio(result.getItem("about_me")) // about me
                // setMajor(potentialRoomate.getItem("major"))
                setIsFront(true)



                if (!result.valid) {
                    throw new Error("Invalid token");
                }

                setRoommate(result); // Store roommate data in state
            } catch (error) {
                console.log("Redirecting to login due to invalid token.", error);
            }
        };

        getPotentialRoommate();
    }, []);

    // Set new user info to match screen
    function updateShownUser() {
        // Fetch a new potential roommate
        setRoommate(null); // Clear current roommate while loading new one
        setIsFront(true);
    }

    // Matched chosen!!
    function matched() {
        // TODO: Add logic to store the matched roommate in the database

        updateShownUser(); // Load a new potential roommate
    }

    // Declined potential user
    function declined() {
        // TODO: Add logic to remove this user from future match lists

        updateShownUser(); // Load a new potential roommate
    }

    function swapSides() {
        setIsFront(!isFront);
    }

    return (
        <div className="hold-all">
            {roommate ? (
                isFront ? (
                    <div onClick={swapSides} className="potential-roomate-front">
                        <div className="user_info">
                            <p>{roommate.name}, {roommate.date_of_birth}
                                <br />
                                Bloomsburg University
                            </p>
                        </div>
                    </div>
                ) : (
                    <div onClick={swapSides} className="potential-roomate-back">
                        <div className="more-user-info">
                            <h3>More about {roommate.name}</h3>
                            <dl>
                                <dt>Major</dt>
                                <dd>{roommate.major}</dd>
                                <dt>Bio:</dt>
                                <dd>{roommate.about_me}</dd>
                            </dl>
                        </div>
                    </div>
                )
            ) : (
                <p>Loading potential roommate...</p>
            )}

            <div className="match-button-cluster">
                <button onClick={declined} className="deny-icon">
                    <i className="bi bi-x-lg" />
                </button>

                <button onClick={matched} className="match-icon">
                    <i className="bi bi-check-lg" />
                </button>
            </div>
        </div>
    );
}

export default Matching;
