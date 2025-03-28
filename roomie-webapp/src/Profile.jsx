import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types";
import './profile.css';  // Import the CSS file

function Profile({ onEditProfile }) {
    Profile.propTypes = {
        onEditProfile: PropTypes.func.isRequired,
    };

    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [age, setAge] = useState("-1");
    const [userImages, setUserImages] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const navigate = useNavigate();

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

    useEffect(() => {
        const getUserImages = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/user/images", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) throw new Error("Failed to fetch images");
                const result = await response.json();

                if (result.images) {
                    const imageArray = result.images.split(",");
                    setUserImages(imageArray);
                }
            } catch (error) {
                setError(error.message);
            }
        };
        getUserImages();
    }, []);

    const nextImage = () => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % userImages.length);
    };

    const prevImage = () => {
        setCurrentIndex((prevIndex) => (prevIndex - 1 + userImages.length) % userImages.length);
    };

    return (
        <div className="profile-container">
            {isLoading ? (
                <div className="loading-container">
                    <p>Loading profile...</p>
                    <div className="spinner-border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            ) : profile ? (
                <div className="profile-content">
                    <img
                        src={profile.profile_picture_url || "/default-profile-pic.jpg"}
                        alt="Profile"
                        className="profile-picture"
                    />

                    <h2>Profile Information</h2>

                    {userImages.length > 0 ? (
                        <div className="carousel-container">
                            <button onClick={prevImage} className="carousel-button left">
                                &#10094;
                            </button>
                            <img
                                src={userImages[currentIndex]}
                                alt={`User Image ${currentIndex + 1}`}
                                className="carousel-image"
                            />
                            <button onClick={nextImage} className="carousel-button right">
                                &#10095;
                            </button>
                        </div>
                    ) : (
                        <p>No images available</p>
                    )}

                    <div className="profile-details">
                        <div className="profile-detail-item">
                            <span className="label">Name:</span>
                            <span>{profile.name}</span>
                        </div>
                        <div className="profile-detail-item">
                            <span className="label">Email:</span>
                            <span>{profile.email}</span>
                        </div>
                        <div className="profile-detail-item">
                            <span className="label">School:</span>
                            <span>{profile.school || "N/A"}</span>
                        </div>
                        <div className="profile-detail-item">
                            <span className="label">About Me:</span>
                            <span>{decodeURIComponent(profile.about_me)}</span>
                        </div>
                    </div>

                    <button onClick={onEditProfile} className="edit-profile-button">
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
