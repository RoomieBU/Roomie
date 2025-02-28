import "./Matching.css"
import "bootstrap-icons/font/bootstrap-icons.min.css";

import { useState } from "react";

function Matching() {

    // State variables
    const [name, setName] = useState("John");
    const [age, setAge] = useState("19");
    const [university, setUniversity] = useState("Bloomsburg University");
    const [bio, setBio] = useState("This is a bio about the life of John");
    const [major, setMajor] = useState("Computer Science");
    const [isFront, setIsFront] = useState(true); // Controls front/back swap

    // user --> don't know how it is getting sent as
    // const user = "getUserInfo()"
    // JSON Object

    // let name = "John"
    // let age = "19"
    // let university = "Bloomsburg University"

    // let bio = "This is a bio about the life of John"

    // let major = "Computer Science"
    // const housingPreference = "MOA"
    // const sleepingHabits = "early"
    // const interests = "football, video games, and reading"

    // get users from database
    // function getUsers() {

    // }

    // set new user info to match screen
    function updateShownUser() {
        setName("")
        setAge("")
        setUniversity("")
        setBio("")
        setMajor("")
        setIsFront(true)
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