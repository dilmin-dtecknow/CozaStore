async function loadCartItems() {

    const response = await fetch("LoadCartItems");

    if (response.ok) {
        const json = await response.json();

        if (json.length == 0) {

            Swal.fire({
                title: 'Done',
                text: "Your Cart empty,Continu Shoping",
                icon: 'info',
                confirmButtonText: 'OK'
            });
//            window.location = "index.html";
        } else {
            let cartItemContainer = document.getElementById("cart-item-container");
            let cartItemRow = document.getElementById("cart-item-row");

            cartItemContainer.innerHTML = "";

            let totalQuantity = 0;
            let total = 0;

            json.forEach(item => {

                let itemSubTotal = item.product.price * item.qty;

                totalQuantity += item.qty;
                total += itemSubTotal;

                let cartItemRowClone = cartItemRow.cloneNode(true);
                cartItemRowClone.querySelector("#cart-item-a").href = "shop-single.html?id=" + item.product.id;
                cartItemRowClone.querySelector("#cart-item-title").href = "shop-single.html?id=" + item.product.id;
                cartItemRowClone.querySelector("#cart-item-image").src = "product-images/" + item.product.id + "/image3.png";
                cartItemRowClone.querySelector("#cart-item-title").innerHTML = item.product.title;
                cartItemRowClone.querySelector("#cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);
                cartItemRowClone.querySelector("#cart-item-qty").value = item.qty;
                cartItemRowClone.querySelector("#cart-item-subtotal").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(itemSubTotal);

                // Add remove button functionality
                let removeButton = cartItemRowClone.querySelector("#cart-item-remove-button");
                removeButton.addEventListener("click", () => removeCartItem(item.product.id));
                // Add remove button functionality

                cartItemContainer.appendChild(cartItemRowClone);
            });
            document.getElementById("cart-total-qty").innerHTML = totalQuantity;
//            document.getElementById("cart-total").innerHTML = total;
            document.getElementById("cart-total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total);
        }
    } else {

        Swal.fire({
            title: 'Done',
            text: "Some thing went wrong!",
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}

//cart Remove item
async function removeCartItem(productId) {
    try {
        const response = await fetch("RemoveCartItem", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `productId=${productId}`
        });

        const result = await response.json();

        if (response.ok) {
            Swal.fire({
                title: 'Done',
                text: result.content,
                icon: 'success',
                confirmButtonText: 'OK'
            }).then(() => {
                // Reload the cart items
                loadCartItems();
            });
        } else {
            Swal.fire({
                title: 'Error',
                text: result.content,
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }
    } catch (error) {
        Swal.fire({
            title: 'Error',
            text: 'Something went wrong. Please try again.',
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}

