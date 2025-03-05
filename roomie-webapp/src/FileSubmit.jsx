import React, { useState } from "react";

function FileSubmit() {
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [uploadStatus, setUploadStatus] = useState("");

    // Handle file selection & convert to Base64
    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            setSelectedFile(file);
            convertBase64(file).then((base64) => {
                setPreview(base64); // Store Base64 string for upload
            }).catch((error) => {
                console.error("Error converting file:", error);
                setUploadStatus("Failed to process the image.");
            });
        }
    };

    // Convert file to base64 using FileReader
    const convertBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file); // Read file as Base64
            reader.onload = () => resolve(reader.result);
            reader.onerror = (error) => reject(error);
        });
    };

// Send base64 to backend
const handleUpload = async () => {
    if (!preview) {
        setUploadStatus("Please select a file first.");
        return;
    }

    const token = localStorage.getItem("token");

    // Use setPreview to update the state after removing the base64 prefix
    const cleanPreview = preview.split(",")[1]; // Remove base64 prefix
    // Create the payload
    const payload = JSON.stringify({
        token: token,
        fileName: selectedFile.name,
        fileType: selectedFile.type,
        data: cleanPreview,
    });

    // Log the payload to inspect the request
    console.log("Payload being sent to server:", payload);

    try {
        const response = await fetch("http://roomie.ddns.net:8080/upload/fileSubmit", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: payload,
        });

        if (!response.ok) {
            const errorResponse = await response.json(); // Get error details from response
            throw new Error(errorResponse.message || "File upload failed.");
        }

        setUploadStatus("File uploaded successfully!");
    } catch (error) {
        setUploadStatus(`Er ror: ${error.message}`);
    }
};


    return (
        <div>
            <h2>Upload Image</h2>
            <input type="file" accept="image/*" onChange={handleFileChange} />
            {preview && <img src={preview} alt="Preview" style={{ width: "200px", marginTop: "10px" }} />}
            <button onClick={handleUpload}>Upload</button>
            {uploadStatus && <p>{uploadStatus}</p>}
        </div>
    );
}

export default FileSubmit;
