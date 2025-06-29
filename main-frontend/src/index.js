import React from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import Header from "./main/Header";
import Root from "./main/Root";

const header = createRoot(document.getElementById("app_header"));
header.render(<Header />);
const root = createRoot(document.getElementById("app_root"));
root.render(<Root />)
