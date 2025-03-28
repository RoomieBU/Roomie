import "./Matching.css";
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

function Matching() {
    const [roommate, setRoommate] = useState(null); // Store roommate data
    const [isLoading, setIsLoading] = useState(true); // Loading state
    const [error, setError] = useState(null); // Error state
    const [isFront, setIsFront] = useState(true); // Controls front/back swap
    const [age, setAge] = useState("-1")

    const [isTimeout, setIsTimeout] = useState(false)

    useEffect(() => {
        if (isLoading) {
            const timer = setTimeout(() => setIsTimeout(true), 5000)
            return () => clearTimeout(timer)
        } else {
            setIsTimeout(false)
        }
    }, [isLoading])

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

    // Verify that the user has filled out their preferences (So matching actually works)
    useEffect(() => {
        const verifyPrefs = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/auth/hasPreferences", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("No Preferences");
                }
            } catch (error) {
                console.log("Redirecting to preferences page");
                navigate("/preferences");
            }
        };

        verifyPrefs();
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
                setAge(calculateAge(result.date_of_birth))
            } catch (error) {
                console.error("Error fetching potential roommate:", error);
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        getPotentialRoommate();
    }, []);



    const resetMatchInteractions = async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/matches/resetMatchInteractions", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ token: localStorage.getItem("token") }),
            });

            if (!response.ok) {
                throw new Error("Failed to reset match interactions.");
            }

            console.log("Match interactions reset successfully.");
            setIsTimeout(false); // Reset timeout state
            updateShownUser(); // Try fetching new roommates
        } catch (error) {
            console.error("Error resetting match interactions:", error);
        }
    };


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
                setAge(calculateAge(result.date_of_birth))

                console.log(roommate)

                console.log("This is the profile Picture", roommate.profile_picture)
                console.log(roommate.major)
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
        setIsFront(!isFront); // Toggle isFront state
    }

    function calculateAge(dateString) {
        // Parse the input date string
        const [year, month, day] = dateString.split('-').map(Number);

        // Create a Date object using the parsed values
        const birthDate = new Date(year, month - 1, day); // month is 0-indexed in JS Date

        // Get current date
        const currentDate = new Date();

        // Calculate the difference in years
        let age = currentDate.getFullYear() - birthDate.getFullYear();

        // Check if birthday hasn't occurred yet this year
        const currentMonth = currentDate.getMonth();
        const birthMonth = birthDate.getMonth();

        if (currentMonth < birthMonth ||
            (currentMonth === birthMonth && currentDate.getDate() < birthDate.getDate())) {
            age--;
        }

        return age
    }

    return (
        <div className="hold-all">
            {error && (
                <p>Error: {error}</p>
            )}

            {isLoading ? (
                isTimeout ? (
                    <>
                        <p>No more matches at this time. Please try again later!</p>
                        <button onClick={resetMatchInteractions} className="btn btn-primary">
                            Reset Interactions
                        </button>
                    </>
                ) : (
                    <>
                        <p>Loading potential roommate...</p>
                        <div className="spinner-border text-primary" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </>
                )
            ) : roommate != null ? (
                <>
                    <div
                        onClick={swapSides}
                        className={isFront ? "potential-roomate-front" : "potential-roomate-back"}
                        style={isFront ? { backgroundImage: `url(${roommate.profile_picture})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}}
                    >
                        {isFront ? (
                            <div className="user_info">
                                <p>{roommate.name}, {roommate.age}
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
                                    <dd>{(() => {
                                            try {
                                                return decodeURIComponent(roommate.about_me);
                                            } catch (e) {
                                                return roommate.about_me;
                                            }
                                        })()}</dd>
                                </dl>
                            </div>
                        )}
                    </div>

                    <div className="match-button-cluster">
                        <button onClick={declined} className="deny-icon">
                            <i className="bi bi-x-lg" />
                        </button>

                        <button onClick={matched} className="match-icon">
                            <i className="bi bi-check-lg" />
                        </button>
                    </div>
                </>
            ) : (
                <p>No potential roommate found.</p>
            )}


        </div>
    );
}

export default Matching;