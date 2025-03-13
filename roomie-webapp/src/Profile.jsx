import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Profile() {
    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [age, setAge] = useState("-1");
    const navigate = useNavigate();

    // Calculate age from date of birth
    const calculateAge = (birthDate) => {
        if (!birthDate) return "-1";
        const today = new Date();
        const birth = new Date(birthDate);
        let age = today.getFullYear() - birth.getFullYear();
        const monthDiff = today.getMonth() - birth.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
            age--;
        }
        return age.toString();
    };

    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/auth/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) throw new Error("Invalid token");
            } catch (error) {
                navigate("/login");
            }
        };
        verifyToken();
    }, [navigate]);

    useEffect(() => {
        const getProfile = async () => {
            setIsLoading(true);
            try {
                const response = await fetch("https://roomie.ddns.net:8080/profile/getProfile", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) throw new Error("Failed to fetch profile");
                const result = await response.json();

                setProfile(result);
                setAge(calculateAge(result.date_of_birth));
            } catch (error) {
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };
        getProfile();
    }, []);

    return (
        <div className="profile-container">
            {isLoading ? (
                <p>Loading profile...</p>
            ) : profile ? (
                <div className="profile-content">
                    <h2>Profile Information</h2>
                    {/* Profile Picture */}
                    <img
                        src={profile.profile_picture_url}
                        alt="Profile"
                        className="profile-picture-circle"
                    />

                    {/* Profile Details */}
                    <dl className="profile-details">
                        <div className="detail-item">
                            <dt>Name:</dt>
                            <dd>{profile.name}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Email:</dt>
                            <dd>{profile.email}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Age:</dt>
                            <dd>{age !== "-1" ? age : "N/A"}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>School:</dt>
                            <dd>{profile.school || "N/A"}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>About Me:</dt>
                            <dd>{profile.about_me}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Preferred Gender:</dt>
                            <dd>{profile.preferred_gender}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Pet Friendly:</dt>
                            <dd>{profile.pet_friendly === "true" ? "Yes" : "No"}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Introvert:</dt>
                            <dd>{profile.introvert || "N/A"}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Extrovert:</dt>
                            <dd>{profile.extrovert || "N/A"}</dd>
                        </div>

                        <div className="detail-item">
                            <dt>Prefers Quiet Hours:</dt>
                            <dd>{profile.prefer_quiet || "N/A"}</dd>
                        </div>
                    </dl>

                    {/* Edit Profile Button */}
                    <button
                        className="edit-profile-button"
                        onClick={() => window.location.href = "https://roomie.ddns.net/profile/edit"}
                    >
                        Edit Profile
                    </button>
                </div>
            ) : (
                <p>{error || "No profile data available."}</p>
            )}
        </div>
    );
}

export default Profile;