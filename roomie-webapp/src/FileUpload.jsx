import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function FileSubmit() {
    const navigate = useNavigate();
    const [selectedFile, setSelectedFile] = useState(null);
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
            } catch (error) {
                console.log("Redirecting to login due to invalid token.");
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    // Handle file selection
    const handleFileChange = async (event) => {
        const file = event.target.files[0];

<<<<<<< Updated upstream:roomie-webapp/src/FileUpload.jsx
        if (file && (file.type === "image/jpeg" || file.type === "image/png" || file.type === "image/webp")) {
            resizeAndConvertToBase64(file, 800, 600).then(setImageData).catch((error) => {
                console.error("Error processing image:", error);
                alert("Failed to process image.");
            });
=======
        if (file) {
            console.log("File Selected: ", file);
            if (file.type === "image/jpeg" || file.type === "image/png" || file.type === "image/webp") {
                console.log("Valid Image File Selected:", file.type);
                setSelectedFile(file);
            } else {
                alert("Please select a valid image file (JPG, PNG, WebP).");
                setSelectedFile(null);
            }
>>>>>>> Stashed changes:roomie-webapp/src/FileSubmit.jsx
        } else {
            alert("Please select a valid image file (JPG, PNG, or WebP).");
        }
    };

<<<<<<< Updated upstream:roomie-webapp/src/FileUpload.jsx

    // Resize image using canvas & convert to Base64
    // JSON can't send a binary file over so we need to convert to base64 :(
    const resizeAndConvertToBase64 = (file, maxWidth, maxHeight) => {
        return new Promise((resolve, reject) => {
            const img = new Image();
            img.src = URL.createObjectURL(file);

            img.onload = () => {
                const canvas = document.createElement("canvas");
                const ctx = canvas.getContext("2d");

                canvas.width = maxWidth;
                canvas.height = maxHeight;

                ctx.drawImage(img, 0, 0, maxWidth, maxHeight);

                // Convert resized image to Base64
                const base64String = canvas.toDataURL(file.type);
                resolve(base64String);
            };

            img.onerror = (error) => reject(error);
        });
    };

    // Handle file upload (Send Base64 JSON)
=======
    // Handle file upload (raw binary)
>>>>>>> Stashed changes:roomie-webapp/src/FileSubmit.jsx
    const onSubmit = async (event) => {
        event.preventDefault();

        if (!selectedFile) {
            alert("Please select an image before submitting.");
<<<<<<< Updated upstream:roomie-webapp/src/FileUpload.jsx
            return;
        }

        const formData = JSON.stringify({
            token: localStorage.getItem("token"),
            image: imageData,
        });

        try {
            const response = await fetch("http://roomie.ddns.net:8080/fileUpload", {
=======
            console.log("No file selected.");
            return;
        }

        console.log("Preparing to upload file:", selectedFile);

        try {
            const token = localStorage.getItem("token");
            console.log("Using token for authorization:", token);

            const response = await fetch("http://roomie.ddns.net:8080/upload/fileSubmit", {
>>>>>>> Stashed changes:roomie-webapp/src/FileSubmit.jsx
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,  // Corrected token usage
                    "Content-Type": selectedFile.type // file type (image/jpeg, ...)
                },
                body: selectedFile // raw binary file
            });

            if (!response.ok) {
                throw new Error("File upload failed.");
            }

<<<<<<< Updated upstream:roomie-webapp/src/FileUpload.jsx
            alert("File uploaded successfully!");
=======
            const responseBody = await response.json(); // Assuming the server returns a JSON response
            console.log("File upload response:", responseBody);

            if (responseBody.status === "success") {
                alert("File uploaded successfully!");
                console.log("File upload successful:", responseBody.filename);
            } else {
                alert("Error: " + responseBody.message);
                console.error("Server response error:", responseBody.message);
            }
>>>>>>> Stashed changes:roomie-webapp/src/FileSubmit.jsx
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
