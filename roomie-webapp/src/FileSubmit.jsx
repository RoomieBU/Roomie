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
        let base64Data = preview;

        // Create the payload
        const payload = JSON.stringify({
            token: token,
            fileName: selectedFile.name,
            fileType: selectedFile.type,
            data: base64Data,
        });

        console.log("Payload being sent to server:", payload);

        try {
            const response = await fetch("http://roomie.ddns.net:8080/upload/fileSubmit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: payload,
            });

            if (!response.ok) {
                let errorMessage = "File upload failed.";
                try {
                    const errorResponse = await response.json();
                    errorMessage = errorResponse.message || errorMessage;
                    console.error("Server Error Response:", errorResponse);
                } catch (jsonError) {
                    console.error("Failed to parse error response:", jsonError);
                }

                throw new Error(errorMessage);
            }

            setUploadStatus("File uploaded successfully!");
        } catch (error) {
            console.error("Upload request failed:", error);
            console.error("Error name:", error.name);
            console.error("Error stack:", error.stack);
            setUploadStatus(`Error Caught: ${error.message}`);
        } finally {
            console.log("Upload attempt finished at:", new Date().toISOString());
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