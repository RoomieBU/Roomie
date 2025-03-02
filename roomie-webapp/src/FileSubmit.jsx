import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function FileSubmit() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [imageData, setImageData] = useState("");
    const [uploadError, setUploadError] = useState("");

    // Handle file selection & convert to Base64
    const handleFileChange = async (event) => {
        const file = event.target.files[0];

        if (file && (file.type === "image/jpeg" || file.type === "image/png" || file.type === "image/webp")) {
            resizeAndConvertToBase64(file, 800, 600).then(setImageData).catch((error) => {
                console.error("Error processing image:", error);
                alert("Failed to process image.");
            });
        } else {
            alert("Please select a valid image file (JPG, PNG, or WebP).");
        }
    };

    // Resize image using canvas & convert to Base64
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
    const onSubmit = async (data) => {
        if (!imageData) {
            alert("Please select an image before submitting.");
            return;
        }

        const formData = JSON.stringify({
            token: localStorage.getItem("token"),
            image: imageData,
        });

        try {
            const response = await fetch("http://roomie.ddns.net:8080/upload/fileSubmit", {
                method: "POST",
                headers: { "Content-Type": "application/json",
                },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("File upload failed.");
            }

            alert("File uploaded successfully!");
        } catch (error) {
            console.error("Upload error:", error);
            setUploadError("Error uploading file. Please try again.");
        }
    };

    return (
        <div className="container">
            <h2>Upload an Image</h2>
            <form onSubmit={handleSubmit(onSubmit)}>
                <div className="mb-3">
                    <label className="form-label">Choose an image</label>
                    <input
                        type="file"
                        accept="image/jpeg, image/png, image/webp"
                        onChange={handleFileChange}
                        className={`form-control ${errors.file ? "is-invalid" : ""}`}
                        {...register("file", { required: "Please upload an image" })}
                    />
                    {errors.file && <div className="invalid-feedback">{errors.file.message}</div>}
                </div>

                <button type="submit" className="btn btn-primary w-100">
                    Upload
                </button>
            </form>

            {uploadError && <p className="text-danger">{uploadError}</p>}
        </div>
    );
}

export default FileSubmit;
