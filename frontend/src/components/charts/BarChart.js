"use client";

import { Bar } from "react-chartjs-2";

import "@/components/charts/chartSetup";

export default function BarChart({ data, options, altura = "h-64" }) {
    return (
        <div className={altura}>
            <Bar data={data} options={options} />
        </div>
    );
}
