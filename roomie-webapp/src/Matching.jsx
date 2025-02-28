import "./Matching.css"
import "bootstrap-icons/font/bootstrap-icons.min.css";
import { useNavigate } from "react-router-dom";

import { useState, useEffect, useRef } from "react";

function Matching() {

    // State variables
    const [name, setName] = useState("John");
    const [age, setAge] = useState("19");
    const [university, setUniversity] = useState("Bloomsburg University");
    const [bio, setBio] = useState("This is a bio about the life of John");
    const [major, setMajor] = useState("Computer Science");
    const [isFront, setIsFront] = useState(true); // Controls front/back swap

    const navigate = useNavigate();

    // verify access to matching with users
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

                const result = await response.json();
                if (!result.valid) {
                    throw new Error("Invalid token");
                }
            } catch (error) {
                console.log("Redirecting to login due to invalid token.", error);
                navigate("/login");
            }
        };

        verifyToken();
    }, [navigate]);

    const potentialRoomate = useRef(null);

    // get next user
    useEffect(() => {
        const getPotentialRoomate = async () => {
            try {
                const response = await fetch("http://roomie.ddns.net:8080/matches/getPotentialRoomate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ token: localStorage.getItem("token") })
                });

                if (!response.ok) {
                    throw new Error("Invalid token");
                }

                const result = await response.json();
                if (!result.valid) {
                    throw new Error("Invalid token");
                }

                return result;
            } catch (error) {
                console.log("Redirecting to login due to invalid token.", error);
            }
        };

        const fetchRoomate = async () => {
            potentialRoomate.current = await getPotentialRoomate();
        };
        fetchRoomate();
    }, []);



    // set new user info to match screen
    function updateShownUser() {
        setName(potentialRoomate.getItem("name"))
        setAge(potentialRoomate.getItem("date_of_birth"))
        setUniversity("Bloomsburg University") // university??
        setBio(potentialRoomate.getItem("about_me")) // about me
        setMajor(potentialRoomate.getItem("major"))
        setIsFront(true)

        console.log(potentialRoomate)
    }

    // matched chosen!!
    function matched() {
        // send potential roomate to current user's database 

        // ping potential roomate that they have a match??

        // show new user
        updateShownUser()
    }

    // declined potential user
    function declined() {
        // remove user from list of potential roomates that will pop up

        // show new user --> updateShownUser()
        updateShownUser()
    }
    

    function swapSides() {
        setIsFront(!isFront)
    }


    return (
        <div className="hold-all">
             {isFront ? (
                <div onClick={swapSides} style={{display: isFront ? 'block' : 'none'}} className="potential-roomate-front">
                    <div className="user_info">
                        <p>{name}, {age} 
                            <br/>
                            {university}
                        </p>
                    </div>
                </div>
            ) : (
                <div onClick={swapSides} style={{display: isFront ? 'none' : 'block'}} className="potential-roomate-back">
                    <div className="more-user-info">
                        <h3>More about {name}</h3>
                        <dl>
                            <dt>Major</dt>
                            <dd>{major}</dd>
                            <dt>Bio:</dt>
                            <dd>{bio}</dd>
                        </dl>
                    </div>
                </div>
            )}
            
            <div className="match-button-cluster">
                <button onClick={() => declined()} className="deny-icon">
                    <i className="bi bi-x-lg"/>
                </button>
                    
                <button onClick={() => matched()} className="match-icon">
                    <i className="bi bi-check-lg"/>
                </button>
            </div>
        </div>
        
    )
}

export default Matching