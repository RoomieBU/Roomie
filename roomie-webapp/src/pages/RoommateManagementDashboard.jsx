import React, { useState, useEffect } from 'react';
import './RoommateManagementDashboard.css';
import RoommateNavBar from '../components/RoommateNavBar';

const RoommateManagementDashboard = () => {
    const [groupchatid, setGroupchat] = useState('');

    const [completeAlerts, setCompleteAlerts] = useState([]);
    const [unresolvedAlerts, setUnresolvedAlerts] = useState([])

    const [toggleAlerts, setToggleAlerts] = useState(true)


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

    // Auto-delete unconfirmed group chats on page load
    useEffect(() => {
        const deleteUnconfirmedChats = async () => {
            const response = await fetch("https://roomie.ddns.net:8080/delete/unconfirmedChats", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") }),
            });

            if (response.ok) {
                console.log('Unconfirmed group chats deleted successfully.');
            } else {
                console.error('Failed to delete unconfirmed group chats.');
            }
        };
        deleteUnconfirmedChats();
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

                    const complete = []
                    const unresolved = []

                    for(const a of data) {
                        if(a.complete) {
                            complete.push(a)
                        } else {
                            unresolved.push(a)
                        }
                    }

                    setCompleteAlerts(complete)
                    setUnresolvedAlerts(unresolved)
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
    

    const updateAlert =  async (alertStatus, alertId) => {
        // alert id
        console.log(alertId)

        try {
            const response = await fetch('https://roomie.ddns.net:8080/alert/updateAlertStatus', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({status: alertStatus, id: alertId})
            })

            if(response.ok) {
                console.log("Alert status updated to: ", alertStatus);
                
                // Find the alert in the appropriate list
                if (alertStatus) { // If setting to complete
                    // Find the alert in unresolved list
                    const alertToMove = unresolvedAlerts.find(alert => alert.id === alertId);
                    if (alertToMove) {
                        // Remove from unresolved list
                        setUnresolvedAlerts(prev => prev.filter(alert => alert.id !== alertId));
                        // Add to complete list with updated status
                        alertToMove.complete = true;
                        setCompleteAlerts(prev => [...prev, alertToMove]);
                    }
                } else { // If setting to unresolved
                    // Find the alert in complete list
                    const alertToMove = completeAlerts.find(alert => alert.id === alertId);
                    if (alertToMove) {
                        // Remove from complete list
                        setCompleteAlerts(prev => prev.filter(alert => alert.id !== alertId));
                        // Add to unresolved list with updated status
                        alertToMove.complete = false;
                        setUnresolvedAlerts(prev => [...prev, alertToMove]);
                    }
                }
            } else {
                console.log("Failed to update alert to: ", alertStatus);
            }   

        } catch (err) {
            console.error(err);
        }
    }

    

    return (
        <div className="dashboard-wrapper">
            <RoommateNavBar/>

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

                <button
                    onClick={() => setToggleAlerts(prevState => !prevState)}
                    className="btn btn-primary alert-button"
                    style={{ marginLeft: '10px' }} // Add margin to the left
                >
                    {toggleAlerts ? 'Check Resolved Alerts' : 'Check Active Alerts'}
                </button>
                    <div className="alert-text">
                        <p>Alert your roommate on important reminders.</p>
                    </div>      
                    <div className="row">
                        {toggleAlerts ? (
                            unresolvedAlerts.length === 0 ? (
                                <p className="text-muted">No resolved alerts at the moment.</p>
                            ) : (
                                unresolvedAlerts.map((alert, index) => (
                                    <div key={index} className="col-md-4 col-sm-6 mb-3">
                                        <div className="card h-100">
                                            <div className="card-body" >
                                                <div style={{display: 'flex', justifyContent: 'space-between'}}>
                                                    <h5 className="card-title">{alert.name}</h5>
                                                    <button onClick={() => updateAlert(true, alert.id)} className="btn btn-light">
                                                        <i className="bi bi-x"/>
                                                    </button>
                                                </div>
                                                <p className="card-text">{alert.description}</p>
                                            </div>
                                        </div>
                                    </div>
                                ))
                            )
                        ) : null}
                    </div>
                    <div className="row">
                        {!toggleAlerts ? (
                            completeAlerts.length === 0 ? (
                                <p className="text-muted">No active alerts at the moment.</p>
                            ) : (
                                completeAlerts.map((alert, index) => (
                                    <div key={index} className="col-md-4 col-sm-6 mb-3">
                                        <div className="card h-100 resolved-alert">
                                            <div className="card-body">
                                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                                    <h5 className="card-title">{alert.name}</h5>
                                                    <button onClick={() => updateAlert(false, alert.id)} className="btn btn-light">
                                                        <i className="bi bi-x"/>
                                                    </button>
                                                </div>
                                                <p className="card-text">{alert.description}</p>
                                            </div>
                                        </div>
                                    </div>
                                ))
                            )
                        ) : null}
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
                        <div className="feature-emoji"><p>✉️</p></div>
                        <a href="/roommateChat">
                            <div className="feature-info">
                                <h3>Chat Room</h3>
                                <p>Start a private conversation with your roommate. Discuss cleaning schedules, grocery lists, and more.</p>
                            </div>
                        </a>
                    </div>

                    <div className="feature-box">
                        <div className="feature-emoji"><p>🗓️</p></div>
                        <a href="/SharedCalendar">
                        <div className="feature-info">
                            <h3>Access Shared Calendar</h3>
                            <p>Share a calendar with your roommates. Input class schedules, events, and more.</p>
                            </div>
                        </a>
                    </div>
                </div>

                <div className="right-section">
                    <a href="/SharedSupply">
                        <div className="right-option">🧹 View Shared Supply List<br /><span>View your shared items, quantities, and last purchase dates.</span></div>
                    </a>
                    <a href="/housingOptions">
                        <div className="right-option">🏠 View Housing Options<br /><span>Checkout your school's various housing options.</span></div>
                    </a>
                    <a href="/RoommateRating">
                        <div className="right-option">⭐ Rate Your Roommate<br /><span>Give your roommate a rating out of 5.</span></div>
                    </a>
                </div>
            </main>
        </div>
    );
};

export default RoommateManagementDashboard;
