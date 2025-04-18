import "./HousingOptions.css"
import roomieLogo from '../assets/roomie-favicon.svg';

import columbia from '../assets/housingImages/Columbia.jpg'
import elwell from '../assets/housingImages/Elwell.jpg'
import JKA from '../assets/housingImages/JKA.jpg'
import luzerne from '../assets/housingImages/Luzerne.jpg'
import lycoming from '../assets/housingImages/Lycoming.jpg'
import MOA from '../assets/housingImages/MOA.jpg'
import MPA from '../assets/housingImages/MPA.jpg'
import northumberland from '../assets/housingImages/Northumberland.jpg'
import schuylkill from '../assets/housingImages/Schuylkill.jpg'
import soltz from '../assets/housingImages/Soltz.jpg'
import { useEffect, useState } from "react";

function HousingOptions() {
    const dorms = [columbia, elwell, luzerne, lycoming, northumberland, schuylkill]
    const apartments = [JKA, MPA, MOA]

    const [dormIndex, setDormIndex] = useState(0)
    const [apartmentIndex, setApartmentIndex] = useState(0)
    const [fadeOut, setFadeOut] = useState(false)

    useEffect(() => {
        // This function handles the complete fade cycle
        const handleImageCycle = () => {
            // First fade out
            setFadeOut(true);
            
            // Wait for fade out animation to complete before changing image
            setTimeout(() => {
                setDormIndex(prev => (prev + 1) % dorms.length);
                setApartmentIndex(prev => (prev + 1) % apartments.length);
                
                // Then fade back in
                setFadeOut(false);
            }, 300); // 300ms matches the CSS transition time
        };

        // Set up the cycle every 5 seconds
        const interval = setInterval(handleImageCycle, 5000);

        // Clean up on unmount
        return () => clearInterval(interval);
    }, [dorms.length, apartments.length]);

    return (
        <div>
            <header className="dashboard-header">
                <div className="logo">
                    <a href="/"><img src={roomieLogo} alt="Roomie Logo" /></a>
                </div>
                <div className="nav-links">
                    <a href="/Dashboard">EDIT PROFILE</a>
                    <a href="/">SIGN OUT.</a>
                </div>
            </header>
            <header className="rating-header">
                <h1>Housing at Bloomsburg</h1>
                <p>
                    Checkout some of the places Bloomsburg has to offer you and your new Roomie.
                </p>
            </header>
            <div className="centerAll">
                <div className="housing-box">

                    <div className="section">
                        <div className="housing-data-space">
                            <h1 className="housing-name name-left">Traditional Residence Halls</h1>
                            <div className="list-space">
                                <p>

                                </p>
                                <ul className="housing-list">
                                    <li className="housing-item">Columbia Hall</li>
                                    <li className="housing-item">Elwell Hall</li>
                                    <li className="housing-item">Luzerne Hall</li>
                                    <li className="housing-item">Lycoming Hall</li>
                                    <li className="housing-item">Northumberland Hall</li>
                                    <li className="housing-item">Schuylkill Hall</li>
                                </ul>
                            </div>
                            
                        </div>
                        <div className="image-border iright">
                            <div 
                                className={`image-space ${fadeOut ? 'fade-out' : 'fade-in'}`} 
                                style={{ backgroundImage: `url(${dorms[dormIndex]})` }}
                            />
                        </div>
                    </div>

                    <div className="section">
                        <div className="image-border ileft">
                            <div className="image-space" style={{ backgroundImage: `url(${soltz})`}}/>
                        </div>
                        <div className="housing-data-space">
                            <h1 className="housing-name name-right">Suite-Style Residence Hall</h1>
                            <div className="list-space ">
                                <p>

                                </p>
                                <ul className="housing-list">
                                    <li className="housing-item">David L. Soltz Hall</li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <div className="section">
                        <div className="housing-data-space">
                            <h1 className="housing-name name-left">Apartment Complexes</h1>
                            <div className="list-space">
                                <p className="housing-description">
                                    Range in size from 2 to 6-person single bedroom apartments with shared kitchen, living room and bathroom(s).
                                </p>
                                <ul className="housing-list">
                                    <li className="housing-item">Jessica S. Kozloff Apartments</li>
                                    <li className="housing-item" >Montgomery Place Apartments</li>
                                    <li className="housing-item">Mount Olympus Apartments</li>
                                </ul>
                            </div>
                        
                        </div>
                        <div className="image-border iright">
                            <div 
                                className={`image-space ${fadeOut ? 'fade-out' : 'fade-in'}`} 
                                style={{ backgroundImage: `url(${apartments[apartmentIndex]})` }}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default HousingOptions