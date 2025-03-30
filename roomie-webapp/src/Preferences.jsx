import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import "./Preferences.css"

function Preferences() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm({
        defaultValues: {
            gender: "",
            preferred_gender: "",
            pet_friendly: false,
            quiet_hours: "",
            morning_person: 3,
            night_owl: 3,
            introvert: 3,
            extrovert: 3,
            guests_often: 3,
            okay_with_guests: 3,
            prefer_quiet: 3,
            neat_tidy: 3,
            okay_with_mess: 3,
            out_late: 3,
            play_instruments: 3,
            gamer: 3
        }
    });

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
                gender: data.gender,
                preferred_gender: data.preferred_gender,
                pet_friendly: data.pet_friendly,
                personality: data.personality,
                morning_person: data.morning_person,
                night_owl: data.night_owl,
                introvert: data.introvert,
                extrovert: data.extrovert,
                guests_often: data.guests_often,
                okay_with_guests: data.okay_with_guests,
                prefer_quiet: data.prefer_quiet,
                neat_tidy: data.neat_tidy,
                okay_with_mess: data.okay_with_mess,
                out_late: data.out_late,
                play_instruments: data.play_instruments,
                gamer: data.gamer,
                smoke: data.smoke,
                smoke_okay: data.smoke_okay,
                drugs: data.drugs,
                drugs_okay: data.drugs_okay
            });
            console.log("Form Data:", formData);

            const response = await fetch("https://roomie.ddns.net:8080/auth/sendPreferences", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("Sending Preferences failed. Please try again.");
            }

            navigate("/dashboard");
        } catch (error) {
            setRegistrationError(error.message);
        }
    };

    // className="container d-flex flex-column align-items-center vh-100 justify-content-center"

    return (
        <div className="riley">
            <h1 className="fw-bold">Roommate Preferences</h1>
            <p className="fw-bold">Enter your preferences. For the slider, the left is for "strongly disagree", and the right is "strongly agree".</p>
            <p className="fw-bold">This information is used to help you find the best roommate, and it not shared with other users.</p>
            <form onSubmit={handleSubmit(onSubmit)} className="something">

                <div className="holdStuff">
                    <div className="genderHolder">
                        <div className="genderChoice">
                            <label className="form-label">Your gender</label>
                            <select
                                className={`form-select ${errors.gender ? "is-invalid" : ""}`}
                                {...register("gender", { required: "Preferred gender is required" })}
                            >
                                <option value="">Select Gender</option>
                                <option value="male">Male</option>
                                <option value="female">Female</option>
                                <option value="non-binary">Non-binary</option>
                            </select>
                            {errors.gender && (
                                <div className="invalid-feedback">{errors.gender.message}</div>
                            )}
                        </div>

                        <div className="genderChoice">
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
                    </div>
                </div>

                <div className="holdStuff">
                    <div className="column">
                        {["morning_person", "night_owl", "introvert", "extrovert", "guests_often", "okay_with_guests"].map((field) => (
                            <div className="mb-3" key={field}>
                                <label className="form-label">{field.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase())}</label>
                                <input type="range" className="form-range" min="1" max="5" {...register(field)} />
                            </div>
                        ))}
                    </div>

                    <div className="column">
                        {["prefer_quiet", "neat_tidy", "okay_with_mess", "out_late", "play_instruments", "gamer"].map((field) => (
                            <div className="mb-3" key={field}>
                                <label className="form-label">{field.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase())}</label>
                                <input type="range" className="form-range" min="1" max="5" {...register(field)} />
                            </div>
                        ))}
                    </div>

                    <div className="column">
                        <div className="mb-3 form-check">
                            <input type="checkbox" className="form-check-input" id="pet_friendly" {...register("pet_friendly")} />
                            <label className="form-check-label" htmlFor="petFriendly">Okay with pets?</label>
                        </div>

                        <div className="mb-3 form-check">
                            <input type="checkbox" className="form-check-input" id="smoke" {...register("smoke")} />
                            <label className="form-check-label" htmlFor="smoke">Do you smoke?</label>
                        </div>

                        <div className="mb-3 form-check">
                            <input type="checkbox" className="form-check-input" id="smoke_okay" {...register("smoke_okay")} />
                            <label className="form-check-label" htmlFor="smoke_okay">Okay with smoke?</label>
                        </div>

                        <div className="mb-3 form-check">
                            <input type="checkbox" className="form-check-input" id="drugs" {...register("drugs")} />
                            <label className="form-check-label" htmlFor="drugs">Do you do drugs or drink alcohol?</label>
                        </div>

                        <div className="mb-3 form-check">
                            <input type="checkbox" className="form-check-input" id="drugs_okay" {...register("drugs_okay")} />
                            <label className="form-check-label" htmlFor="drugs_okay">Okay with drugs and alcohol?</label>
                        </div>
                    </div>

                </div>

                <button type="submit" className="btn btn-primary w-100">Submit</button>
            </form >
        </div >
    );
}

export default Preferences;
