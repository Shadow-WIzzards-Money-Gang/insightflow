"use client";

import { forwardRef } from "react";
import { Pie } from "react-chartjs-2";

import "@/components/charts/chartSetup";

const PieChart = forwardRef(function PieChart({ data, options, altura = "h-64" }, ref) {
    return (
        <div className={altura}>
            <Pie ref={ref} data={data} options={options} />
        </div>
    );
});

export default PieChart;
