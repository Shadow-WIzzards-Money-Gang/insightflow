export default function Card({
    value,
    label,
    textColor,
    bgColor,
    borderColor,
    valueClassName = "text-3xl sm:text-4xl lg:text-6xl",
    extra = null,
}) {
    return (
        <div
            className={`
                flex flex-col items-center justify-center relative border-2 w-full h-full
                min-w-0 min-h-32 sm:min-h-40 rounded-lg
                px-2 pt-4 pb-8 sm:pb-9
                ${borderColor}
                ${bgColor}
            `}
        >
            {extra}
            <p
                    className={`
                    ${textColor}
                    font-bold leading-tight break-words
                    text-center
                    ${valueClassName}
                `}
            >
                {value}
            </p>
            <p className={`
                text-secondary-text
                absolute bottom-0 w-full px-2
                pb-2 text-center text-xs sm:text-base
            `}>{label}</p>
        </div>
    );
}