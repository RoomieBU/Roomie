import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types";

function Profile({ onEditProfile }) {
    Profile.propTypes = {
        onEditProfile: PropTypes.func.isRequired,
    };

    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [age, setAge] = useState("-1");
    const [userImages, setUserImages] = useState([]); // State to store user images
    const [currentIndex, setCurrentIndex] = useState(0); // State to keep track of the current image index
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

    // Fetch profile information
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

    // Fetch images for the user
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

    // Function to go to the next image
    const nextImage = () => {
        setCurrentIndex((prevIndex) => {
            const nextIndex = (prevIndex + 1) % userImages.length;
            return nextIndex;
        });
    };

    // Function to go to the previous image
    const prevImage = () => {
        setCurrentIndex((prevIndex) => {
            const prevIndexUpdated = (prevIndex - 1 + userImages.length) % userImages.length;
            return prevIndexUpdated;
        });
    };

    return (
        <div className="profile-container">
            {isLoading ? (
                <div className="container-fluid d-flex align-items-center justify-content-center vh-100">
                    <div className="text-center">
                        <p className="fs-4">Loading profile...</p>
                        <div className="spinner-border text-primary" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
            ) : profile ? (
                <div className="profile-content">
                    {/* Profile Picture */}
                    <img
                        src={profile.profile_picture_url}
                        alt="Profile"
                        className="profile-picture-circle"
                        style={{ width: '120px', height: '120px', objectFit: 'cover' }} // Smaller profile picture
                    />

                    {/* Profile Information */}
                    <h2 className="profile-title">Profile Information</h2>

                    {/* Custom Image Carousel */}
                    {userImages.length > 0 ? (
                        <div className="custom-carousel">
                            <button onClick={prevImage} className="carousel-btn prev-btn">
                                &#10094; {/* Left arrow */}
                            </button>
                            <img
                                className="carousel-image"
                                src={userImages[currentIndex]}
                                alt={`User Image ${currentIndex + 1}`}
                                style={{ width: '300px', height: '200px', objectFit: 'cover' }} // Resized user images
                            />
                            <button onClick={nextImage} className="carousel-btn next-btn">
                                &#10095; {/* Right arrow */}
                            </button>
                        </div>

                    ) : (
                        <p>No images available</p>
                    )}

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

                        <div className="detail-item">
                            <span className="detail-label">School:</span>
                            <span className="detail-value">{profile.school || "N/A"}</span>
                        </div>

                        <div className="detail-item">
                            <span className="detail-label">About Me:</span>
                            <span className="detail-value">{decodeURIComponent(profile.about_me)}</span>
                        </div>

                        {/* Add Edit Profile Button */}
                        <button
                            className="edit-profile-btn"
                            onClick={onEditProfile}
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
