import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
// Utilize React Toastify as a different way to show alerts that is more aesthetically-pleasing for users (and not quite as annoying)
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function Login() {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [loginError, setLoginError] = useState("");

    const onSubmit = async (data) => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: data.email,
                    password: data.password,
                }),
            });

            if (!response.ok) {
                throw new Error("Invalid login credentials");
            }

            const responseData = await response.json();

            // Save token to local storage or context for authentication
            localStorage.setItem("token", responseData.token);


            // Check if user is registered and redirect on response
            const response2 = await fetch("https://roomie.ddns.net:8080/auth/isregistered", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    token: localStorage.getItem("token")
                }),
            });

            // Show "success toast" notification before navigating to next page (Dashboard)
            // toast.success("Login successful!", {
            //     position: toast.POSITION.TOP_RIGHT,
            //     autoClose: 2000,
            // });

            // Delay navigation a bit so the user can see the toast
            setTimeout(() => {
                // If login response is invalid, redirect to registration page
                if (!response2.ok) {
                    navigate("/registration");
                // Else, redirect user to dashboard
                } else {
                    navigate("/dashboard");
                }
            }, 2200);
        // Catch any possible errors and display error message if any login errors occur
        } catch (error) {
            setLoginError(error.message);
        }
    };


    return (
        <div className="manBun">
            <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
                <h1 className="dashboard-h1">Welcome to ROOMIE.</h1>
                <p className="dashboard-p">
                    Don’t have an account?{" "}
                    <a href="" onClick={() => navigate("/signup")}>
                        Sign up!
                    </a>
                </p>
                <form onSubmit={handleSubmit(onSubmit)} className="w-50">
                    <div className="mb-3">
                        <label className="dashboard-p">School Email</label>
                        <input
                            type="email"
                            className={`form-control ${errors.email ? "is-invalid" : ""}`}
                            {...register("email", { required: "Email is required" })}
                        />
                        {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="dashboard-p">Password</label>
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
            {/* <ToastContainer /> */}
        </div>
    );
}

export default Login;