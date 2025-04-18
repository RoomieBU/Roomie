import React, { useState, useEffect } from 'react';
import './RoommateManagementDashboard.css';
import roomieLogo from '../assets/roomie-favicon.svg';

const RoommateManagementDashboard = () => {
    const [groupchatid, setGroupchat] = useState('');
    const [alerts, setAlerts] = useState([]);
    const [form, setForm] = useState({
        name: '',
        groupchat_id: '',
        description: '',
        start_time: '',
        end_time: '',
        token: localStorage.getItem("token")
    });

    useEffect(() => {
        const checkIfConfirmed = async () => {
            const response = await fetch("https://roomie.ddns.net:8080/matches/isUserCurrentRoommate", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") }),
            });

            if (response.ok) {
                const data = await response.json();
                setGroupchat(data.groupchatid);
                setForm(prev => ({ ...prev, groupchat_id: data.groupchatid }));
            }
        };
        checkIfConfirmed();
    }, []);

    useEffect(() => {
        import('bootstrap/dist/js/bootstrap.bundle.min.js');
    }, []);


    useEffect(() => {
        if (!groupchatid) return;

        const fetchAlerts = async () => {
            try {
                const response = await fetch('https://roomie.ddns.net:8080/alert/getAllAlerts', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ groupchat_id: groupchatid })
                });

                if (response.ok) {
                    const data = await response.json();
                    setAlerts(data);
                } else {
                    console.error('Failed to fetch alerts');
                }
            } catch (err) {
                console.error('Error fetching alerts:', err);
            }
        };

        fetchAlerts();
    }, [groupchatid]);


    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('https://roomie.ddns.net:8080/alert/addAlert', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form)
            });

            if (res.ok) {
                setForm({ name: '', groupchat_id: groupchatid, description: '', start_time: '', end_time: '', token: localStorage.getItem("token") });

                const modalEl = document.getElementById('addAlertModal');
                const modal = window.bootstrap.Modal.getInstance(modalEl);
                modal.hide();
            } else {
                const errorData = await res.json();
            }
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="dashboard-wrapper">
            <header className="dashboard-header">
                <div className="logo">
                    <a href="/"><img src={roomieLogo} alt="Roomie Logo" /></a>
                </div>
                <div className="nav-links">
                    <a href="/Dashboard">EDIT PROFILE</a>
                    <a href="/">SIGN OUT.</a>
                </div>
            </header>

            <section className="hero-section">
                <h1>ROOMIE.</h1>
                <h2>Roommate Management.</h2>
                <p>Congratulations! It's a match.<br />Manage your shared living space effectively and efficiently.</p>
            </section>

            <section className="alert-section container my-4">
                <div className="container alert-list mt-4 items-center space-y-4">
                    <button
                        type="button"
                        className="btn btn-primary alert-button"
                        data-bs-toggle="modal"
                        data-bs-target="#addAlertModal"
                    >
                        ➕ Send Your Roommate an Alert
                    </button>
                    <div className="alert-text">
                        <p>Alert your roommate on important reminders.</p>
                    </div>
                    <div className="row">
                        {alerts.length === 0 ? (
                            <p className="text-muted">No alerts at the moment.</p>
                        ) : (
                            alerts.map((alert, index) => (
                                <div key={index} className="col-md-4 col-sm-6 mb-3">
                                    <div className="card h-100">
                                        <div className="card-body">
                                            <h5 className="card-title">{alert.name}</h5>
                                            <h6 className="card-subtitle mb-2 text-muted">
                                                {new Date(alert.start_time).toLocaleString()} → {new Date(alert.end_time).toLocaleString()}
                                            </h6>
                                            <p className="card-text">{alert.description}</p>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>


                {/* Modal */}
                <div className="modal fade" id="addAlertModal" tabIndex="-1" aria-labelledby="addAlertModalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="addAlertModalLabel">Create New Alert</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <form onSubmit={handleSubmit}>
                                    <div className="mb-3">
                                        <label className="form-label">Name</label>
                                        <input type="text" className="form-control" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Description</label>
                                        <textarea className="form-control" rows="3" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Start Time</label>
                                        <input type="datetime-local" className="form-control" value={form.start_time} onChange={(e) => setForm({ ...form, start_time: e.target.value })} required />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">End Time</label>
                                        <input type="datetime-local" className="form-control" value={form.end_time} onChange={(e) => setForm({ ...form, end_time: e.target.value })} required />
                                    </div>
                                    <button type="submit" className="btn btn-success">Submit Alert</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <main className="main-content">



                <div className="left-section">
                    <div className="feature-box">
                        <div className="feature-emoji"><p>📲</p></div>
                        <div className="feature-info">
                            <h3>Chat Room</h3>
                            <p>Start a private conversation with your roommate. Discuss cleaning schedules, grocery lists, and more.</p>
                            <div className="dots-row">
                                <span className="dot dot-purple"></span><span>You</span>
                                <span className="dot dot-green"></span><span>Emily</span>
                                <span className="dot dot-yellow"></span><span>Matthew</span>
                            </div>
                        </div>
                    </div>

                    <div className="feature-box">
                        <div className="feature-emoji"><p>🗓️</p></div>
                        <div className="feature-info">
                            <h3>Access Shared Calendar</h3>
                            <p>Share a calendar with your roommates. Input class schedules, events, and more.</p>
                            <div className="dots-row">
                                <span className="dot dot-purple"></span><span>You</span>
                                <span className="dot dot-green"></span><span>Emily</span>
                                <span className="dot dot-yellow"></span><span>Matthew</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="right-section">
                    <a href="/housingOptions">
                        <div className="right-option">🏠 View Housing Options<br /><span>Checkout your school's various housing options.</span></div>
                    </a>
                    <a href="/RoommateRating">
                        <div className="right-option">⭐ Rate Your Roommate<br /><span>Give your roommate a rating out of 5.</span></div>
                    </a>
                    <a href="/RoommateReporting">
                        <div className="right-option">🚨 Report a Roommate Issue<br /><span>Report a roommate issue and assign its priority.</span></div>
                    </a>
                    <div className="right-option">🚩 View Issues<br /><span>View issues submitted by roommates.</span></div>
                </div>
            </main>
        </div>
    );
};

export default RoommateManagementDashboard;
