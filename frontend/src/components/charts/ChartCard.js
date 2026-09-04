export default function ChartCard({ title, subtitle, children, className = "" }) {
    return (
        <div
            className={`
                flex flex-col gap-3 rounded-lg border-2 border-secondary-bg-color
                bg-primary-bg-card-color p-4
                ${className}
            `}
        >
            <div className="flex flex-col gap-0.5">
                <h3 className="text-sm font-bold text-secondary-text">{title}</h3>
                {subtitle && (
                    <p className="text-xs text-secondary-text opacity-60">{subtitle}</p>
                )}
            </div>
            <div className="flex-1">{children}</div>
        </div>
    );
}
