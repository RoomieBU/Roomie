import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

function Login() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [loginError, setLoginError] = useState("");

    const onSubmit = async (data) => {
        try {
            const response = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    username: data.email, // Assuming email is used as username
                    password: data.password,
                }),
            });

            if (!response.ok) {
                throw new Error("Invalid login credentials");
            }

            const responseData = await response.json();
            alert("Login successful!");

            // Save token to local storage or context for authentication
            localStorage.setItem("token", responseData.token);
            navigate("/dashboard"); // Redirect after successful login

        } catch (error) {
            setLoginError(error.message);
        }
    };


    return (
        <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
            <h1 className="fw-bold">Welcome to ROOMIE.</h1>
            <p>
                Don’t have an account?{" "}
                <a href="" onClick={() => navigate("/signup")}>
                    Sign up!
                </a>
            </p>
            <form onSubmit={handleSubmit(onSubmit)} className="w-50">
                <div className="mb-3">
                    <label className="form-label">School Email</label>
                    <input
                        type="email"
                        className={`form-control ${errors.email ? "is-invalid" : ""}`}
                        {...register("email", { required: "Email is required" })}
                    />
                    {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                </div>

                <div className="mb-3">
                    <label className="form-label">Password</label>
                    <input
                        type="password"
                        className={`form-control ${errors.password ? "is-invalid" : ""}`}
                        {...register("password", { required: "Password is required" })}
                    />
                    {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
                </div>

                {loginError && <div className="text-danger mb-3">{loginError}</div>}

                <button type="submit" className="btn btn-primary w-100">
                    Sign In
                </button>
            </form>
        </div>
    );
}

export default Login;