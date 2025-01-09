document.getElementById("registrationForm").addEventListener("submit", (event) => {
    event.preventDefault();

    // Retrieve form values
    const name = document.getElementById("name").value.trim();
    const phoneEmail = document.getElementById("phoneEmail").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();
    const errorTextTag = document.getElementById("errorText");

    // Clear previous error messages
    clearError(errorTextTag);

    // Validate inputs
    if (!validatePasswordMatch(password, confirmPassword, errorTextTag)) return;
    if (!validatePhoneOrEmail(phoneEmail, errorTextTag)) return;

    // If validation passes, proceed with form submission logic
    console.log("Form submitted successfully!");
});

function clearError(errorTextTag) {
    errorTextTag.textContent = "";
    errorTextTag.style.display = "none";
}

function displayError(errorTextTag, message) {
    errorTextTag.textContent = message;
    errorTextTag.style.display = "block";
}

function validatePasswordMatch(password, confirmPassword, errorTextTag) {
    if (password !== confirmPassword) {
        displayError(errorTextTag, "Parollar mos emas!");
        return false;
    }
    return true;
}

function validatePhoneOrEmail(value, errorTextTag) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^998\d{9}$/; // Matches numbers like 998912345678

    if (emailRegex.test(value)) {
        console.log("Valid email detected");
        return true;
    } else if (phoneRegex.test(value)) {
        console.log("Valid phone number detected");
        return true;
    } else {
        displayError(errorTextTag, "Telefon raqami yoki email noto‘g‘ri kiritilgan.");
        return false;
    }
}
