document.getElementById("registrationForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    // Form qiymatlarini olish
    const name = document.getElementById("name").value.trim();
    const phoneEmail = document.getElementById("phoneEmail").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();
    const errorTextTag = document.getElementById("errorText");

    // Oldingi xatoliklarni tozalash
    clearError(errorTextTag);

    // Kiritilgan ma'lumotlarni tekshirish
    if (!validatePasswordMatch(password, confirmPassword, errorTextTag)) return;
    if (!validatePhoneOrEmail(phoneEmail, errorTextTag)) return;

    // So‘rov uchun tayyor body
    const body = {
        name: name,
        username: phoneEmail,
        password: password,
    };

    try {
        const response = await fetch("http://localhost:9090/auth/registration", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error("Xatolik ma'lumotlari:", errorData);
            throw new Error(errorData.message || "Kutilmagan xatolik yuz berdi");
        }

        const data = await response.json(); // Muvaffaqiyatli javobni JSON formatida o‘qish
        console.log("Muvaffaqiyatli javob ma'lumotlari:", data);

        // Muvaffaqiyatli javobni qayta ishlash
        alert(data.message || "Ro‘yxatdan o‘tish muvaffaqiyatli! Tasdiqlash xati yuborildi.");
        window.location.href = "http://localhost:63342/GiybatProjects/giybat_uz_frontend/login.html";
    } catch (error) {
        console.error("Ushlangan xatolik:", error);

        if (error.message.includes("Failed to fetch")) {
            // Tarmoq yoki server bilan bog‘liq xatolik
            displayError(errorTextTag, "Server bilan bog‘lanishda muammo yuz berdi. Iltimos, qaytadan urinib ko‘ring.");
        } else {
            // API yoki validatsiya xatoligi
            displayError(errorTextTag, error.message || "Kutilmagan xatolik yuz berdi");
        }

        alert(error.message || "Xato yuz berdi. Iltimos, qaytadan urinib ko‘ring.");
    }
});

// Xatolik xabarlarini tozalash funksiyasi
function clearError(errorTextTag) {
    errorTextTag.textContent = "";
    errorTextTag.style.display = "none";
}

// Xatolik xabarlarini chiqarish funksiyasi
function displayError(errorTextTag, message) {
    errorTextTag.textContent = message;
    errorTextTag.style.display = "block";
}

// Parollarni tekshirish funksiyasi
function validatePasswordMatch(password, confirmPassword, errorTextTag) {
    if (password !== confirmPassword) {
        displayError(errorTextTag, "Parollar mos emas!");
        return false;
    }
    return true;
}

// Telefon raqami yoki emailni tekshirish funksiyasi
function validatePhoneOrEmail(value, errorTextTag) {
    // Email uchun regex: Oddiy email formatini tekshiradi (local-part@domain)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Telefon raqami uchun regex: +998 bilan boshlanadigan va 9 ta raqamni o‘z ichiga oladi
    const phoneRegex = /^\+998\d{9}$/; // Masalan, +998889996499

    if (emailRegex.test(value)) {
        console.log("Yaroqli email topildi");
        return true;
    } else if (phoneRegex.test(value)) {
        console.log("Yaroqli telefon raqami topildi");
        return true;
    } else {
        displayError(errorTextTag, "Telefon raqami yoki email noto‘g‘ri kiritilgan.");
        return false;
    }
}
