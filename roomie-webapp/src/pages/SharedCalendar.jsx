import React, { useEffect, useState } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';

const SharedCalendar = () => {
  const [events, setEvents] = useState({});
  const [value, setValue] = useState(new Date());

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    const resp = await fetch('https://roomie.ddns.net:8080/calendar/getEvents', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: localStorage.getItem('token') })
    });
    const data = await resp.json();
    const map = {};
    data.forEach(item => {
      const [y, m, d] = item.eventDate.split('-').map(Number);
      const dt = new Date(y, m - 1, d);
      const key = dt.toDateString();
      if (!map[key]) map[key] = [];
      map[key].push(item.event);
    });
    setEvents(map);
  };

  const handleDayClick = async date => {
    const title = prompt('Enter event title:');
    if (!title) return;
    const eventDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    const resp = await fetch('https://roomie.ddns.net:8080/calendar/addEvent', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        token: localStorage.getItem('token'),
        eventDate,
        event: title
      })
    });
    if (resp.ok) {
      const key = date.toDateString();
      setEvents(prev => ({
        ...prev,
        [key]: [...(prev[key] || []), title]
      }));
    }
  };

  const handleDelete = async (date, title) => {
    const eventDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    const confirmed = window.confirm(`Delete “${title}”?`);
    if (!confirmed) return;

    const resp = await fetch('https://roomie.ddns.net:8080/calendar/deleteEvent', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        token: localStorage.getItem('token'),
        eventDate,
        event: title
      })
    });

    if (resp.ok) {
      const key = date.toDateString();
      setEvents(prev => {
        const filtered = (prev[key] || []).filter(e => e !== title);
        const updated = { ...prev };
        if (filtered.length) updated[key] = filtered;
        else delete updated[key];
        return updated;
      });
    } else {
      console.error('Failed to delete event');
    }
  };

  const renderTileContent = ({ date, view }) => {
    if (view !== 'month') return null;
    const key = date.toDateString();
    const dayEvents = events[key] || [];
    return (
      <ul className="shared-calendar-event-list" style={{ paddingLeft: '10px', margin: 0 }}>
        {dayEvents.map((evt, i) => (
          <li key={i} style={{ fontSize: '0.8em' }}>
            • {evt}
            <button
              className="btn btn-sm btn-link text-danger ms-2 p-0"
              onClick={e => {
                e.stopPropagation();
                handleDelete(date, evt);
              }}
            >
              ✕
            </button>
          </li>
        ))}
      </ul>
    );
  };

  return (
    <div className="container mt-4">
      <h2>Shared Calendar</h2>
      <Calendar
        onClickDay={handleDayClick}
        value={value}
        onChange={setValue}
        tileContent={renderTileContent}
      />
    </div>
  );
};

export default SharedCalendar;
