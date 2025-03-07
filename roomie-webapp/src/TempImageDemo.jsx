import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function TempImageDemo() {
    const [images, setImages] = useState([]);
    const [isLoading, setIsLoading] = useState(true); // Loading state
    const [error, setError] = useState(null); // Error state
    const navigate = useNavigate();

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

    // Fetch the user's images
    useEffect(() => {
        const fetchImages = async () => {
            setIsLoading(true);
            setError(null);
            try {
                const response = await fetch("https://roomie.ddns.net:8080/user/images", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Failed to fetch images");
                }

                const result = await response.json();
                if (result.images) {
                    setImages(result.images.split(","));
                }
            } catch (error) {
                console.error("Error fetching images:", error);
                setError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchImages();
    }, []);

    return (
        <div>
            {isLoading ? (
                <p>Loading images...</p>
            ) : error ? (
                <p>Error: {error}</p>
            ) : images.length > 0 ? (
                <div className="image-container">
                    {images.map((url, index) => (
                        <img key={index} src={url} alt={`Upload ${index}`} />
                    ))}
                </div>
            ) : (
                <p>No images available.</p>
            )}
        </div>
    );
}

export default TempImageDemo;
