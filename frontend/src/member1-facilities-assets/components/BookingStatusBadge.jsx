import React from "react";

const BookingStatusBadge = ({ status }) => {
  const getStatusStyles = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-amber-100 text-amber-700 border-amber-200";
      case "APPROVED":
        return "bg-emerald-100 text-emerald-700 border-emerald-200";
      case "REJECTED":
        return "bg-rose-100 text-rose-700 border-rose-200";
      case "CANCELLED":
        return "bg-slate-100 text-slate-500 border-slate-200";
      default:
        return "bg-gray-100 text-gray-500 border-gray-200";
    }
  };

  return (
    <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold border ${getStatusStyles(status)}`}>
      {status}
    </span>
  );
};

export default BookingStatusBadge;
