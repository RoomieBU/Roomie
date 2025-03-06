import "./Matching.css";
import "bootstrap-icons/font/bootstrap-icons.min.css";
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

function Matching() {
    const [roommate, setRoommate] = useState(null); // Store roommate data
    const [isLoading, setIsLoading] = useState(true); // Loading state
    const [error, setError] = useState(null); // Error state
    const [isFront, setIsFront] = useState(true); // Controls front/back swap
    const [age, setAge] = useState(0); // Stores Calculated Age of roomate shown

    const navigate = useNavigate();

    // Verify that the user is currently logged in and has a valid token
    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/auth/verify", {
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
            setIsLoading(true);
            setError(null);
            try {
                const response = await fetch("https://roomie.ddns.net:8080/matches/getPotentialRoommate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Failed to fetch potential roommate");
                }

                const result = await response.json();
                setRoommate(result); // Store roommate data in state
            } catch (error) {
                console.error("Error fetching potential roommate:", error);
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        getPotentialRoommate();
    }, []);

    // Set new user info to match screen
    function updateShownUser() {
        // Fetch a new potential roommate
        setRoommate(null); // Clear current roommate while loading new one
        setIsFront(true);
        setIsLoading(true);
        setError(null);

        const getPotentialRoommate = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/matches/getPotentialRoommate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Failed to fetch potential roommate");
                }

                const result = await response.json();
                setRoommate(result); // Store roommate data in state
            } catch (error) {
                console.error("Error fetching potential roommate:", error);
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        getPotentialRoommate();
    }

    const sendMatchData = async (relationship) => {

        try {

            const matchInteraction = JSON.stringify({
                token: localStorage.getItem("token"),
                shown_user: roommate.email,
                relationship: relationship
            })

            const response = await fetch("https://roomie.ddns.net:8080/matches/sendMatchInteraction", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: matchInteraction,
            });

            if (!response.ok) {
                throw new Error("Match Interaction failed. Please try again.");
            }

            alert("Interaction Added"); // will be removed after testing

        } catch (error) {
            console.error("HERE we are", error)
        }
    }

    // Matched chosen!!
    function matched() {
        // TODO: Add logic to store the matched roommate in the database
        sendMatchData(true)
        updateShownUser(); // Load a new potential roommate
    }

    // Declined potential user
    function declined() {
        // TODO: Add logic to remove this user from future match lists

        sendMatchData(false)
        updateShownUser(); // Load a new potential roommate
    }

    function swapSides() {
        console.log("Swapping sides. Current roommate:", roommate); // Debugging: Check if roommate exists
        setIsFront(!isFront); // Toggle isFront state
    }

    function calculateAge() {
        const date = new Date(roommate.date_of_birth)
        console.log("DATE: ", date)

        const today = new Date()

        // Calculate age
        let age = today.getFullYear() - date.getFullYear()

        // Check if birthday has occured this year
        const hasbirthdayPassed = today.getMonth() > date.getMonth() || (today.getMonth() === date.getMonth() && today.getDate() >= date.getDate())

        if(!hasbirthdayPassed) {
            age--
        }

        console.log("AGE: ", age)

        setAge(age)
    }

    return (
        <div className="hold-all">
            {error && (
                <p>Error: {error}</p>
            )}

            {isLoading ? (
                <p>Loading potential roommate...</p>
            ): roommate ? (
                <div onClick={swapSides} className={isFront ? "potential-roomate-front" : "potential-roomate-back"}>
                    {isFront ? (
                        <div className="user_info">
                            <p>{roommate.name}, {roommate.date_of_birth}
                                <br />
                                Bloomsburg University
                            </p>
                        </div>
                    ) : (
                        <div className="more-user-info">
                            <h3>More about {roommate.name}</h3>
                            <dl>
                                <dt>Major</dt>
                                <dd>{roommate.major}</dd>
                                <dt>Bio:</dt>
                                <dd>{roommate.about_me}</dd>
                            </dl>
                        </div>
                    )}
                </div>
            ) : (
                <p>No potential roommate found.</p>
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