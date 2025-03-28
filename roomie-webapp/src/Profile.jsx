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
        <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
            {isLoading ? (
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
                    <div style={{ textAlign: "center" }}>
                        <p style={{ fontSize: "20px" }}>Loading profile...</p>
                        <div className="spinner-border" style={{ color: "#007bff" }} role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
            ) : profile ? (
                <div>
                    {/* Profile Picture */}
                    <img
                        src={profile.profile_picture_url || "/default-profile-pic.jpg"} // Fallback image if the URL is missing
                        alt="Profile"
                        style={{
                            width: "150px",
                            height: "150px",
                            borderRadius: "50%",
                            objectFit: "cover",
                            marginBottom: "20px"
                        }}
                    />

                    {/* Profile Information */}
                    <h2 style={{ fontSize: "24px", fontWeight: "bold", marginBottom: "20px" }}>Profile Information</h2>

                    {/* Custom Image Carousel */}
                    {userImages.length > 0 ? (
                        <div style={{ position: "relative", width: "400px", height: "300px", marginBottom: "20px" }}>
                            <button
                                onClick={prevImage}
                                style={{
                                    position: "absolute",
                                    top: "50%",
                                    left: "10px",
                                    transform: "translateY(-50%)",
                                    background: "none",
                                    border: "none",
                                    fontSize: "24px",
                                    color: "#007bff"
                                }}
                            >
                                &#10094; {/* Left arrow */}
                            </button>
                            <img
                                src={userImages[currentIndex]}
                                alt={`User Image ${currentIndex + 1}`}
                                style={{
                                    width: "100%",
                                    height: "100%",
                                    objectFit: "cover",
                                    borderRadius: "8px"
                                }}
                            />
                            <button
                                onClick={nextImage}
                                style={{
                                    position: "absolute",
                                    top: "50%",
                                    right: "10px",
                                    transform: "translateY(-50%)",
                                    background: "none",
                                    border: "none",
                                    fontSize: "24px",
                                    color: "#007bff"
                                }}
                            >
                                &#10095; {/* Right arrow */}
                            </button>
                        </div>
                    ) : (
                        <p>No images available</p>
                    )}

                    {/* Profile Details Grid */}
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "20px" }}>
                        <div>
                            <span style={{ fontWeight: "bold" }}>Name:</span>
                            <span>{profile.name}</span>
                        </div>
                        <div>
                            <span style={{ fontWeight: "bold" }}>Email:</span>
                            <span>{profile.email}</span>
                        </div>
                        <div>
                            <span style={{ fontWeight: "bold" }}>School:</span>
                            <span>{profile.school || "N/A"}</span>
                        </div>
                        <div>
                            <span style={{ fontWeight: "bold" }}>About Me:</span>
                            <span>{decodeURIComponent(profile.about_me)}</span>
                        </div>
                    </div>

                    {/* Add Edit Profile Button */}
                    <button
                        onClick={onEditProfile}
                        style={{
                            padding: "10px 20px",
                            backgroundColor: "#007bff",
                            color: "#fff",
                            border: "none",
                            borderRadius: "5px",
                            cursor: "pointer",
                            fontSize: "16px"
                        }}
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
