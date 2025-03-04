import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function Registration() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [registrationError, setRegistrationError] = useState("");

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
                date_of_birth: data.date_of_birth
            });

            const response = await fetch("http://roomie.ddns.net:8080/auth/sendRegistration", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("Registration failed. Please try again.");
            }

            alert("Registration successful!");
            navigate("/dashboard");
        } catch (error) {
            setRegistrationError(error.message);
        }
    };

    return (
        <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
            <h1 className="fw-bold">Let's get some info about you!</h1>
            
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
                    <label className="form-label">Email Verification Code</label>
                    <input
                        type="text"
                        className={`form-control ${errors.code ? "is-invalid" : ""}`}
                        {...register("code", { required: "Verification code is required. Please check your email." })}
                    />
                </div>

                {registrationError && <div className="text-danger mb-3">{registrationError}</div>}

                <button type="submit" className="btn btn-primary w-100">
                    Register
                </button>
            </form>
        </div>
    );
}

export default Registration;
