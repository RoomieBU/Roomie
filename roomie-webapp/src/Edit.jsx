import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

function Edit({ onProfile }) {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors }, reset } = useForm();
    const [profileError, setProfileError] = useState("");
    const [selectedProfilePicture, setSelectedProfilePicture] = useState(null);
    const [selectedUserImages, setSelectedUserImages] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

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
            } catch (error) {
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    // Fetch profile data and autofill form
    useEffect(() => {
        const getProfile = async () => {
            setIsLoading(true);
            try {
                const response = await fetch("https://roomie.ddns.net:8080/profile/getProfile", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) throw new Error("Failed to fetch profile");
                const result = await response.json();

                const nameParts = result.name ? result.name.split(' ') : ['', ''];
                const firstName = nameParts[0] || '';
                const lastName = nameParts.slice(1).join(' ') || ''; // Handle multiple last names

                reset({
                    first_name: firstName,
                    last_name: lastName,
                    about_me: result.about_me ? decodeURIComponent(result.about_me) : ''
                });
            } catch (error) {
                setProfileError(error.message);
            } finally {
                setIsLoading(false);
            }
        };
        getProfile();
    }, [reset]);

    // Handle file input change for profile picture
    const handleProfilePictureChange = (event) => {
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

        setSelectedProfilePicture(file);
    };

    // Handle file input change for additional user images
    const handleUserImagesChange = (event) => {
        const files = Array.from(event.target.files);

        if (files.length === 0) return;

        const validTypes = ["image/jpeg", "image/png", "image/webp"];
        for (let file of files) {
            if (!validTypes.includes(file.type)) {
                setProfileError("Unsupported image format.");
                return;
            }
            if (file.size > MAX_FILE_SIZE) {
                setProfileError("Image file is too large. Please upload a smaller image.");
                return;
            }
        }

        setSelectedUserImages(files);
    };

    const onSubmit = async (data) => {
        try {
            // Update profile data
            const profilePayload = JSON.stringify({
                token: localStorage.getItem("token"),
                first_name: data.first_name,
                last_name: data.last_name,
                about_me: encodeURIComponent(data.about_me),
            });

            const profileResponse = await fetch("https://roomie.ddns.net:8080/profile/editProfile", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: profilePayload,
            });

            if (!profileResponse.ok) {
                throw new Error("Profile update failed. Please try again.");
            }

            // Upload profile picture if selected
            if (selectedProfilePicture) {
                const reader = new FileReader();
                reader.readAsDataURL(selectedProfilePicture);
                reader.onload = async () => {
                    let base64Data = reader.result.split(",")[1];
                    base64Data = base64Data.replace(/\s+/g, "");

                    const filePayload = JSON.stringify({
                        token: localStorage.getItem("token"),
                        fileName: selectedProfilePicture.name,
                        fileType: selectedProfilePicture.type,
                        data: base64Data,
                        isProfilePicture: "True",
                    });

                    const fileResponse = await fetch("https://roomie.ddns.net:8080/upload/fileSubmit", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: filePayload,
                    });

                    if (!fileResponse.ok) {
                        throw new Error("Profile picture upload failed.");
                    }
                };
            }

            // Upload user images if selected
            if (selectedUserImages.length > 0) {
                for (let file of selectedUserImages) {
                    const reader = new FileReader();
                    reader.readAsDataURL(file);
                    reader.onload = async () => {
                        let base64Data = reader.result.split(",")[1];
                        base64Data = base64Data.replace(/\s+/g, "");

                        const filePayload = JSON.stringify({
                            token: localStorage.getItem("token"),
                            fileName: file.name,
                            fileType: file.type,
                            data: base64Data,
                            isProfilePicture: "False", // For user images
                        });

                        const fileResponse = await fetch("https://roomie.ddns.net:8080/upload/fileSubmit", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: filePayload,
                        });

                        if (!fileResponse.ok) {
                            throw new Error("User image upload failed.");
                        }
                    };
                }
            }

                navigate("/dashboard");
        } catch (error) {
            setProfileError(error.message);
        }
    };

    const handleCancel = (e) => {
        e.preventDefault();  // Prevent form submission on cancel
        onProfile();  // Call the provided onProfile callback
    };

    if (isLoading) {
        return (
            <div className="container-fluid d-flex align-items-center justify-content-center vh-100">
                <div className="text-center">
                    <p className="fs-4">Loading profile data...</p>
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container-fluid d-flex align-items-center justify-content-center">
            <div className="col-12 col-md-6 col-lg-4 text-center">
                <h1 className="fw-bold mb-4">Edit Your Profile</h1>
                <form onSubmit={handleSubmit(onSubmit)} className="w-100">
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

                    {/* Profile Picture Input */}
                    <div className="mb-3">
                        <label className="form-label">Profile Picture</label>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleProfilePictureChange}
                            className="form-control"
                        />
                    </div>

                    {/* User Images Input */}
                    <div className="mb-3">
                        <label className="form-label">Additional User Images</label>
                        <input
                            type="file"
                            accept="image/*"
                            multiple
                            onChange={handleUserImagesChange}
                            className="form-control"
                        />
                    </div>

                    {profileError && <div className="text-danger mb-3">{profileError}</div>}
                    <div style={{ display: "flex", flexDirection: "row", gap: "20px" }}>
                        <button
                            className="btn btn-secondary w-100 mt-3"
                            onClick={handleCancel}
                        >
                            Cancel
                        </button>
                    </div>
                    <button
                        type="submit"
                        className="btn btn-primary w-100 mt-3"
                    >
                        Save Changes
                    </button>
                </form>
            </div>
        </div>
    );
}

Edit.propTypes = {
    onProfile: PropTypes.func.isRequired,
};

export default Edit;
