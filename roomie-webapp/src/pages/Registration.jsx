import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function Registration() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [registrationError, setRegistrationError] = useState("");
    const [selectedProfilePicture, setSelectedProfilePicture] = useState(null);

    const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    // Resize uploaded image
    const resizeImage = (file, maxWidth, maxHeight) => {
        return new Promise((resolve, reject) => {
            const img = new Image();
            const reader = new FileReader();

            reader.onload = () => {
                img.src = reader.result;
            };

            reader.onerror = reject;
            reader.readAsDataURL(file);

            img.onload = () => {
                const canvas = document.createElement("canvas");
                const ctx = canvas.getContext("2d");

                const aspectRatio = img.width / img.height;

                if (img.width > img.height) {
                    canvas.width = maxWidth;
                    canvas.height = maxWidth / aspectRatio;
                } else {
                    canvas.height = maxHeight;
                    canvas.width = maxHeight * aspectRatio;
                }

                ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
                const resizedDataUrl = canvas.toDataURL(file.type);
                resolve(resizedDataUrl);
            };
        });
    };

    // Handle profile picture selection + resizing
    const handleProfilePictureChange = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const validTypes = ["image/jpeg", "image/png", "image/webp"];
        if (!validTypes.includes(file.type)) {
            setRegistrationError("Unsupported image format.");
            return;
        }

        if (file.size > MAX_FILE_SIZE) {
            setRegistrationError("Image file is too large. Please upload a smaller image.");
            return;
        }

        resizeImage(file, 1800, 2400)
            .then((resizedDataUrl) => {
                setSelectedProfilePicture(resizedDataUrl);
            })
            .catch(() => {
                setRegistrationError("Failed to resize profile picture.");
            });
    };

    const onSubmit = async (data) => {
        try {
            // Step 1: Send registration data
            const profileData = JSON.stringify({
                token: localStorage.getItem("token"),
                first_name: data.first_name,
                last_name: data.last_name,
                about_me: data.about_me,
                date_of_birth: data.date_of_birth,
                major: data.major,
                school: data.school,
                code: data.code
            });

            const response = await fetch("https://roomie.ddns.net:8080/auth/sendRegistration", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: profileData,
            });

            if (!response.ok) {
                throw new Error("Registration failed. Please try again.");
            }

            const result = await response.json();
                if (result.token) {
                    localStorage.setItem("token", result.token);
                }

            // Step 2: Upload profile picture
            if (selectedProfilePicture) {
                const base64Data = selectedProfilePicture.split(",")[1];

                const filePayload = JSON.stringify({
                    token: localStorage.getItem("token"),
                    fileName: "profile-picture",
                    fileType: "image/jpeg",
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
            }

            navigate("/dashboard");
        } catch (error) {
            setRegistrationError(error.message);
        }
    };

    return (
        <div className="manBun">
            <div className="container d-flex flex-column align-items-center justify-content-center">
                <h1 className="fw-bold">Register for ROOMIE</h1>
                <p>
                    Already have an account?{" "}
                    <a href="" onClick={() => navigate("/login")}>
                        Sign in!
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
                        <label className="form-label">Date of Birth</label>
                        <input
                            type="date"
                            className={`form-control ${errors.date_of_birth ? "is-invalid" : ""}`}
                            {...register("date_of_birth", { required: "Date of birth is required" })}
                        />
                        {errors.date_of_birth && <div className="invalid-feedback">{errors.date_of_birth.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Major</label>
                        <input
                            type="text"
                            className={`form-control ${errors.major ? "is-invalid" : ""}`}
                            {...register("major", { required: "Major is required" })}
                        />
                        {errors.major && <div className="invalid-feedback">{errors.major.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="form-label">School</label>
                        <input
                            type="text"
                            className={`form-control ${errors.school ? "is-invalid" : ""}`}
                            {...register("school", { required: "School is required" })}
                        />
                        {errors.school && <div className="invalid-feedback">{errors.school.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Profile Picture</label>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleProfilePictureChange}
                            className="form-control"
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Email Verification Code</label>
                        <input
                            type="text"
                            className={`form-control ${errors.code ? "is-invalid" : ""}`}
                            {...register("code", { required: "Verification code is required. Please check your email." })}
                        />
                        {errors.code && <div className="invalid-feedback">{errors.code.message}</div>}
                    </div>

                    {registrationError && <div className="text-danger mb-3">{registrationError}</div>}

                    <button type="submit" className="btn btn-primary w-100">
                        Register
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Registration;
