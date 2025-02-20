import React, { useState } from "react";
import "./../stylesheet/signUp.css";

const SignUpForm = () => {
    const [formData, setFormData] = useState({
        email: "",
        password: "",
        confirmPassword: "",
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        alert("Congratulations! Welcome to ROOMIE!");
    };

    return (
        <div className="signup-container">
            <h2>Welcome to ROOMIE.</h2>
            <p>
                Don't have an account? <a href="#">Sign Up!</a>
            </p>

            <form onSubmit={handleSubmit}>
                <label>School Email</label>
                <input
                    type="email"
                    name="email"
                    placeholder="johndoe@gmail.com"
                    value={(formData.email}
                    onChance={handleChange}
                    )}
            </form>
        </div>
    )
}