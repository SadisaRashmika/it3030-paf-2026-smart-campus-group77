import React from "react";
import PendingApprovalsPanel from "../components/PendingApprovalsPanel";

const ApprovalsPage = ({ user }) => {
    return (
        <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
            <PendingApprovalsPanel user={user} />
        </div>
    );
};

export default ApprovalsPage;
    
