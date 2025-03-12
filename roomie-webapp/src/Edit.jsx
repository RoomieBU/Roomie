import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

function Edit() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [profileError, setProfileError] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);

    // Verify that the user is currently logged in and has a valid token
    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/auth/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") }),
                });

                if (!response.ok) {
                    throw new Error("Invalid token");
                }

                return;
            } catch (error) {
                console.log("Redirecting to login due to invalid token.");
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    // Handle file input change
    const handleFileChange = (event) => {
        const file = event.target.files[0];

        if (!file) return;

        const validTypes = ["image/jpeg", "image/png", "image/webp"];
        if (!validTypes.includes(file.type)) {
            setProfileError("Unsupported image format.");
            return;
        }

        if (file.size > MAX_FILE_SIZE) {
            setProfileError("Image file is too large. Please upload a smaller image.");
            return;
        }

        setSelectedFile(file);
    };

    // Handle form submission (profile update + file upload)
    const onSubmit = async (data) => {
        try {
            // Update profile data
            const profilePayload = JSON.stringify({
                token: localStorage.getItem("token"),
                first_name: data.first_name,
                last_name: data.last_name,
                about_me: data.about_me,
            });

            const profileResponse = await fetch("https://roomie.ddns.net:8080/profile/editProfile", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: profilePayload,
            });

            if (!profileResponse.ok) {
                throw new Error("Profile update failed. Please try again.");
            }

            // Upload profile picture if a file is selected
            if (selectedFile) {
                const reader = new FileReader();
                reader.readAsDataURL(selectedFile);
                reader.onload = async () => {
                    let base64Data = reader.result.split(",")[1];
                    base64Data = base64Data.replace(/\s+/g, "");

                    const filePayload = JSON.stringify({
                        token: localStorage.getItem("token"),
                        fileName: selectedFile.name,
                        fileType: selectedFile.type,
                        data: base64Data,
                        isProfilePic: "True",
                    });

                    const fileResponse = await fetch("https://roomie.ddns.net:8080/upload/fileSubmit", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: filePayload,
                    });

                    if (!fileResponse.ok) {
                        throw new Error("Profile picture upload failed.");
                    }

                    alert("Profile updated successfully!");
                    navigate("/dashboard");
                };
            } else {
                alert("Profile updated successfully!");
                navigate("/dashboard");
            }
        } catch (error) {
            setProfileError(error.message);
        }
    };

    return (
        <div className="manBun">
            <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
                <h1 className="fw-bold">Edit Your Profile</h1>
                <p>
                    Go back to{" "}
                    <a href="" onClick={() => navigate("/dashboard")}>
                        Dashboard
                    </a>
                </p>
                <form onSubmit={handleSubmit(onSubmit)} className="w-50">
                    <div className="mb-3">
                        <label className="form-label">First Name</label>
                        <input
                            type="text"
                            className={`form-control ${errors.first_name ? "is-invalid" : ""}`}
                            {...register("first_name", { required: "First name is required" })}
                        />
                        {errors.first_name && <div className="invalid-feedback">{errors.first_name.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Last Name</label>
                        <input
                            type="text"
                            className={`form-control ${errors.last_name ? "is-invalid" : ""}`}
                            {...register("last_name", { required: "Last name is required" })}
                        />
                        {errors.last_name && <div className="invalid-feedback">{errors.last_name.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="form-label">About Me</label>
                        <textarea
                            className={`form-control ${errors.about_me ? "is-invalid" : ""}`}
                            {...register("about_me", { required: "Please write something about yourself" })}
                        />
                        {errors.about_me && <div className="invalid-feedback">{errors.about_me.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Profile Picture</label>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleFileChange}
                            className="form-control"
                        />
                    </div>

                    {profileError && <div className="text-danger mb-3">{profileError}</div>}

                    <button type="submit" className="btn btn-primary w-100" style={{ position: "fixed", bottom: "0", left: "0", right: "0", borderRadius: "0" }}>
                        Save Changes
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Edit;