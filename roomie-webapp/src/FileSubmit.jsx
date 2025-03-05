import React, { useState } from "react";

function FileSubmit() {
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [uploadStatus, setUploadStatus] = useState("");

    // Handle file selection
    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            setSelectedFile(file);
            previewFile(file);
        }
    };

    // Convert file to base64
    const previewFile = (file) => {
        const reader = new FileReader();
        reader.readAsDataUrl(file); // Read file as base64
        reader.onloadend = () => {
            setPreview(reader.result); // store base64 string in state
        };
    };

    // Send base64 to backend
    const handleUpload = async () => {
        if (!selectedFile) {
            setUploadStatus("Please select a file first.");
            return;
        }

        const token = localStorage.getItem("token");
        const payload = JSON.stringify({
            token: token,
            fileName: selectedFile.name,
            fileType: selectedFile.type,
            data: preview.split(",")[1],
        });

        try {
            const response = await fetch("http://roomie.ddns.net:8080/upload/fileSubmit", {
                method: "POST",
                headers: {"Content-Type": "application/json" },
            });

            if (!response.ok) {
                throw new Error("File uploaded failed.");
            }

            setUploadStatus("File uploaded successfully!");
        } catch (error) {
            setUploadStatus(error.message);
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