"use client";

import { forwardRef } from "react";
import { Bar } from "react-chartjs-2";

import "@/components/charts/chartSetup";

const BarChart = forwardRef(function BarChart({ data, options, altura = "h-64" }, ref) {
    return (
        <div className={altura}>
            <Bar ref={ref} data={data} options={options} />
        </div>
    );
});

export default BarChart;
