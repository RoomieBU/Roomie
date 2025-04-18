import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types";
import './profile.css';  // Import the CSS file
import Spinner from "../components/Spinner"

function Profile({ onEditProfile }) {
    Profile.propTypes = {
        onEditProfile: PropTypes.func.isRequired,
    };

    const [profile, setProfile] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [userImages, setUserImages] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const navigate = useNavigate();


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
            } catch (error) {
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };
        getProfile();
    }, []);

    // Fetch user images
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

    const onDeleteImage = async () => {
        const token = localStorage.getItem("token");
        const file_url = userImages[currentIndex];

        try {
            const response = await fetch("https://roomie.ddns.net:8080/user/deleteImage", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    token: token,
                    file_url: file_url
                })
            });

            const result = await response.json();
            if (response.ok) {
                // remove deleted image from the array
                const updatedImages = userImages.filter((_, idx) => idx !== currentIndex);
                setUserImages(updatedImages);
                setCurrentIndex((prev) => Math.min(prev, updatedImages.length - 1));
                alert("Image deleted successfully.");
            } else {
                alert(result.message || "Failed to delete image.");
            }
        } catch (error) {
            console.error("Delete failed: ", error);
            alert("An error occurred while deleting the image.");
        }
    }

    return (
        <div className="profile-container">
            {isLoading ? (
                <div className="loading-container">
                    <Spinner load={"profile..."}/>
                </div>
            ) : profile ? (
                <div className="profile-content">
                    {/* Profile Picture */}
                    <img
                        src={profile.profile_picture_url || "/default-profile-pic.jpg"}
                        alt="Profile"
                        className="profile-picture-page"
                    />

                    <h2 className="profile-heading">Profile Information</h2>

                    {/* Image Carousel */}
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

                    {/* Edit Profile Button */}
                    <button className="delete-image-btn" onClick={onDeleteImage}>
                        Delete
                    </button>

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
                            <span className="detail-value">
                                {(() => {
                                    try {
                                        return decodeURIComponent(profile.about_me);
                                    } catch (e) {
                                        return profile.about_me || "No bio available.";  // Fallback to the raw string or "No bio available" if decoding fails
                                    }
                                })()}
                            </span>
                        </div>


                        {/* Edit Profile Button */}
                        <button className="edit-profile-btn" onClick={onEditProfile}>
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
