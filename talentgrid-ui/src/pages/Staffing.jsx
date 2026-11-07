import { useEffect, useState } from "react";
import api, { errorMessage } from "../api/client.js";

export default function Staffing() {
  const [projects, setProjects] = useState([]);
  const [projectId, setProjectId] = useState("");
  const [strategy, setStrategy] = useState("WEIGHTED");
  const [top, setTop] = useState(5);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [percentById, setPercentById] = useState({});

  useEffect(() => {
    api.get("/projects").then((r) => setProjects(r.data)).catch((e) => setError(errorMessage(e)));
  }, []);

  const findTeam = () => {
    setError(""); setMessage(""); setResult(null);
    api.get(`/projects/${projectId}/recommendations`, { params: { strategy, top } })
      .then((r) => setResult(r.data))
      .catch((e) => setError(errorMessage(e)));
  };

  const allocate = (c) => {
    const percent = Number(percentById[c.employeeId] || Math.min(c.availablePercent, 50));
    const project = projects.find((p) => String(p.id) === String(projectId));
    api.post("/allocations", {
      employeeId: c.employeeId,
      projectId: Number(projectId),
      roleName: "Team Member",
      allocationPercent: percent,
      startDate: project?.startDate || new Date().toISOString().slice(0, 10),
      endDate: project?.endDate || null,
    })
      .then(() => { setMessage(`${c.fullName} allocated at ${percent}%`); setError(""); findTeam(); })
      .catch((e) => { setError(errorMessage(e)); setMessage(""); });
  };

  return (
    <div>
      <h1>Staffing</h1>
      {error && <div className="error">{error}</div>}
      {message && <div className="success">{message}</div>}

      <div className="card row">
        <label>Project
          <select value={projectId} onChange={(e) => setProjectId(e.target.value)}>
            <option value="">Select a project</option>
            {projects.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
        </label>
        <label>Ranking
          <select value={strategy} onChange={(e) => setStrategy(e.target.value)}>
            <option value="WEIGHTED">Best skill match</option>
            <option value="EXPERIENCE_FIRST">Most experienced</option>
            <option value="AVAILABILITY_FIRST">Most available</option>
          </select>
        </label>
        <label>Top
          <input type="number" min="1" max="20" value={top} onChange={(e) => setTop(e.target.value)} style={{ width: 70 }} />
        </label>
        <button disabled={!projectId} onClick={findTeam}>Find Team</button>
      </div>

      {result && (
        <div>
          <p>
            <b>{result.projectName}</b>: {result.requiredSkillCount} required skills,
            {" "}{result.candidatesConsidered} employees considered
          </p>
          <table>
            <thead>
              <tr><th>#</th><th>Name</th><th>Match</th><th>Available</th><th>Exp</th><th>Gaps</th><th>Allocate %</th><th></th></tr>
            </thead>
            <tbody>
              {result.candidates.map((c, i) => (
                <tr key={c.employeeId}>
                  <td>{i + 1}</td>
                  <td>{c.fullName}<br /><small>{c.empCode}</small></td>
                  <td>
                    <div className="bar"><div style={{ width: `${c.matchPercent}%` }} /></div>
                    {c.matchPercent}%
                  </td>
                  <td>{c.availablePercent}%</td>
                  <td>{c.experienceYears} yrs</td>
                  <td>
                    {c.missingSkills.map((s) => <span key={s} className="badge red">missing: {s}</span>)}
                    {c.weakSkills.map((s) => <span key={s} className="badge amber">weak: {s}</span>)}
                    {c.missingSkills.length + c.weakSkills.length === 0 && "none"}
                  </td>
                  <td>
                    <input type="number" min="1" max={c.availablePercent} style={{ width: 70 }}
                      value={percentById[c.employeeId] ?? Math.min(c.availablePercent, 50)}
                      onChange={(e) => setPercentById({ ...percentById, [c.employeeId]: e.target.value })} />
                  </td>
                  <td><button onClick={() => allocate(c)}>Allocate</button></td>
                </tr>
              ))}
              {result.candidates.length === 0 && <tr><td colSpan="8">No suitable candidates</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}