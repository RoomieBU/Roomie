import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "../App.css";

function SignUp() {
    const navigate = useNavigate();
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm();

    const onSubmit = async (data) => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/auth/register", {
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
                throw new Error("User already exists or registration failed.");
            }

            const responseData = await response.json();

            // Save token to local storage (optional)
            localStorage.setItem("token", responseData.token);

            navigate("/login"); // Redirect to login page after success
        } catch (error) {
            alert(error.message);
        }
    };


    return (
        <div className="manBun">
            <div className="container d-flex flex-column align-items-center vh-100 justify-content-center">
                <h1 className="dashboard-h1">Create An Account with Us!</h1>
                <p className="dashboard-p">
                    & find your dream roommate..
                </p>
                <form onSubmit={handleSubmit(onSubmit)} className="w-50">
                    <div className="mb-3">
                        <label className="dashboard-p">School Email:</label>
                        <input
                            type="email"
                            className={`form-control ${errors.email ? "is-invalid" : ""}`}
                            {...register("email", { required: "Email is required" })}
                        />
                        {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label className="dashboard-p">Password:</label>
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
                    <p className="dashboard-p">
                        Already have an account?{" "}
                        <a href="login" onClick={() => navigate("/login")}>
                            Sign in!
                        </a>
                    </p>
                </form>
            </div>
        </div>
    );
}

export default SignUp;