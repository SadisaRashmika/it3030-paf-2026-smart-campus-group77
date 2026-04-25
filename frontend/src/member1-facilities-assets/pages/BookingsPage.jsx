import React from "react";
import BookingPanel from "../components/BookingPanel";

const BookingsPage = ({ user }) => {
    return (
        <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
            <BookingPanel user={user} />
        </div>
    );
};

export default BookingsPage;
