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
                    {/* Profile Picture */}
                    <img
                        src={profile.profile_picture_url}
                        alt="Profile"
                        className="profile-picture-circle"
                    />

                    {/* Profile Information */}
                    <h2 className="profile-title">Profile Information</h2>

                    {/* Profile Details Grid */}
                    <div className="profile-details-grid">
                        <div className="detail-item">
                            <span className="detail-label">Name:</span>
                            <span className="detail-value">{profile.name}</span>
                        </div>

                        <div className="detail-item">
                            <span className="detail-label">Email:</span>
                            <span className="detail-value">{profile.email}</span>
                        </div>

                        {/* Add all other fields similarly */}
                        <div className="detail-item">
                            <span className="detail-label">School:</span>
                            <span className="detail-value">{profile.school || "N/A"}</span>
                        </div>

                        <div className="detail-item">
                            <span className="detail-label">Preferred Gender:</span>
                            <span className="detail-value">{profile.preferred_gender}</span>
                        </div>

                        {/* Add Edit Profile Button */}
                        <button
                            className="edit-profile-btn"
                            onClick={() => navigate("/profile/edit")}
                        >
                            Edit Profile
                        </button>
                    </div>
                </div>
            ) : (
                <p>{error || "No profile data available."}</p>
            )}
        </div>
    );
}

export default Profile;