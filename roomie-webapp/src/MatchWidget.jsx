
import "./MatchWidget.css"
import { useState } from "react";

function MatchWidget() {

    const [isFront, setIsFront] = useState(true)

    function toggleSide() {
        setIsFront(!isFront)
    }

    return (
        <div onClick={toggleSide} className="match-container">
            <div className={isFront ? "front" : "back"}>

            </div>
        </div>
    )

}

export default MatchWidget;