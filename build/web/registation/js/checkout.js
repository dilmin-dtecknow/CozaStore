//payHear
// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {

    console.log("Payment completed. OrderID:" + orderId);

    window.location = "thankyou.html";
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    console.log("Error:" + error);
};


async function loadCheckoutData() {
    const response = await fetch("LoadCheckout");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
            const address = json.address;
            const cityList = json.cityList;
            const cartList = json.cartList;

            //load cities
            let citySelect = document.getElementById("c_country");
            citySelect.length = 1;
            cityList.forEach(city => {
                let cityOption = document.createElement("option");
                cityOption.value = city.id;
                cityOption.innerHTML = city.name;
                citySelect.appendChild(cityOption);
            });

            //load diff cities
            let diff_citySelect = document.getElementById("c_diff_country");
            diff_citySelect.length = 1;
            cityList.forEach(city => {
                let cityOption = document.createElement("option");
                cityOption.value = city.id;
                cityOption.innerHTML = city.name;
                diff_citySelect.appendChild(cityOption);
            });

            //set current address
            let diferentAddressCheckbox = document.getElementById("c_ship_different_address");
            diferentAddressCheckbox.addEventListener("change", e => {

                let first_name = document.getElementById("c_fname");
                let last_name = document.getElementById("c_lname");
                let city = document.getElementById("c_country");
                let address1 = document.getElementById("c_address");
                let address2 = document.getElementById("c_address_l2");
                let postal_code = document.getElementById("c_postal_zip");
                let mobile = document.getElementById("c_phone");

                if (!diferentAddressCheckbox.checked) {
                    first_name.value = "";
                    last_name.value = "";
                    city.value = 0;
                    city.disabled = false;
                    city.dispatchEvent(new Event("change"));

                    address1.value = "";
                    address2.value = "";
                    postal_code.value = "";
                    mobile.value = "";

                } else {

                    first_name.value = address.first_name;
                    last_name.value = address.last_name;
                    city.value = address.city.id;
                    city.disabled = true;
                    city.dispatchEvent(new Event("change"));

                    address1.value = address.line1;
                    address2.value = address.line2;
                    postal_code.value = address.postal_code;
                    mobile.value = address.mobile;
                }
            });

            //load ceckout items
            let tbody = document.getElementById("cs-tbody");
            let item_tr = document.getElementById("item-tr");
            let order_subtotal_tr = document.getElementById("order-subtotal-tr");
            let order_shipping_tr = document.getElementById("order-shipping-tr");
            let order_total_tr = document.getElementById("order-total-tr");
            tbody.innerHTML = "";

            let sub_total = 0;
            cartList.forEach(item => {
                let item_clone = item_tr.cloneNode(true);
                item_clone.querySelector("#item-title").innerHTML = item.product.title;
                item_clone.querySelector("#item-qty").innerHTML = item.qty;

                let item_sub_total = item.product.price * item.qty;
                sub_total += item_sub_total;

                item_clone.querySelector("#item-subtotal").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item_sub_total);

                tbody.appendChild(item_clone);
            });

            order_subtotal_tr.querySelector("#subtotal").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(sub_total);
            tbody.appendChild(order_subtotal_tr);


            //update total on city change
            citySelect.addEventListener("change", e => {
                const selectedCityId = parseInt(citySelect.value);
                const selectedCity = cityList.find(city => city.id === selectedCityId);
                //update shipping caharges

                if (selectedCity) {
                    //get cat item count
                    let item_count = cartList.length;
                    let shipping_amount = item_count * selectedCity.shipingCharge;

                    console.log("Selected city shipping charge:", selectedCity.shipingCharge);
                    console.log("Shipping amount:", shipping_amount);


//                    if (citySelect.value == 1) {
//                        //colombo
//                        shipping_amount = item_count * 1000;
//                    } else {
//                        //outof colombo
//                        shipping_amount = item_count * 2500;
//                    }

                    order_shipping_tr.querySelector("#shipping-amount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(shipping_amount);

                    tbody.appendChild(order_shipping_tr);

                    //update total
                    let total = sub_total + shipping_amount;
                    order_total_tr.querySelector("#total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);
                    tbody.appendChild(order_total_tr);
                }
            });

//            city.dispatchEvent(new Event("change"));


        } else {

            Swal.fire({
                title: 'Done',
                text: json.message,
                icon: 'error',
                confirmButtonText: 'OK'
            });
//        window.location="signIn.html";
        }

    } else {

        console.log("some thing went wrong");
    }
}

async function checkout() {
//    window.location='thankyou.html'
    let isCurentAddressCheckbox = document.getElementById("c_ship_different_address").checked;

//get address data
    let first_name = document.getElementById("c_fname");
    let last_name = document.getElementById("c_lname");
    let city = document.getElementById("c_country");
    let address1 = document.getElementById("c_address");
    let address2 = document.getElementById("c_address_l2");
    let postal_code = document.getElementById("c_postal_zip");
    let mobile = document.getElementById("c_phone");
    
   //request data(json)
    const data = {
        isCurentAddressCheckbox: isCurentAddressCheckbox,
        first_name: first_name.value,
        last_name: last_name.value,
        city_id: city.value,
        address1: address1.value,
        address2: address2.value,
        postal_code: postal_code.value,
        mobile: mobile.value
    };
    
    const response = await fetch("Checkout",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );
    
    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
//            console.log(json.payherJson);
            payhere.startPayment(json.payherJson);
            Swal.fire({
                title: 'Done',
                text: json.message,
                icon: 'success',
                confirmButtonText: 'OK'
            });
        } else {
            Swal.fire({
                title: 'Done',
                text: json.message,
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }

    } else {
        Swal.fire({
                title: 'Done',
                text: "Try Again",
                icon: 'error',
                confirmButtonText: 'OK'
            });
    }

}

