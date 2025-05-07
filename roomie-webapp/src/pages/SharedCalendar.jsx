import React, { useState, useEffect } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import './SharedCalendar.css';
import RoommateNavBar from '../components/RoommateNavBar';

export default function SharedCalendar() {
  const [events, setEvents] = useState({});
  const today = new Date();

  // 1) On mount: load from backend
  useEffect(() => {
    const loadEvents = async () => {
      try {
        const resp = await fetch('https://roomie.ddns.net:8080/calendar/getEvents', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token: localStorage.getItem('token') })
        });
        if (!resp.ok) throw new Error('Failed to load events');
        const data = await resp.json();
        console.log(data);
        // data: [ { calendarId, groupChatId, eventDate:"YYYY-MM-DD", events:"e1|uid,e2|uid" }, … ]

        const map = {};
        data.forEach(item => {
          const key = new Date(item.eventDate).toDateString();
          console.log(key);
          if (!item.events) return;
          // parse the CSV of “title|userId”
          map[key] = item.events
            .split(',')
            .map(pair => pair.split('|')[0].trim())
            .filter(title => title.length > 0);
        });
        setEvents(map);
      } catch (e) {
        console.error(e);
      }
    };
    loadEvents();
  }, []);

  // 2) When user clicks a day: prompt, update UI, POST to backend
  const handleDayClick = async date => {
    const title = window.prompt('Enter an event for ' + date.toDateString());
    if (!title) return;

    const key = date.toDateString();
    // optimistic UI
    setEvents(prev => ({
      ...prev,
      [key]: [...(prev[key] || []), title]
    }));

    // send to server
    try {
      const resp = await fetch('https://roomie.ddns.net:8080/calendar/addEvent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token: localStorage.getItem('token'),
          eventDate: date.toISOString().slice(0,10),
          event: title
        })
      });
      if (!resp.ok) throw new Error('Server rejected new event');
    } catch (e) {
      console.error(e);
      // rollback UI on failure:
      setEvents(prev => {
        const arr = (prev[key] || []).filter(t => t !== title);
        const copy = { ...prev };
        if (arr.length) copy[key] = arr;
        else delete copy[key];
        return copy;
      });
    }
  };

  const handleDeleteEvent = async (date, title) => {
    const confirmed = window.confirm(`Delete event: "${title}"?`);
    if (!confirmed) return;

    const key = date.toDateString();
    setEvents(prev => {
      const filtered = (prev[key] || []).filter(e => e !== title);
      const copy = { ...prev };
      if (filtered.length) copy[key] = filtered;
      else delete copy[key];
      return copy;
    });

    try {
      const resp = await fetch('https://roomie.ddns.net:8080/calendar/deleteEvent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token: localStorage.getItem('token'),
          eventDate: date.toISOString().slice(0, 10),
          event: title
        })
      });
      if (!resp.ok) throw new Error('Failed to delete event');
    } catch (e) {
      console.error(e);
      // rollback on failure
      setEvents(prev => ({
        ...prev,
        [key]: [...(prev[key] || []), title]
      }));
    }
  };


  // 3) Render events under each tile
const renderTileContent = ({ date, view }) => {
  if (view !== 'month') return null;
  const dayEvents = events[date.toDateString()] || [];
  return (
    <ul className="shared-calendar-event-list">
      {dayEvents.map((evt, i) => (
        <li key={i} onClick={() => handleDeleteEvent(date, evt)} title="Click to delete">
          • {evt}
        </li>
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
        <p>Add things such as class schedules, extracurricular schedules, events at your shared residence, and more!</p>
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
