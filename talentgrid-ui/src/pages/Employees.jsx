import { useEffect, useState } from "react";
import api, { errorMessage } from "../api/client.js";

export default function Employees() {
  const [departments, setDepartments] = useState([]);
  const [skills, setSkills] = useState([]);
  const [filters, setFilters] = useState({ name: "", departmentId: "", skillId: "", minProficiency: "", status: "" });
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [error, setError] = useState("");

  // dropdown data, loaded once
  useEffect(() => {
    api.get("/departments").then((r) => setDepartments(r.data)).catch((e) => setError(errorMessage(e)));
    api.get("/skills").then((r) => setSkills(r.data)).catch((e) => setError(errorMessage(e)));
  }, []);

  // reload whenever filters or page change
  useEffect(() => {
    const params = { page, size: 8, sort: "firstName,asc" };
    Object.entries(filters).forEach(([k, v]) => { if (v !== "") params[k] = v; });
    api.get("/employees/search", { params })
      .then((r) => { setData(r.data); setError(""); })
      .catch((e) => setError(errorMessage(e)));
  }, [filters, page]);

  const change = (key) => (e) => { setPage(0); setFilters({ ...filters, [key]: e.target.value }); };

  return (
    <div>
      <h1>Employees</h1>
      {error && <div className="error">{error}</div>}

      <div className="card row">
        <label>Name<input value={filters.name} onChange={change("name")} placeholder="search..." /></label>
        <label>Department
          <select value={filters.departmentId} onChange={change("departmentId")}>
            <option value="">All</option>
            {departments.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
          </select>
        </label>
        <label>Skill
          <select value={filters.skillId} onChange={change("skillId")}>
            <option value="">Any</option>
            {skills.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </label>
        <label>Min level
          <select value={filters.minProficiency} onChange={change("minProficiency")} disabled={!filters.skillId}>
            <option value="">Any</option>
            {[1, 2, 3, 4, 5].map((n) => <option key={n} value={n}>{n}+</option>)}
          </select>
        </label>
        <label>Status
          <select value={filters.status} onChange={change("status")}>
            <option value="">All</option>
            <option>ACTIVE</option><option>ON_LEAVE</option><option>INACTIVE</option>
          </select>
        </label>
      </div>

      <table>
        <thead>
          <tr><th>Code</th><th>Name</th><th>Department</th><th>Designation</th><th>Exp (yrs)</th><th>Manager</th><th>Status</th></tr>
        </thead>
        <tbody>
          {data.content.map((e) => (
            <tr key={e.id}>
              <td>{e.empCode}</td><td>{e.fullName}</td><td>{e.departmentName}</td>
              <td>{e.designationTitle}</td><td>{e.experienceYears}</td>
              <td>{e.managerName || "-"}</td><td>{e.status}</td>
            </tr>
          ))}
          {data.content.length === 0 && <tr><td colSpan="7">No employees found</td></tr>}
        </tbody>
      </table>

      <div className="row" style={{ marginTop: 12 }}>
        <button className="secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
        <span>Page {page + 1} of {Math.max(data.totalPages, 1)} ({data.totalElements} employees)</span>
        <button className="secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
      </div>
    </div>
  );
}