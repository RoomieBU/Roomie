import React, { useState } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import './SharedCalendar.css';
import RoommateNavBar from '../components/RoommateNavBar';

export default function SharedCalendar() {
    const [events, setEvents] = useState({});
    // get current date
    const today = new Date();

    // when a date is clicked prompt user to enter an event through an alert
    const handleDayClick = date => {
        const title = window.prompt('Enter an event for ' + date.toDateString());
        if (!title) return;

        setEvents(prev => {
            // convert date to string
            const key = date.toDateString();
            return {
                ...prev,
                [key]: [...(prev[key] || []), title],
            };
        });
    };

    const renderTileContent = ({ date, view }) => {
        if (view !== 'month') return null;
        const dayEvents = events[date.toDateString()] || [];
        return (
            <ul className="shared-calendar-event-list">
                {dayEvents.map((evt, i) => (
                    <li key={i}>• {evt}</li>
                ))}
            </ul>
        );
    };

    return (
        <div>
            <RoommateNavBar />

            <header className="rating-header">
                <h1>Share a Calendar</h1>
                <h1>with your “ROOMIE.”</h1>
                <p>Add things such as class schedules, extracurricular schedules, events taking place at the shared residence, and more!</p>
            </header>

            <div className="centerAll">
                <div className="card calendar-card mx-auto mt-4">
                    <div className="card-body">
                        <Calendar
                            className="purple-calendar"
                            value={today}
                            onClickDay={handleDayClick}
                            tileContent={renderTileContent}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}