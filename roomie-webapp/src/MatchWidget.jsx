import "./MatchWidget.css";
import { useState } from "react";
import PropTypes from "prop-types";

function MatchWidget({ firstName, lastName, major, aboutMe, school, age, picture }) {
    const [isFront, setIsFront] = useState(true);

    function toggleSide() {
        setIsFront(!isFront);
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
                            {firstName} {lastName}, {age}
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
    age: PropTypes.number,
    picture: PropTypes.string
};

export default MatchWidget;
