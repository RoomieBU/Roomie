import "./Matching.css"

function Matching() {

    // user --> don't know how it is getting sent as
    // const user = "getUserInfor()"

    const name = "John"
    const age = "19"
    const university = "Bloomsburg University"
    const major = "Computer Science"
    const housingPreference = "MOA"
    const sleepingHabits = "early"
    const interests = "football, video games, and reading"

    function swapSides() {
        const front = document.getElementById("front")
        const back = document.getElementById("back")

        if(front.style.display === "none") {
            front.style.display = "block"
            back.style.display = "none"
        }
        else {
            front.style.display = "none"
            back.style.display = "block"
        }

    }


    return (
        <div className="hold-all">
            <div id="front" onClick={() => swapSides()} className="potential-roomate-front">
                <div className="user_info">
                    <text>{name}, {age}</text>
                    <br/>
                    <text>{university}</text>
                </div>
            </div> 

            <div id="back" onClick={() => swapSides()} className="potential-roomate-back">
                <div className= "more-user-info">
                    <h3>More about {name}</h3>
                    <dl>
                        <dt>Major:</dt>
                        <dd>{major}</dd>
                        <dt>Housing Preference:</dt>
                        <dd>{housingPreference}</dd>
                        <dt>Sleeping Habits:</dt>
                        <dd>{name} likes to goes to bed {sleepingHabits}.</dd>
                        <dt>Interests:</dt>
                        <dd>{name} likes {interests}</dd>
                    </dl>
                </div>
            </div>
            
            <div className="match-button-cluster">
                <button>
                    X
                </button>
                    
                <button>
                    M
                </button>
            </div>
        </div>
        
    )
}

export default Matching