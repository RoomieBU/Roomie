import React, { useState, useRef, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";

import "./Dashboard.css"

import Matching from "./Matching";

function Dashboard() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token"); // Remove authentication token
        navigate("/login"); // Redirect to login page
    };


    // action area states
    const [hideMatching, setHideMatching] = useState(true)
    const [hideDefault, setHideDefault] = useState(false)


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
        const newLeftWidth = Math.min(Math.max(startLeftWidthRef.current + deltaPercentage, 10), 90);

        setLeftWidth(newLeftWidth);
    }, []);

    const handleMouseUp = useCallback(() => {
        isDraggingRef.current = false;
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
    }, [handleMouseMove]);

    // Handle mouse down on divider
    const handleMouseDown = useCallback((e) => {
        isDraggingRef.current = true;
        startXRef.current = e.clientX;
        startLeftWidthRef.current = leftWidth;
        document.addEventListener('mousemove', handleMouseMove);
        document.addEventListener('mouseup', handleMouseUp);
        e.preventDefault(); // Prevent text selection while dragging
    }, [handleMouseMove, handleMouseUp, leftWidth]);

    // Clean up event listeners on unmount
    useEffect(() => {
        return () => {
            document.removeEventListener('mousemove', handleMouseMove);
            document.removeEventListener('mouseup', handleMouseUp);
        };
    }, [handleMouseMove, handleMouseUp]);

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

                return;
            } catch (error) {
                console.log("Redirecting to login due to invalid token.");
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    function showRelevantComponent(action) {
        setHideMatching(true)
        setHideDefault(true)
        // chat one as well

        switch (action) {
            case "match":
                setHideMatching(false)
                break;
            case "chat":
                // add chat show here
                setHideDefault(false) // this for now
                break;
            default:
                setHideDefault(false)
        }
    }


    return (

        <div>
            <div className="header">
                <div className="action-section">
                    <h4 className="logo">Roomie.</h4>
                    <button className="btn" onClick={() => showRelevantComponent("match")}>Match.</button>
                    <button className="btn" onClick={() => showRelevantComponent("chat")}>Chat.</button>
                    <button className="btn" onClick={handleLogout}>Log Out.</button>
                </div>



                <div className="profile-link" onClick={() => navigate("/profile")} />
            </div>

            <div ref={containerRef} className={`split-panel-container border rounded ${isDraggingRef.current ? 'no-select' : ''}`}>
                {/* Left Panel */}
                <div className="left-panel bg-light p-3" style={{ width: `${leftWidth}%` }}>
                    {/* <h4 className="mb-3">Left Panel</h4>
                    <p>This panel starts at 1/3 width (33.3%)</p>
                    <p className="mt-2">Current width: {leftWidth.toFixed(1)}%</p> */}

                    <div>
                        <h1 className="fw-bold">Welcome to Your Dashboard</h1>
                        <p>This is your landing page after logging in. Profile is in top right</p>
                    </div>

                </div>

                {/* Divider */}
                <div
                    ref={dividerRef}
                    className="divider bg-secondary"
                    onMouseDown={handleMouseDown}
                    onMouseOver={() => dividerRef.current.classList.add('divider-hover')}
                    onMouseOut={() => dividerRef.current.classList.remove('divider-hover')}
                />

                {/* Right Panel */}
                <div className="right-panel bg-white p-3" style={{ width: `${100 - leftWidth - 0.5}%` }}>
                    {/* <h4 className="mb-3">Right Panel</h4>
                    <p>This panel starts at 2/3 width (66.7%)</p>
                    <p className="mt-2">Current width: {(100 - leftWidth - 0.5).toFixed(1)}%</p>
                    
                    <div className="mt-4 p-3 bg-light border rounded">
                    <p className="mb-0">Drag the divider to resize the panels</p>
                    </div> */}

                    {hideDefault ? null : <p>Roomie.</p>}


                    {hideMatching ? null : <Matching />}

                </div>
            </div>
        </div>
    );
}

export default Dashboard;
