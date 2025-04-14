import "./MatchWidget.css";
import { useState } from "react";
import PropTypes from "prop-types";

function MatchWidget({ firstName, lastName, major, aboutMe, school, dob, picture }) {
    const [isFront, setIsFront] = useState(true);

    function toggleSide() {
        setIsFront(!isFront);
    }

    function calculateAge(dateString) {
        // Parse the date string using the Date constructor
        const birthDate = new Date(dateString);
    
        if (isNaN(birthDate.getTime())) {
            throw new Error("Invalid date format. Expected format: 'MMM D, YYYY' (e.g., 'Aug 7, 2002')");
        }
    
        const currentDate = new Date();
    
        let age = currentDate.getFullYear() - birthDate.getFullYear();
    
        const currentMonth = currentDate.getMonth();
        const birthMonth = birthDate.getMonth();
    
        if (
            currentMonth < birthMonth ||
            (currentMonth === birthMonth && currentDate.getDate() < birthDate.getDate())
        ) {
            age--;
        }
    
        return age;
    }

    return (
        <div onClick={toggleSide} className="match-container">
            <div
                onClick={toggleSide}
                className={isFront ? "front" : "back"}
                style={
                    isFront
                        ? {
                              backgroundImage: `url(${picture ? picture : `https://ui-avatars.com/api/?name=${firstName[0]}&background=random`})`,
                              backgroundSize: "cover",
                              backgroundPosition: "center",
                          }
                        : {}
                }
            >
                {isFront ? (
                    <div className="user_info">
                        <p>
                            {firstName} {lastName}, {calculateAge(dob)}
                            <br />
                            {school}
                        </p>
                    </div>
                ) : (
                    <div className="more-user-info">
                        <h3>More about {firstName}</h3>
                        <dl>
                            <dt>Major</dt>
                            <dd>{major}</dd>
                            <dt>Bio:</dt>
                            <dd>{aboutMe}</dd>
                        </dl>
                    </div>
                )}
            </div>
        </div>
    );
}

MatchWidget.propTypes = {
    firstName: PropTypes.string.isRequired,
    lastName: PropTypes.string.isRequired,
    major: PropTypes.string,
    aboutMe: PropTypes.string,
    school: PropTypes.string,
    dob: PropTypes.number,
    picture: PropTypes.string
};

export default MatchWidget;
