import React from 'react';
import './About.css';
import SalImg from './assets/SalImage.JPEG';
import EmilyImg from './assets/EmilyFasoImage.JPG';
import MattImg from './assets/MattImage.JPG';
import RileyImg from './assets/RileySimmonsImage.JPG';
import SamImg from './assets/SamKappImage.JPG';


const About = () => {
    const teamMembers = [
        {
            id: 1,
            name: 'Sal La Marca',
            img: SalImg,
            bio: 'TBD' 
        },
        {
            id: 2,
            name: 'Emily Faso',
            img: EmilyImg,
            bio: 'Web developer that dabbles in both front and back-end development. Enjoys exploring the newest UI/UX techniques and prioritizing continous learning.' 
        },
        {
            id: 3,
            name: 'Matthew Yurkunas',
            img: MattImg,
            bio: 'TBD' 
        },
        {
            id: 4,
            name: 'Riley Simmons',
            img: RileyImg,
            bio: 'TBD'
        },
        {
            id: 5,
            name: 'Samuel Kapp',
            img: SamImg,
            bio: 'TBD' 
        }
    ];

    return (
        <div className="about-container">
            <section className="hero-section">
                <div className="hero-content">
                    <h1>About Roomie.</h1>
                </div>
            </section>

            <section className="mission-section">
                <div className="mission-content">
                    <h2>Our Goal:</h2>
                    <p>
                        Roomie is a roommate matching web application designed for college students— by college students. We wanted to create a “Tinder-like” swiping interface using machine-learning based compatibility matching and Java-based integrated messaging to assist in the student roommate selection process. Ultimately hoping to provide and ensure a smooth, informed matching experience.
                    </p>
                </div>
                <div className="arrow-down">
                    <a href="#team-section">↓</a>
                </div>
            </section>

            <section className="team-section">
                <h2>Meet Our Team</h2>
                <p className="team-subtitle">The passionate developers behind the application.</p>

                <div className="team-grid">
                    {teamMembers.map((member) => (
                        <div key={member.id} className="team-card">
                            <div className="card-image">
                                <img src={member.img} alt={member.name} />
                            </div>
                            <h3>{member.name}</h3>
                            <p className="bio">{member.bio}</p>
                            </div>
                    ))}
                </div>
            </section>
        </div>
    );
};

export default About;