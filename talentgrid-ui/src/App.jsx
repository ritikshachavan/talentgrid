import { NavLink, Route, Routes } from "react-router-dom";
import Employees from "./pages/Employees.jsx";
import Staffing from "./pages/Staffing.jsx";

export default function App() {
  const link = ({ isActive }) => (isActive ? "active" : "");
  return (
    <div className="layout">
      <nav className="sidebar">
        <h2>TalentGrid</h2>
        <NavLink to="/" end className={link}>Employees</NavLink>
        <NavLink to="/staffing" className={link}>Staffing</NavLink>
      </nav>
      <main className="content">
        <Routes>
          <Route path="/" element={<Employees />} />
          <Route path="/staffing" element={<Staffing />} />
        </Routes>
      </main>
    </div>
  );
}