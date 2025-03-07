import React, { useState, useEffect } from "react";

function TempImageDemo() {
    const [images, setImages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchImages = async () => {
            console.log("[DEBUG] Starting image fetch");
            try {
                // Get token
                const token = localStorage.getItem("token");
                console.log("[DEBUG] Retrieved token from localStorage:", token ? "exists" : "missing");

                if (!token) {
                    console.warn("[DEBUG] No token found, setting error");
                    setError("You must be logged in to view images.");
                    return;
                }

                // API call
                console.log("[DEBUG] Starting fetch request");
                const controller = new AbortController();
                const timeoutId = setTimeout(() => {
                    controller.abort();
                    console.error("[DEBUG] Fetch timed out after 10 seconds");
                    setError("Request timed out. Please try again.");
                }, 10000);

                const response = await fetch("https://roomie.ddns.net/user/images", {
                    method: "GET",
                    headers: { "Authorization": `Bearer ${token}` }, // Added Bearer prefix
                    signal: controller.signal
                });
                clearTimeout(timeoutId);

                // Response handling
                console.log("[DEBUG] Received response, status:", response.status);
                console.log("[DEBUG] Response headers:", JSON.stringify([...response.headers.entries()]));

                if (!response.ok) {
                    const errorText = await response.text();
                    console.error("[DEBUG] Non-OK response. Status:", response.status, "Content:", errorText);
                    throw new Error(`HTTP ${response.status}: ${errorText || "Unknown error"}`);
                }

                // Data parsing
                const rawData = await response.text();
                console.log("[DEBUG] Raw response data:", rawData);

                try {
                    const data = JSON.parse(rawData);
                    console.log("[DEBUG] Parsed JSON data:", data);

                    if (!data.images) {
                        console.warn("[DEBUG] 'images' field missing in response");
                        setError("No images found in response");
                        return;
                    }

                    const imageUrls = data.images.split(",");
                    console.log("[DEBUG] Extracted image URLs:", imageUrls);
                    setImages(imageUrls);

                } catch (parseError) {
                    console.error("[DEBUG] JSON parsing failed:", parseError);
                    throw new Error("Invalid response format from server");
                }

            } catch (err) {
                console.error("[DEBUG] Error in fetchImages:", err);
                setError(err.message || "An error occurred while fetching images.");

            } finally {
                console.log("[DEBUG] Cleaning up, setting loading to false");
                setLoading(false);
            }
        };

        fetchImages();

        return () => {
            console.log("[DEBUG] Component unmounted, cleanup");
        };
    }, []);

    // Render states
    console.log("[DEBUG] Render state - loading:", loading, "error:", error, "images:", images);

    if (loading) {
        return <p>Loading images...</p>;
    }

    if (error) {
        return (
            <div className="error-container">
                <p>Error: {error}</p>
                <button onClick={() => window.location.reload()}>Retry</button>
            </div>
        );
    }

    return (
        <div>
            <h1>Your Uploaded Images</h1>
            {images.length === 0 ? (
                <p>No images found in your account.</p>
            ) : (
                <div className="image-grid">
                    {images.map((url, index) => (
                        <div key={index} className="image-item">
                            <img
                                src={url}
                                alt={`Upload ${index + 1}`}
                                onError={(e) => {
                                    console.warn("[DEBUG] Image failed to load:", url);
                                    e.target.src = "/image-error.jpg";
                                }}
                                onLoad={() => console.log("[DEBUG] Image loaded successfully:", url)}
                            />
                            <div className="image-url">Image URL: {url}</div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default TempImageDemo;