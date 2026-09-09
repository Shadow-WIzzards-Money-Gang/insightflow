"use client";

export default function Button({ label, handleClick, disabled = false }) {
    return (
        <button
          disabled={disabled}
          className={`
            text-primary-bg-card-color
            bg-primary-text
            text-md
            font-medium
            py-2
            px-4
            rounded
            cursor-pointer
            whitespace-nowrap
            transition-opacity
            hover:opacity-90
            focus:outline-none
            focus-visible:ring-2
            focus-visible:ring-primary-text
            uppercase
            disabled:cursor-not-allowed
            disabled:opacity-50
            disabled:hover:opacity-50
          `}
          onClick={(e) => handleClick?.(e)}
        >
            { label }
        </button>
    );
}