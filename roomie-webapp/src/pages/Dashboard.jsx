import { useState, useRef, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";
import Matching from "../components/Matching";
import Chat from "../components/Chat";
import Profile from "./Profile";
import Sidebar from "../components/Sidebar";
import Edit from "./Edit";

import roomieLogo from "../assets/roomie-favicon.svg"

function Dashboard() {
    const navigate = useNavigate();
    const [profilePictureUrl, setProfilePictureUrl] = useState("https://roomie.ddns.net/images/defaultProfilePic.jpg"); // Default profile picture

    // First, check if user is in an accepted roommate group. If so, redirect them to roomie.ddns.net/roommatemanagementdashboard
    useEffect(() => {
        const checkIfConfirmed = async () => {
            const response = await fetch("https://roomie.ddns.net:8080/matches/isUserCurrentRoommate", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") }),
            });

            if (response.ok) { navigate("/RoommateManagementDashboard"); }
        };
        checkIfConfirmed();
    }, [navigate]);


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

    const handleLogout = () => {
        localStorage.removeItem("token"); // Remove authentication token
        navigate("/login"); // Redirect to login page
    };

    // Action area states
    const [hideMatching, setHideMatching] = useState(false);
    const [hideDefault, setHideDefault] = useState(true);
    const [hideChat, setHideChat] = useState(true);
    const [hideProfile, setHideProfile] = useState(true);
    const [hideEdit, setHideEdit] = useState(true);

    const [splitScreen, setSplitScreen] = useState(true);

    // Set chat name
    const [selectedChat, setSelectedChat] = useState(null);

    const [leftWidth, setLeftWidth] = useState(33.3); // Initial left panel width as percentage
    const containerRef = useRef(null);
    const dividerRef = useRef(null);
    const isDraggingRef = useRef(false);
    const startXRef = useRef(0);
    const startLeftWidthRef = useRef(0);

    // Use useCallback to memoize the event handlers
    const handleMouseMove = useCallback((e) => {
        if (!isDraggingRef.current) return;

        const containerWidth = containerRef.current.offsetWidth;
        const deltaX = e.clientX - startXRef.current;
        const deltaPercentage = (deltaX / containerWidth) * 100;

        // Calculate new width with constraints (minimum 10%, maximum 90%)
        const newLeftWidth = Math.min(Math.max(startLeftWidthRef.current + deltaPercentage, 30), 40);

        setLeftWidth(newLeftWidth);
    }, []);

    const handleMouseUp = useCallback(() => {
        isDraggingRef.current = false;
        document.removeEventListener("mousemove", handleMouseMove);
        document.removeEventListener("mouseup", handleMouseUp);
    }, [handleMouseMove]);

    // Handle mouse down on divider
    const handleMouseDown = useCallback((e) => {
        isDraggingRef.current = true;
        startXRef.current = e.clientX;
        startLeftWidthRef.current = leftWidth;
        document.addEventListener("mousemove", handleMouseMove);
        document.addEventListener("mouseup", handleMouseUp);
        e.preventDefault(); // Prevent text selection while dragging
    }, [handleMouseMove, handleMouseUp, leftWidth]);

    // Clean up event listeners on unmount
    useEffect(() => {
        return () => {
            document.removeEventListener("mousemove", handleMouseMove);
            document.removeEventListener("mouseup", handleMouseUp);
        };
    }, [handleMouseMove, handleMouseUp]);

    // Verify that the user is currently logged in and has a valid token
    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/auth/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") }),
                });

                if (!response.ok) {
                    throw new Error("Invalid token");
                }

                return;
            } catch (error) {
                console.log("Redirecting to login due to invalid token.", error);
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    // Add this new state for tracking the current view
    const [currentView, setCurrentView] = useState("Match");

    // Add this handler function
    const handleViewChange = (viewName) => {
        setCurrentView(viewName);
        // This will also handle showing the relevant component based on the view
        showRelevantComponent(viewName.toLowerCase());
    };

    const handleChatSelect = (chat) => {
        setSelectedChat(chat);
    };

    function showRelevantComponent(action) {
        setHideMatching(true);
        setHideDefault(true);
        setHideChat(true);
        setHideProfile(true);
        setHideEdit(true);
        setSplitScreen(true);

        switch (action) {
            case "match":
                setHideMatching(false);
                break;
            case "chat":
                // add chat show here
                setHideChat(false); // this for now
                break;
            case "profile":
                setHideProfile(false);
                setSplitScreen(false);
                break;
            case "edit":
                setSplitScreen(false)
                setHideEdit(false);
                break;
            default:
                setHideDefault(false);
        }
    }

    return (
        <div>

            <header className="dashboard-header">
                <div className="logo">
                    <a><img src={roomieLogo} onClick={() => window.location.reload()} alt="Roomie Logo" /></a>
                </div>
                <div className="nav-links left-lean">
                    <button onClick={() => handleViewChange("Match")}>MATCH.</button>
                    <button onClick={() => handleViewChange("Chat")}>CHAT.</button>
                </div>
                <div className="nav-links">
                    <img
                        src={profilePictureUrl}
                        alt="Profile"
                        className="profile-picture"
                        onClick={() => handleViewChange("Profile")}
                    />
                    <button onClick={handleLogout}>SIGN OUT.</button>
                </div>
            </header>

            <div
                ref={containerRef}
                className={`split-panel-container border rounded ${isDraggingRef.current ? "no-select" : ""}`}
            >


                {splitScreen && (
                <>
                    {/* Left Panel */}
                    <div className="left-panel" style={{ width: `${leftWidth}%` }}>
                        <Sidebar onChatSelect={handleChatSelect}  currentView={currentView} />
                    </div>
                    {/* Divider */}
                    <div
                        ref={dividerRef}
                        className="divider bg-secondary"
                        onMouseDown={handleMouseDown}
                        onMouseOver={() => dividerRef.current.classList.add("divider-hover")}
                        onMouseOut={() => dividerRef.current.classList.remove("divider-hover")}
                    />
                    {/* Right Panel */}
                    <div className="right-panel bg-white p-3" style={{ width: `${100 - leftWidth - 0.5}%` }}>
                        {hideDefault ? null : <p>Roomie.</p>}
                        {hideMatching ? null : <Matching />}
                        {hideChat ? null : <Chat selectedChat={selectedChat}/>}
                    </div>
                </>
                )}

                {!hideProfile && <Profile onEditProfile={() => handleViewChange("Edit")} />}

                {!hideEdit && <Edit onProfile={() => handleViewChange("Profile")} />}


            </div>
        </div>
    );
}

export default Dashboard;