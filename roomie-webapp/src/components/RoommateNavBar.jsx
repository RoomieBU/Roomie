import roomieLogo from "../assets/roomie-favicon.svg"
import "./RoommateNavBar.css"

function RoommateNavBar () {

    const handleSignOut = () => {
        localStorage.removeItem("token"); // Remove authentication token
    };

    return (
        <>
        <header className="dashboard-header">
                <div className="logo">
                    <a href="/RoommateManagementDashboard"><img src={roomieLogo} alt="Roomie Logo" /></a>
                </div>
                <div className="nav-links left-lean">
                    <a href="/SharedCalendar">CALENDAR.</a>
                    <a href="/RoommateRating">RATE.</a>
                    <a href="/housingOptions">HOUSING.</a>
                    <a href="/SharedSupply">SUPPLIES.</a>
                    <a href="/RoommateChat">CHAT.</a>
                </div>
                <div className="nav-links">
                    <a href="/profile">EDIT PROFILE</a>
                    <a onClick={handleSignOut} href="/">SIGN OUT.</a>
                </div>
            </header>
        </>
    )
}

export default RoommateNavBar