async function signIn() {

    const user_dto = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    const response = await fetch("SignIn",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.success) {

            window.location = "index.html";

        } else {

            if (json.content === "UnVerified") {
                window.location = "verify.html";
            } else {

                Swal.fire({
                    title: 'Sorry',
                    text: json.content,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            }

        }

    } else {
        Swal.fire({
            title: 'Sorry',
            text: 'Something else. Please Try again later!',
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}

function forgotPassword() {
    window.location = "forgotPassword.html";
}

async function sendCode() {
    const user_dto = {
        email: document.getElementById("email").value,

    };
//    console.log(user_dto);
    const response = await fetch("ForgotPassword",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.success) {
//            window.location = "change.html";
            Swal.fire({
                title: 'Error!',
                text: json.content,
                icon: 'success',
                confirmButtonText: 'OK'
            });
        } else {

            Swal.fire({
                title: 'Error!',
                text: json.content,
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }

    } else {
        Swal.fire({
            title: 'Error!',
            text: 'Verification failed. Please try again.',
            icon: 'error',
            confirmButtonText: 'Cool'
        });
    }

}

async function verifyCode() {
    const code_dto = {
        verification: document.getElementById("code").value
    };

    const response = await fetch("VerifyCode",
            {
                method: "POST",
                body: JSON.stringify(code_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.success) {
//            window.location = "change.html";
            resetPassword();

        } else {

            Swal.fire({
                title: 'Error!',
                text: json.content,
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }

    } else {
        Swal.fire({
            title: 'Error!',
            text: 'Verification failed. Please try again.',
            icon: 'error',
            confirmButtonText: 'Cool'
        });
    }
}

//function forgotPassword() {
//    Swal.fire({
//        title: 'Enter your email',
//        input: 'email',
//        inputLabel: 'Email',
//        inputPlaceholder: 'Enter your email address',
//        showCancelButton: true,
//        confirmButtonText: 'Send Verification Code',
//        preConfirm: (email) => {
//            return fetch('ForgotPassword', {
//                method: 'POST',
//                body: JSON.stringify({ email: email }),
//                headers: { "Content-Type": "application/json" }
//            })
//            .then(response => response.json())
//            .then(data => {
//                if (data.success) {
//                    return email;  // Pass email to the next popup for verification
//                } else {
//                    Swal.showValidationMessage(data.content); // Display error
//                }
//            });
//        }
//    }).then((result) => {
//        if (result.isConfirmed) {
//            verifyCode(result.value);  // result.value will be the email
//        }
//    });
//}

//function verifyCode(email) {
//    Swal.fire({
//        title: 'Enter verification code',
//        input: 'text',
//        inputLabel: 'Verification Code',
//        inputPlaceholder: 'Enter the 6-digit code sent to your email',
//        showCancelButton: true,
//        confirmButtonText: 'Verify',
//        preConfirm: (code) => {
//            return fetch('VerifyCode', {
//                method: 'POST',
//                body: JSON.stringify({ email: email, code: code }),
//                headers: { "Content-Type": "application/json" }
//            })
//            .then(response => response.json())
//            .then(data => {
//                if (data.success) {
//                    return code;  // Pass code to the next popup to reset password
//                } else {
//                    Swal.showValidationMessage(data.content);
//                }
//            });
//        }
//    }).then((result) => {
//        if (result.isConfirmed) {
//            resetPassword(email);
//        }
//    });
//}
//
function resetPassword() {
    Swal.fire({
        title: 'Reset Password',
        html:
                '<input id="password" type="password" placeholder="New password" class="swal2-input">' +
                '<input id="confirmPassword" type="password" placeholder="Confirm password" class="swal2-input">',
        showCancelButton: true,
        confirmButtonText: 'Change Password',
        preConfirm: () => {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                Swal.showValidationMessage("Passwords do not match.");
            } else if (password.length < 8) {
                Swal.showValidationMessage("Password must be at least 8 characters long.");
            } else {
                return fetch('ResetPassword', {
                    method: 'POST',
                    body: JSON.stringify({password: password}),
                    headers: {"Content-Type": "application/json"}
                })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                Swal.fire('Success!', 'Password changed successfully', 'success');
                                window.location = "signIn.html";
                            } else {
                                Swal.showValidationMessage(data.content);
                            }
                        });
            }
        }
    });
}


