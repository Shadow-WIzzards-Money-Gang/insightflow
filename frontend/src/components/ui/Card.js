export default function Card({ value, label, textColor, bgColor, borderColor }) {
    return (
        <div 
            className={`
                flex flex-col items-center justify-center relative border-2 w-full h-full min-w-50 min-h-40 rounded-lg
                ${borderColor} 
                ${bgColor}
            `}
        >
            <p 
                    className={`
                    ${textColor}
                    font-bold
                    text-6xl
                `}
            >
                {value}
            </p>
            <p className={`
                text-secondary-text
                absolute bottom-0
                pb-2
            `}>{label}</p>
        </div>
    );
}