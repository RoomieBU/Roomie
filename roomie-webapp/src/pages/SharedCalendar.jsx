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
    console.log('[SharedCalendar] useEffect mount: loading events');
    const loadEvents = async () => {
      try {
        const token = localStorage.getItem('token');
        console.log('[loadEvents] token:', token);
        const resp = await fetch('https://roomie.ddns.net:8080/calendar/getEvents', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ token })
        });
        console.log('[loadEvents] response status:', resp.status);
        if (!resp.ok) throw new Error(`Failed to load events, status ${resp.status}`);
        const data = await resp.json();
        console.log('[loadEvents] data:', data);

        const map = {};
        data.forEach(item => {
          const key = new Date(item.eventDate).toDateString();
          console.log('[loadEvents] processing item:', item, 'key:', key);
          if (!item.events) return;
          map[key] = item.events
            .split(',')
            .map(pair => pair.split('|')[0].trim())
            .filter(title => title.length > 0);
        });
        console.log('[loadEvents] mapped events:', map);
        setEvents(map);
      } catch (e) {
        console.error('[loadEvents] error:', e);
      }
    };
    loadEvents();
  }, []);

  // 2) When user clicks a day: prompt, update UI, POST to backend
  const handleDayClick = async date => {
    console.log('[handleDayClick] clicked date:', date);
    const title = window.prompt('Enter an event for ' + date.toDateString());
    console.log('[handleDayClick] user entered title:', title);
    if (!title) return;

    const key = date.toDateString();
    setEvents(prev => ({
      ...prev,
      [key]: [...(prev[key] || []), title]
    }));
    console.log('[handleDayClick] optimistic UI events:', events);

    try {
      const payload = { token: localStorage.getItem('token'), eventDate: date.toISOString().slice(0,10), event: title };
      console.log('[handleDayClick] sending payload:', payload);
      const resp = await fetch('https://roomie.ddns.net:8080/calendar/addEvent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      console.log('[handleDayClick] server response status:', resp.status);
      if (!resp.ok) throw new Error(`Server rejected new event, status ${resp.status}`);
    } catch (e) {
      console.error('[handleDayClick] error:', e);
      setEvents(prev => {
        const arr = (prev[key] || []).filter(t => t !== title);
        const copy = { ...prev };
        if (arr.length) copy[key] = arr;
        else delete copy[key];
        console.log('[handleDayClick] rolled-back events:', copy);
        return copy;
      });
    }
  };

  // 3) Render events under each tile
  const renderTileContent = ({ date, view }) => {
    if (view !== 'month') return null;
    const dayEvents = events[date.toDateString()] || [];
    console.log('[renderTileContent] date:', date.toDateString(), 'events:', dayEvents);
    return (
      <ul className="shared-calendar-event-list">
        {dayEvents.map((evt, i) => <li key={i}>• {evt}</li>)}
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
