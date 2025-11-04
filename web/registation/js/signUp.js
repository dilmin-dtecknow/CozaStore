async function signUp() {
    const firstName = document.getElementById("first_name").value;
    const lastName = document.getElementById("last_name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm_password").value;

    const errorMessage = document.getElementById("error_message");

    const message = document.getElementById("message");

    if (!firstName) {
        errorMessage.textContent = "First Name field is required. Please fill in the field.";
    } else if (!lastName) {
        errorMessage.textContent = "Last Name field is required. Please fill in the field.";
    } else if (!email) {
        errorMessage.textContent = "Email field is required. Please fill in the field.";
    } else if (!password) {
        errorMessage.textContent = "Password field is required. Please fill in the field.";
    } else if (!confirmPassword) {
        errorMessage.textContent = "Confirm Password field is required. Please fill in the field.";
    } else if (password !== confirmPassword) {
        message.textContent = "Passwords do not match.";
    } else {
        errorMessage.textContent = ""; // Clear error message
//        alert("Form submitted successfully!");
        //request
        const user_dto = {
            first_name: firstName,
            last_name: lastName,
            email: email,
            password: password
        }

       
            const response = await fetch("SignUp", {
                method: 'POST',
                body: JSON.stringify(user_dto),
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                
//                alert("ok");
                const json = await response.json();

                if (json.success) {
                    window.location = "verify.html";
                    console.log("registerd");
                } else {
//                    document.getElementById("message").innerHTML = json.content;

                    Swal.fire({
                        title: 'Error!',
                        text: json.content,
                        icon: 'error',
                        confirmButtonText: 'Cool'
                    });
                }
            } else {
//                alert("Signup failed. Please try again.");
                Swal.fire({
                    title: 'Error!',
                    text: 'Signup failed. Please try again.',
                    icon: 'error',
                    confirmButtonText: 'Cool'
                });
            }
       

    }
}

function checkPasswordMatch() {
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm_password").value;
    const message = document.getElementById("message");

    if (password === confirmPassword) {
        message.textContent = "Passwords match";
        message.style.color = "green";
    } else {
        message.textContent = "Passwords do not match";
        message.style.color = "red";
    }

}

function capitalizeFirstLetter(input) {
    const value = input.value;
    input.value = value.charAt(0).toUpperCase() + value.slice(1);
}

//seller section

async function sellerSignUp() {
    const firstName = document.getElementById("seller_fname").value;
    const lastName = document.getElementById("seller_lname").value;
    const email = document.getElementById("seller_email").value;
    const password = document.getElementById("seller_password").value;
    const confirmPassword = document.getElementById("seller_conf_password").value;

    const errorMessage = document.getElementById("seller_msg");

    const message = document.getElementById("seller_upmessage");

    if (!firstName) {
        errorMessage.textContent = "First Name field is required. Please fill in the field.";
    } else if (!lastName) {
        errorMessage.textContent = "Last Name field is required. Please fill in the field.";
    } else if (!email) {
        errorMessage.textContent = "Email field is required. Please fill in the field.";
    } else if (!password) {
        errorMessage.textContent = "Password field is required. Please fill in the field.";
    } else if (!confirmPassword) {
        errorMessage.textContent = "Confirm Password field is required. Please fill in the field.";
    } else if (password !== confirmPassword) {
        message.textContent = "Passwords do not match.";
    } else {
        errorMessage.textContent = ""; // Clear error message
//        alert("Form submitted successfully!");
        //request
        const user_dto = {
            first_name: firstName,
            last_name: lastName,
            email: email,
            password: password
        }

       
            const response = await fetch("SellerSignUp", {
                method: 'POST',
                body: JSON.stringify(user_dto),
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                
//                alert("ok");
                const json = await response.json();

                if (json.success) {
                    window.location = "verify.html";
                    console.log("registerd");
                } else {
//                    document.getElementById("message").innerHTML = json.content;

                    Swal.fire({
                        title: 'Error!',
                        text: json.content,
                        icon: 'error',
                        confirmButtonText: 'Cool'
                    });
                }
            } else {
//                alert("Signup failed. Please try again.");
                Swal.fire({
                    title: 'Error!',
                    text: 'Signup failed. Please try again.',
                    icon: 'error',
                    confirmButtonText: 'Cool'
                });
            }
       

    }
}


function SellerheckPasswordMatch() {
    const password = document.getElementById("seller_password").value;
    const confirmPassword = document.getElementById("seller_conf_password").value;
    const message = document.getElementById("seller_upmessage");

    if (password === confirmPassword) {
        message.textContent = "Passwords match";
        message.style.color = "green";
    } else {
        message.textContent = "Passwords do not match";
        message.style.color = "red";
    }

}