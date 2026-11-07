import { useEffect, useState } from "react";
import {
  Bar, BarChart, CartesianGrid, Cell, Legend, Pie, PieChart,
  ResponsiveContainer, Tooltip, XAxis, YAxis,
} from "recharts";
import api, { errorMessage } from "../api/client.js";

const STATUS_COLORS = { ACTIVE: "#22c55e", ON_LEAVE: "#f59e0b", INACTIVE: "#94a3b8" };

export default function Dashboard() {
  const [summary, setSummary] = useState({});
  const [utilization, setUtilization] = useState([]);
  const [skills, setSkills] = useState([]);
  const [bench, setBench] = useState([]);
  const [expiring, setExpiring] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([
      api.get("/analytics/summary"),
      api.get("/analytics/utilization"),
      api.get("/analytics/skill-distribution"),
      api.get("/analytics/bench"),
      api.get("/analytics/expiring-allocations", { params: { days: 30 } }),
    ])
      .then(([s, u, k, b, x]) => {
        setSummary(s.data);
        setUtilization(u.data);
        setSkills(k.data);
        setBench(b.data);
        setExpiring(x.data);
      })
      .catch((e) => setError(errorMessage(e)));
  }, []);

  const total = Object.values(summary).reduce((a, b) => a + b, 0);
  const pieData = Object.entries(summary).map(([name, value]) => ({ name, value }));
  const utilData = utilization.map((u) => ({
    name: u.fullName.split(" ")[0],
    Allocated: u.allocatedPercent,
    Available: u.availablePercent,
  }));
  const skillData = skills.map((s) => ({ name: s.skillName, Employees: s.employeeCount }));

  return (
    <div>
      <h1>Dashboard</h1>
      {error && <div className="error">{error}</div>}

      <div className="stats">
        <div className="stat"><div className="num">{total}</div><div className="lbl">Total employees</div></div>
        <div className="stat"><div className="num">{summary.ACTIVE || 0}</div><div className="lbl">Active</div></div>
        <div className="stat"><div className="num">{bench.length}</div><div className="lbl">On bench (no project)</div></div>
        <div className="stat"><div className="num">{expiring.length}</div><div className="lbl">Allocations ending in 30 days</div></div>
      </div>

      <div className="grid2">
        <div className="card">
          <h3>Employees by status</h3>
          <div className="chart">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={pieData} dataKey="value" nameKey="name" outerRadius={95} label>
                  {pieData.map((d) => <Cell key={d.name} fill={STATUS_COLORS[d.name] || "#3b82f6"} />)}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card">
          <h3>Utilization per employee (%)</h3>
          <div className="chart">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={utilData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" interval={0} angle={-30} textAnchor="end" height={60} />
                <YAxis domain={[0, 100]} />
                <Tooltip />
                <Legend />
                <Bar dataKey="Allocated" stackId="a" fill="#3b82f6" />
                <Bar dataKey="Available" stackId="a" fill="#cbd5e1" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card">
          <h3>Skill distribution (employees per skill)</h3>
          <div className="chart">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={skillData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" interval={0} angle={-30} textAnchor="end" height={60} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="Employees" fill="#8b5cf6" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card">
          <h3>Allocations ending in 30 days</h3>
          <table>
            <thead><tr><th>Employee</th><th>Project</th><th>%</th><th>Ends</th><th>Days left</th></tr></thead>
            <tbody>
              {expiring.map((a) => (
                <tr key={a.allocationId}>
                  <td>{a.employeeName}</td><td>{a.projectName}</td>
                  <td>{a.allocationPercent}%</td><td>{a.endDate}</td>
                  <td><span className={a.daysLeft <= 7 ? "badge red" : "badge amber"}>{a.daysLeft}</span></td>
                </tr>
              ))}
              {expiring.length === 0 && <tr><td colSpan="5">Nothing ending soon</td></tr>}
            </tbody>
          </table>
        </div>
      </div>

      <div className="card">
        <h3>Bench: available for new projects</h3>
        <table>
          <thead><tr><th>Code</th><th>Name</th><th>Department</th><th>Designation</th><th>Exp (yrs)</th></tr></thead>
          <tbody>
            {bench.map((e) => (
              <tr key={e.id}>
                <td>{e.empCode}</td><td>{e.fullName}</td><td>{e.departmentName}</td>
                <td>{e.designationTitle}</td><td>{e.experienceYears}</td>
              </tr>
            ))}
            {bench.length === 0 && <tr><td colSpan="5">Everyone is allocated</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}