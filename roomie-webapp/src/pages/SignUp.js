import React from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./../App.css";

function SignUp() {
    const navigate = useNavigate();
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm();

    const onSubmit = (data) => {
        let users = JSON.parse(localStorage.getItem("users")) || [];

        if (users.some(user => user.email === data.email)) {
            alert("User already exists. Please log in.");
            navigate("/");
            return;
        }

        users.push(data);
        localStorage.setItem("users", JSON.stringify(users));
        alert("Account created successfully!");
        navigate("/");
    };

    return (
        <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
            <h1 className="fw-bold">Create An Account with Us!</h1>
            <p>
                & find your dream roommate..
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
                        {...register("password", { required: "Password is required", minLength: { value: 6, message: "At least 6 characters required" } })}
                    />
                    {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
                </div>

                <button type="submit" className="btn btn-primary w-100">
                    Sign Up
                </button>
                <p>
                    Already have an account?{" "}
                    <a href="#" onClick={() => navigate("/")}>
                        Sign in!
                    </a>
                </p>
            </form>
        </div>
    );
}

export default SignUp;