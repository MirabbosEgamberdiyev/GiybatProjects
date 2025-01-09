document.getElementById("registrationForm").addEventListener("submit", async (event) => {
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

    // Prepare request body
    const body = {
        name: name,
        username: phoneEmail,
        password: password,
    };

    try {
        // Send POST request to the server
        const response = await fetch("http://localhost:9090/auth/registration", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        // Handle the response
        const data = await response.json();

        if (response.ok) {
            console.log("Form submitted successfully!");
            alert(data.message || "Ro‘yxatdan o‘tish muvaffaqiyatli! Emailingizga tasdiqlash xati yuborildi.");
            window.location.href = "http://localhost:63342/GiybatProjects/giybat_uz_frontend/login.html";
        } else {
            displayError(errorTextTag, data.message || "Xato yuz berdi. Iltimos, qaytadan urinib ko‘ring.");
            alert(data.message || "Xato yuz berdi. Iltimos, qaytadan urinib ko‘ring.");

        }
    } catch (error) {
        displayError(errorTextTag, "Server bilan bog‘lanishda xatolik yuz berdi.");
        console.error("Error:", error);
    }
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
    // Email regex: Matches a typical email format (local-part@domain)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Phone regex: Matches phone numbers starting with +998 and followed by 9 digits
    const phoneRegex = /^\+998\d{9}$/; // Matches numbers like +998889996499

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
