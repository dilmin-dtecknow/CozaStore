async function loadProduct() {
    const parameters = new URLSearchParams(window.location.search);
    console.log(parameters);
    if (parameters.has("id")) {
        const productId = parameters.get("id");
        console.log(productId);

        const response = await fetch("LoadSingleProduct?id=" + productId);
//
        if (response.ok) {
            const json = await response.json();
            console.log(json);
//            console.log(json.productList);
            const id = json.product.id;

            document.getElementById("mainImage").src = "product-images/" + id + "/image1.png";
            document.getElementById("image1").src = "product-images/" + id + "/image1.png";
            document.getElementById("image2").src = "product-images/" + id + "/image2.png";
            document.getElementById("image3").src = "product-images/" + id + "/image3.png";

            document.getElementById("product-tile").innerHTML = json.product.title;
            document.getElementById("product-title-link").innerHTML = json.product.title;
            document.getElementById("product-description").innerHTML = json.product.description;
            document.getElementById("product-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(json.product.price);
            document.getElementById("product-brand").innerHTML = json.product.brand.name;
            document.getElementById("product-main-category").innerHTML = json.product.category.main_category.name;
            document.getElementById("product-sub-category").innerHTML = json.product.category.sub_category.name;
            document.getElementById("product-qty").innerHTML = json.product.qty;
            document.getElementById("product-size").innerHTML = json.product.size.name;
            document.getElementById("product-color").style.backgroundColor = json.product.color.name;

            //add to cart
            document.getElementById("add-to-cart-main").addEventListener("click",
                    (e) => {
                addToCart(json.product.id, document.getElementById("add-to-cart-qty").value);
                e.preventDefault();
            }
            );
            //similer product

            let productHtml = document.getElementById("similer-product");
            document.getElementById("similer-product-main").innerHTML = "";

            //similer product List
            json.productList.forEach(item => {
//                console.log(item.title);

                let productCloneHtml = productHtml.cloneNode(true);

                productCloneHtml.querySelector("#similer-product-a1").href = "shop-single.html?id=" + item.id;
                productCloneHtml.querySelector("#similer-product-image").src = "product-images/" + item.id + "/image1.png";
                productCloneHtml.querySelector("#similer-product-a2").href = "shop-single.html?id=" + item.id;
                productCloneHtml.querySelector("#similer-product-title").innerHTML = item.title;
                productCloneHtml.querySelector("#similer-product-size").innerHTML = item.size.name;
                productCloneHtml.querySelector("#similer-product-price").innerHTML = "Rs. " + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);

                productCloneHtml.querySelector("#similer-product-color").style.backgroundColor = item.color.name;

                //add to cart similer product
                productCloneHtml.querySelector("#similer-product-add-to-cart").addEventListener("click",
                        (e) => {
                    addToCart(item.id, 1);
                    e.preventDefault();
                }
                );


                document.getElementById("similer-product-main").appendChild(productCloneHtml);
            });

            "use strict";

            var slider = function () {
                $('.nonloop-block-3').owlCarousel({
                    center: false,
                    items: 1,
                    loop: false,
                    stagePadding: 15,
                    margin: 20,
                    nav: true,
                    navText: ['<span class="icon-arrow_back">', '<span class="icon-arrow_forward">'],
                    responsive: {
                        600: {
                            margin: 20,
                            items: 2
                        },
                        1000: {
                            margin: 20,
                            items: 3
                        },
                        1200: {
                            margin: 20,
                            items: 3
                        }
                    }
                });
            };
            slider();
        } else {
            window.location = "index.html";
        }
    } else {

        window.location = "index.html";

    }
}

async function addToCart(id, qty) {
//    console.log("add to cart" + id);
//    console.log("add to cart" + qty);

    const response = await fetch(
            "AddToCart?id=" + id + "&qty=" + qty
    );

    if (response.ok) {
        const json = await response.json();
        
        if (json.success) {
            
            Swal.fire({
                    title: 'Done',
                    text: json.content,
                    icon: 'success',
                    confirmButtonText: 'OK'
                });
                window.location = "cart.html";
        } else {
            Swal.fire({
                    title: 'Sorry',
                    text: json.content,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
        }
    } else {
       Swal.fire({
                    title: 'Sorry',
                    text: "Please Try again in a few minutes",
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
    }
}


