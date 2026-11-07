import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api" });

// turns any backend error into a readable message
export function errorMessage(err) {
  const data = err.response?.data;
  if (data?.fieldErrors) {
    return Object.entries(data.fieldErrors).map(([f, m]) => `${f}: ${m}`).join(", ");
  }
  return data?.message || err.message || "Something went wrong";
}

export default api;