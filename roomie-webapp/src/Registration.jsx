import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import "./Registration.css"; // Import the CSS file

function Registration() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [registrationError, setRegistrationError] = useState("");

    // Verify that the user is currently logged in and has a valid token
    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await fetch("https://roomie.ddns.net:8080/auth/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
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

    const onSubmit = async (data) => {
        try {
            const formData = JSON.stringify({
                token: localStorage.getItem("token"),
                first_name: data.first_name,
                last_name: data.last_name,
                about_me: data.about_me,
                date_of_birth: data.date_of_birth,
                major: data.major,
                school: data.school,
                photo: data.photo,
                code: data.code
            });

            const response = await fetch("https://roomie.ddns.net:8080/auth/sendRegistration", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("Registration failed. Please try again.");
            }

            navigate("/dashboard");
        } catch (error) {
            setRegistrationError(error.message);
        }
    };

    return (
        <div className="registration-container">
            <div className="registration-card">
                <h1 className="registration-title">Register for ROOMIE.</h1>
                <p className="registration-subtext">
                    Already have an account?{" "}
                    <a href="" onClick={() => navigate("/login")} className="registration-link">
                        Sign in!
                    </a>
                </p>
                <form onSubmit={handleSubmit(onSubmit)} className="registration-form">
                    <div className="form-group">
                        <label className="form-label">First Name:</label>
                        <input
                            type="text"
                            className={`form-control ${errors.first_name ? "is-invalid" : ""}`}
                            {...register("first_name", { required: "First name is required." })}
                        />
                        {errors.first_name && <div className="invalid-feedback">{errors.first_name.message}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Last Name:</label>
                        <input
                            type="text"
                            className={`form-control ${errors.last_name ? "is-invalid" : ""}`}
                            {...register("last_name", { required: "Last name is required." })}
                        />
                        {errors.last_name && <div className="invalid-feedback">{errors.last_name.message}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">A Little Bit About Yourself:</label>
                        <textarea
                            className={`form-control ${errors.about_me ? "is-invalid" : ""}`}
                            {...register("about_me", { required: "Please write something about yourself." })}
                        />
                        {errors.about_me && <div className="invalid-feedback">{errors.about_me.message}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Date of Birth:</label>
                        <input
                            type="date"
                            className={`form-control ${errors.date_of_birth ? "is-invalid" : ""}`}
                            {...register("date_of_birth", { required: "Date of birth is required." })}
                        />
                        {errors.date_of_birth && <div className="invalid-feedback">{errors.date_of_birth.message}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Major:</label>
                        <input
                            type="text"
                            className={`form-control ${errors.major ? "is-invalid" : ""}`}
                            {...register("major", { required: "Major is required." })}
                        />
                        {errors.school && <div className="invalid-feedback">{errors.major.message}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">School:</label>
                        <input
                            type="text"
                            className={`form-control ${errors.school ? "is-invalid" : ""}`}
                            {...register("school", { required: "School is required." })}
                        />
                        {errors.school && <div className="invalid-feedback">{errors.school.message}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Upload a Profile Picture:</label>
                        <input
                            type="file"
                            accept="image/*"
                            className={`form-control ${errors.major ? "is-invalid" : ""}`}
                            {...register("photo", { required: "Profile Photo is required." })}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Email Verification Code:</label>
                        <input
                            type="text"
                            className={`form-control ${errors.code ? "is-invalid" : ""}`}
                            {...register("code", { required: "Verification code is required. Please check your email." })}
                        />
                        {errors.code && <div className="invalid-feedback">{errors.code.message}</div>}
                    </div>

                    {registrationError && <div className="registration-error">{registrationError}</div>}

                    <button type="submit" className="registration-button">
                        Register
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Registration;