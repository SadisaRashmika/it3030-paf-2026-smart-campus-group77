import React, { useState } from 'react';
import BookingForm from '../member2-bookings-management/components/BookingForm';
import MyBookingsPage from '../member2-bookings-management/pages/MyBookingsPage';
import AdminBookingDashboard from '../member2-bookings-management/pages/AdminBookingDashboard';
import './App.css';

function App() {
  const [currentView, setCurrentView] = useState('form');

  return (
    <div className="app-container">
      <nav className="nav-bar">
        <div className="nav-brand">M2: Bookings</div>
        <div className="nav-links">
          <button 
            className={`nav-btn ${currentView === 'form' ? 'active' : ''}`}
            onClick={() => setCurrentView('form')}
          >
            New Booking
          </button>
          <button 
            className={`nav-btn ${currentView === 'my-bookings' ? 'active' : ''}`}
            onClick={() => setCurrentView('my-bookings')}
          >
            My Bookings (User)
          </button>
          <button 
            className={`nav-btn ${currentView === 'admin' ? 'active' : ''}`}
            onClick={() => setCurrentView('admin')}
          >
            Admin Dashboard
          </button>
        </div>
      </nav>

      <main className="main-content">
        {currentView === 'form' && (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <p style={{ textAlign: 'center', marginBottom: '1rem', color: '#888' }}>
              Note: This is a demo container. Below is the BookingForm component.
              Dummy resources have been passed in.
            </p>
            <BookingForm 
              resources={[
                { id: 1, name: 'Lecture Hall A', location: 'Main Building' },
                { id: 2, name: 'Computer Lab 3', location: 'IT Faculty' },
                { id: 3, name: 'Projector 1', location: 'Storage Room' }
              ]}
              userId={1}
              onSuccess={() => setCurrentView('my-bookings')}
            />
          </div>
        )}
        {currentView === 'my-bookings' && <MyBookingsPage userId={1} />}
        {currentView === 'admin' && <AdminBookingDashboard />}
      </main>
    </div>
  );
}

export default App;
