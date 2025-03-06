import React, { useState, useEffect } from "react";

function TempImageDemo() {
    const [images, setImages] = useState([]); // Stores the list of image URLs
    const [loading, setLoading] = useState(true); // Loading state
    const [error, setError] = useState(""); // Error message

    // Fetch images from the backend
    useEffect(() => {
        const fetchImages = async () => {
            try {
                const token = localStorage.getItem("token");
                if (!token) {
                    setError("You must be logged in to view images.");
                    return;
                }

                // Fetch image URLs from the backend
                const response = await fetch("https://roomie.ddns.net:8080/user/images", {
                    method: "GET",
                    headers: {
                        "Authorization": token,
                    },
                });

                if (!response.ok) {
                    throw new Error("Failed to fetch images. Please try again later.");
                }

                const data = await response.json();

                // Check if the response contains image URLs
                if (data.images) {
                    // Split the comma-separated string into an array of URLs
                    const imageUrls = data.images.split(",");
                    setImages(imageUrls);
                } else {
                    setError("No images found.");
                }
            } catch (err) {
                setError(err.message || "An error occurred while fetching images.");
            } finally {
                setLoading(false); // Stop loading
            }
        };

        fetchImages();
    }, []); // Run only once on component mount

    // Display loading state
    if (loading) {
        return <p>Loading images...</p>;
    }

    // Display error message
    if (error) {
        return <p>{error}</p>;
    }

    // Display images in a simple list
    return (
        <div>
            <h1>Your Uploaded Images</h1>
            <div>
                {images.map((url, index) => (
                    <div key={index}>
                        <img
                            src={url} // Use the URL directly in the image tag
                            alt={`Upload ${index + 1}`}
                            onError={(e) => {
                                // Fallback if the image fails to load
                                e.target.src = "/image-error.jpg";
                            }}
                        />
                    </div>
                ))}
            </div>
        </div>
    );
}

export default TempImageDemo;