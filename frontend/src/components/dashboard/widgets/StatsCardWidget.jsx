import React from "react";

export default function StatsCardWidget({ cards }) {
  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, i) => {
        const Icon = card.icon;
        return (
          <div
            key={i}
            className={`rounded-2xl p-5 border ${card.bg || "bg-white border-slate-200"} shadow-sm`}
          >
            <div className={`inline-flex items-center justify-center h-10 w-10 rounded-xl mb-3 ${card.iconBg || "bg-slate-100"}`}>
              {Icon && <Icon size={20} className={card.iconColor || "text-slate-600"} />}
            </div>
            <p className="text-2xl font-extrabold text-slate-900">
              {card.value ?? "—"}
            </p>
            <p className="text-xs font-bold uppercase tracking-widest text-slate-400 mt-1">
              {card.label}
            </p>
          </div>
        );
      })}
    </div>
  );
}
