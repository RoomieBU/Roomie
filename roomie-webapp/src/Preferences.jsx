import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function Preferences() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();

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
                preferred_gender: data.preferred_gender,
                pet_friendly: data.pet_friendly,
                personality: data.personality,
                wakeup_time: data.wake_up_time,
                sleep_time: data.sleep_time,
                quiet_hours: data.quiet_hours
            });

            const response = await fetch("http://roomie.ddns.net:8080/auth/sendPreferences", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("Sending Preferences failed. Please try again.");
            }

            alert("Sending Preferences successful!");
            navigate("/dashboard");
        } catch (error) {
            setRegistrationError(error.message);
        }
    };

    return (
        <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
            <h1 className="fw-bold">Roommate Preferences</h1>
            <form onSubmit={handleSubmit(onSubmit)} className="w-50">

                {/* Preferred Gender */}
                <div className="mb-3">
                <label className="form-label">Preferred Gender</label>
                <select
                    className={`form-select ${errors.preferred_gender ? "is-invalid" : ""}`}
                    {...register("preferred_gender", { required: "Preferred gender is required" })}
                >
                    <option value="">Select Gender</option>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                    <option value="non-binary">Non-binary</option>
                </select>
                {errors.preferred_gender && (
                    <div className="invalid-feedback">{errors.preferred_gender.message}</div>
                )}
                </div>

                {/* Pet Friendly */}
                <div className="mb-3 form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    id="pet_friendly"
                    {...register("pet_friendly")}
                />
                <label className="form-check-label" htmlFor="petFriendly">
                    Pet Friendly
                </label>
                </div>

                {/* Personality */}
                <div className="mb-3">
                <label className="form-label">Personality</label>
                <textarea
                    className={`form-control ${errors.personality ? "is-invalid" : ""}`}
                    {...register("personality", { required: "Please describe your personality" })}
                />
                {errors.personality && (
                    <div className="invalid-feedback">{errors.personality.message}</div>
                )}
                </div>

                {/* Wake Up Time */}
                <div className="mb-3">
                <label className="form-label">Wake Up Time</label>
                <input
                    type="time"
                    className={`form-control ${errors.wake_up_time ? "is-invalid" : ""}`}
                    {...register("wake_up_time", { required: "Wake up time is required" })}
                />
                {errors.wake_up_time && (
                    <div className="invalid-feedback">{errors.wake_up_time.message}</div>
                )}
                </div>

                {/* Sleep Time */}
                <div className="mb-3">
                <label className="form-label">Sleep Time</label>
                <input
                    type="time"
                    className={`form-control ${errors.sleep_time ? "is-invalid" : ""}`}
                    {...register("sleep_time", { required: "Sleep time is required" })}
                />
                {errors.sleep_time && (
                    <div className="invalid-feedback">{errors.sleep_time.message}</div>
                )}
                </div>

                {/* Quiet Hours */}
                <div className="mb-3">
                <label className="form-label">Quiet Hours</label>
                <input
                    type="text"
                    className={`form-control ${errors.quiet_hours ? "is-invalid" : ""}`}
                    placeholder="e.g., 10pm - 7am"
                    {...register("quiet_hours", { required: "Please specify your quiet hours" })}
                />
                {errors.quiet_hours && (
                    <div className="invalid-feedback">{errors.quiet_hours.message}</div>
                )}
                </div>

                <button type="submit" className="btn btn-primary w-100">
                Submit
                </button>
            </form>
        </div>
    );
}

export default Preferences;
