async function verifyMe() {

    const code_dto = {
        verification: document.getElementById("code").value
    };

    const response = await fetch("Verification",
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
            window.location = "index.html";
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

