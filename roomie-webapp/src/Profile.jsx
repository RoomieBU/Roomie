import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";


function Profile() {
    const [profile, setProfile] = useState(null); // Store profile data
    const [isLoading, setIsLoading] = useState(true); // Loading state
    const [error, setError] = useState(null); // Error state
    const [age, setAge] = useState("-1")

    const navigate = useNavigate();

    // const profile = {
    //     email: "john.doe@example.com",
    //     first_name: "John",
    //     last_name: "Doe",
    //     about_me: "I am a software engineering student who loves hiking, gaming, and coffee.",
    //     date_of_birth: "1998-06-15",
    //     preferred_gender: "Any",
    //     pet_friendly: true,
    //     personality: "Outgoing and friendly",
    //     quiet_hours: "10 PM - 7 AM"
    // };

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

    // Fetch the users profile info
    useEffect(() => {
        const getProfile = async () => {
            setIsLoading(true);
            setError(null);
            try {
                const response = await fetch("https://roomie.ddns.net:8080/profile/getProfile", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Failed to fetch profile");
                }

                const result = await response.json();
                setProfile(result); // Store profile data in state
                setAge(calculateAge(result.date_of_birth))
            } catch (error) {
                console.error("Error fetching profile:", error);
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        getProfile();
    }, []);


    return (
        <div>
            {isLoading ? (
                <p>Loading profile...</p>
            ) : profile ? (
                <div className="profile-container">
                    <h2>Profile Information</h2>
                    <dl className="profile-details">
                        <dt>Email:</dt>
                        <dd>{profile.email}</dd>
    
                        <dt>First Name:</dt>
                        <dd>{profile.name}</dd>
    
                        <dt>About Me:</dt>
                        <dd>{profile.about_me}</dd>
    
                        <dt>Date of Birth:</dt>
                        <dd>{profile.date_of_birth}</dd>
    
                        <dt>Preferred Gender:</dt>
                        <dd>{profile.preferred_gender}</dd>
    
                        <dt>Pet Friendly:</dt>
                        <dd>{profile.pet_friendly ? "Yes" : "No"}</dd>
    
                        <dt>Personality:</dt>
                        <dd>{profile.personality}</dd>
    
                        <dt>Quiet Hours:</dt>
                        <dd>{profile.quiet_hours}</dd>
                    </dl>
                </div>
            ) : (
                <p>No profile data available.</p>
            )}
        </div>
    );
}

export default Profile;
