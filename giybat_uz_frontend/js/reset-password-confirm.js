function resetPasswordConfirm() {
    const confirmCodeValue = document.getElementById("confirm_code").value;
    const newPasswordValue = document.getElementById("new_password").value;
    const username = localStorage.getItem("username");

    if (!confirmCodeValue || !newPasswordValue || !username) {
        alert("Please fill all inputs");
        return;
    }


}