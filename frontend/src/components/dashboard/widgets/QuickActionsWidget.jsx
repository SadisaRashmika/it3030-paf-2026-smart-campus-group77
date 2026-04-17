import React from "react";
import { useNavigate } from "react-router-dom";

export default function QuickActionsWidget({ actions = [] }) {
  const navigate = useNavigate();
  return (
    <div className="flex flex-wrap gap-3">
      {actions.map((action, i) => {
        const Icon = action.icon;
        return (
          <button
            key={i}
            onClick={() => navigate(action.href)}
            className="flex items-center gap-2.5 rounded-2xl border border-slate-200 bg-white px-5 py-3.5 text-sm font-bold text-slate-700 shadow-sm hover:bg-blue-50 hover:border-blue-200 hover:text-blue-700 transition-all active:scale-95"
          >
            {Icon && <Icon size={18} />}
            {action.label}
          </button>
        );
      })}
    </div>
  );
}
