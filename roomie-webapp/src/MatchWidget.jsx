import "./MatchWidget.css";
import { useState } from "react";
import PropTypes from "prop-types";

function MatchWidget({ name, major, aboutMe, school, age }) {
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
                              backgroundImage: `url(https://ui-avatars.com/api/?name=${name[0]}&background=random)`,
                              backgroundSize: "cover",
                              backgroundPosition: "center",
                          }
                        : {}
                }
            >
                {isFront ? (
                    <div className="user_info">
                        <p>
                            {name}, {age}
                            <br />
                            {school}
                        </p>
                    </div>
                ) : (
                    <div className="more-user-info">
                        <h3>More about {name}</h3>
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
    name: PropTypes.string.isRequired,
    major: PropTypes.string,
    aboutMe: PropTypes.string,
    school: PropTypes.string,
    age: PropTypes.number,
};

export default MatchWidget;
