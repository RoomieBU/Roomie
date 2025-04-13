import PropTypes from "prop-types";

function Spinner({ load }) {
    // Inline style for centering
    const spinnerContainerStyle = {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        flexDirection: "column",
        width: "100%",
        height: "100%",
    };

    const textStyle = {
        marginBottom: "10px",
        textAlign: "center"
    };

    const spinnerStyle = {
        width: "2rem",  // Spinner size
        height: "2rem"
    };

    return (
        <div style={spinnerContainerStyle}>
            <p style={textStyle}>{`Loading ${load}`}</p>
            <div className="spinner-border text-primary" style={spinnerStyle} role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    );
}

Spinner.propTypes = {
    load: PropTypes.string.isRequired
};

export default Spinner;