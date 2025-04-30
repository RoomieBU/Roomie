import React, { useState, useEffect } from "react";
import roomieLogo from "../assets/roomie-favicon.svg"
import "./RoommateNavBar.css"

function RoommateNavBar () {

    const handleSignOut = () => {
        localStorage.removeItem("token"); // Remove authentication token
    };

    const [profilePictureUrl, setProfilePictureUrl] = useState("")

    // Fetch profile picture URL on component mount
        useEffect(() => {
            const fetchProfileData = async () => {
                try {
                    const response = await fetch("https://roomie.ddns.net:8080/profile/getProfile", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ token: localStorage.getItem("token") }),
                    });
    
                    if (!response.ok) throw new Error("Failed to fetch profile");
    
                    const data = await response.json();
                    setProfilePictureUrl(data.profile_picture_url || "https://roomie.ddns.net/images/defaultProfilePic.jpg"); // Use default if no URL is provided
                } catch (error) {
                    console.error("Error fetching profile:", error);
                    setProfilePictureUrl("https://roomie.ddns.net/images/defaultProfilePic.jpg"); // Fallback to default image
                }
            };
    
            fetchProfileData();
        }, []);

    return (
        <>
        <header className="dashboard-header">
                <div className="logo">
                    <a href="/RoommateManagementDashboard"><img src={roomieLogo} alt="Roomie Logo" /></a>
                </div>
                <div className="nav-links left-lean">
                    <a href="/SharedCalendar">CALENDAR.</a>
                    <a href="/RoommateRating">RATE.</a>
                    <a href="/housingOptions">HOUSING.</a>
                    <a href="/SharedSupply">SUPPLIES.</a>
                    <a href="/RoommateChat">CHAT.</a>
                </div>
                <div className="nav-links">
                    <img
                        src={profilePictureUrl}
                        alt="EDIT PROFILE."
                        className="profile-picture"
                        style={{fontFamily: "DM Sans", color: "#6F42C1", fontSize: "17px"}}
                        onClick={() => window.location.href = "/profile"}
                    />
                    {/* <a href="/profile">EDIT PROFILE</a> */}
                    <a onClick={handleSignOut} href="/">SIGN OUT.</a>
                </div>
            </header>
        </>
    )
}

export default RoommateNavBar