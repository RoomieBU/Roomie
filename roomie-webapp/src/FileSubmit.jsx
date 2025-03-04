import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function FileSubmit() {
    const navigate = useNavigate();
    const [imageData, setImageData] = useState("");
    const [uploadError, setUploadError] = useState("");

    // Verify that the user is currently logged in and has a valid token
    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("http://roomie.ddns.net:8080/auth/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Invalid token");
                }
                console.log("Token verified successfully.");
            } catch (error) {
                console.log("Redirecting to login due to invalid token.");
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    // Handle file selection & convert to Base64
    const handleFileChange = async (event) => {
        const file = event.target.files[0];

        if (file) {
            console.log("File selected:", file);
            if (file.type === "image/jpeg" || file.type === "image/png" || file.type === "image/webp") {
                console.log("Valid image file.");
                convertToBase64(file).then((base64Image) => {
                    console.log("Image converted to Base64:", base64Image);
                    setImageData(base64Image);
                }).catch((error) => {
                    console.error("Error processing image:", error);
                    alert("Failed to process image.");
                });
            } else {
                alert("Please select a valid image file (JPG, PNG, or WebP).");
            }
        } else {
            console.log("No file selected.");
        }
    };

    // Convert image to Base64 without resizing
    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);

            reader.onload = () => {
                console.log("Base64 string generated.");
                resolve(reader.result);
            };

            reader.onerror = (error) => {
                console.error("FileReader error:", error);
                reject(error);
            };
        });
    };

    // Handle file upload (Send Base64 JSON)
    const onSubmit = async (event) => {
        event.preventDefault();

        if (!imageData) {
            alert("Please select an image before submitting.");
            console.log("No image data to upload.");
            return;
        }

        const formData = JSON.stringify({
            token: localStorage.getItem("token"),
            image: imageData,
        });

        console.log("Form data prepared for upload:", formData);

        try {
            const response = await fetch("http://roomie.ddns.net:8080/upload/fileSubmit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("File upload failed.");
            }

            alert("File uploaded successfully!");
            console.log("File upload successful.");
        } catch (error) {
            console.error("Upload error:", error);
            setUploadError("Error uploading file. Please try again.");
        }
    };

    return (
        <div className="container">
            <h2>Upload an Image</h2>
            <form onSubmit={onSubmit}>
                <input type="file" accept="image/jpeg, image/png, image/webp" onChange={handleFileChange} />
                <button type="submit">Upload</button>
            </form>
            {uploadError && <p className="text-danger">{uploadError}</p>}
        </div>
    );
}

export default FileSubmit;
