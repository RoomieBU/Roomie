import React, { useState, useEffect } from "react";

function TempImageDemo() {
    const [images, setImages] = useState([]);

    useEffect(() => {
        const fetchImages = async () => {
            const token = localStorage.getItem("token");
            const response = await fetch("https://roomie.ddns.net/user/images", {
                method: "GET",
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setImages(data.images?.split(",") || []);
            }
        };

        fetchImages();
    }, []);

    return (
        <div>
            {images.map((url, index) => (
                <img key={index} src={url} alt={`Upload ${index}`} />
            ))}
        </div>
    );
}

export default TempImageDemo;