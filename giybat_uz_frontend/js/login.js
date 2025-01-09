async function login() {
    const usernameInput = document.getElementById("username");
    const username = usernameInput.value;

    const passwordInput = document.getElementById("password");
    const password = passwordInput.value;

    // Find the error display element
    const errorTextTag = document.getElementById("errorText"); // Make sure you have this element in the HTML

    // Validate the username and password
    if (!validatePhoneOrEmail(username, errorTextTag)) return;

    // Prepare request body for login
    const body = {
        username: username,
        password: password
    };

    try {
        // Send POST request to the server for login
        const response = await fetch("http://localhost:9090/auth/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        // Handle the response
        const data = await response.json();

        if (response.ok) {
            console.log("Login successful!");
            alert(data.message || "Tizimga kirish muvaffaqiyatli!");
            window.location.href = "http://localhost:63342/GiybatProjects/giybat_uz_frontend/sms-confirm.html"; // Redirect to the dashboard or home page
        } else {
            displayError(errorTextTag, data.message || "Login xato! Iltimos, qaytadan urinib ko‘ring.");
            alert(data.message || "Login xato! Iltimos, qaytadan urinib ko‘ring.");
        }
    } catch (error) {
        displayError(errorTextTag, "Server bilan bog‘lanishda xatolik yuz berdi.");
        console.error("Error:", error);
    }
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

function displayError(errorTextTag, message) {
    if (errorTextTag) {
        errorTextTag.textContent = message;
        errorTextTag.style.color = "red"; // Optional: Change color to red for visibility
    }
}
