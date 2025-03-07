import React, { useState } from "react";

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

function FileSubmit({ isProfilePic = false }) {
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [uploadStatus, setUploadStatus] = useState("");

    const handleFileChange = (event) => {
        const file = event.target.files[0];

        if (!file) return;

        const validTypes = ["image/jpeg", "image/png", "image/webp"];
        if (!validTypes.includes(file.type)) {
            setUploadStatus("Unsupported image format.");
            return;
        }

        if (file.size > MAX_FILE_SIZE) {
            setUploadStatus("Image file is too large. Please upload a smaller image.");
            return;
        }

        setSelectedFile(file);
        resizeImage(file)
            .then((base64) => setPreview(base64))
            .catch((error) => {
                setUploadStatus("Failed to process the image.");
            });
    };

    // Resize image to the desired size
    const resizeImage = (file) => {
        return new Promise((resolve, reject) => {
            const img = new Image();
            const reader = new FileReader();

            reader.onload = (e) => {
                img.src = e.target.result;
            };
            reader.onerror = reject;

            reader.readAsDataURL(file);

            img.onload = () => {
                const canvas = document.createElement("canvas");
                const ctx = canvas.getContext("2d");

                let width = img.width;
                let height = img.height;

                // Resize based on whether it's a profile picture or not
                if (isProfilePic) {
                    // Profile picture: Resize to 150x150
                    width = 150;
                    height = 150;
                } else {
                    // Regular image: Resize for phone screens, max width of 1080px
                    const maxWidth = 1080;
                    if (width > maxWidth) {
                        height = (maxWidth / width) * height;
                        width = maxWidth;
                    }
                }

                // Set canvas dimensions to resized image dimensions
                canvas.width = width;
                canvas.height = height;

                // Draw the resized image on the canvas
                ctx.drawImage(img, 0, 0, width, height);

                // Convert canvas to base64
                const base64 = canvas.toDataURL(file.type);
                resolve(base64);
            };
        });
    };

    const handleUpload = async () => {
        if (!preview) {
            setUploadStatus("Please select a file first.");
            return;
        }

        const token = localStorage.getItem("token");
        let base64Data = preview.split(",")[1];

        // Clean the base64 data by removing any newlines or spaces
        base64Data = base64Data.replace(/\s+/g, "");

        const payload = JSON.stringify({
            token: token,
            fileName: selectedFile.name,
            fileType: selectedFile.type,
            data: base64Data,
            isProfilePic: isProfilePic.toString(),
        });

        try {
            const response = await fetch("https://roomie.ddns.net:8080/upload/fileSubmit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: payload,
            });

            const responseBody = await response.json();

            if (!response.ok) {
                throw new Error(responseBody.message || "File upload failed.");
            }

            setUploadStatus("File uploaded successfully!");
        } catch (error) {
            setUploadStatus(`Upload failed: ${error.message}`);
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
