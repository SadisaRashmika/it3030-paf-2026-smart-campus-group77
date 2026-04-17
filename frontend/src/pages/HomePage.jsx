import { useOutletContext } from "react-router-dom";
import PortalHomeContent from "../member4-notifications-oauth/components/PortalHomeContent";

export default function HomePage() {
  const { user, onLogin, onNavigate } = useOutletContext();

  return <PortalHomeContent user={user} onLogin={onLogin} onNavigate={onNavigate} />;
}
