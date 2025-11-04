async function homeLoadData() {
//    console.log("ok");

    const response = await  fetch("IndexData");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const data = json.response_dto;

        if (data.success) {
            const userData = data.content;

            let quick_link_ul = document.getElementById("quick-link-main");

            let quick_signin = document.getElementById("quik-signin");
            quick_signin.remove();

            let new_li_a_tag = document.createElement("a");
            let new_li_tag1 = document.createElement("li");
            
            let new_li_a_tag2 = document.createElement("a");
            let new_li_tag2 = document.createElement("li");

            new_li_a_tag.href = "#";
            new_li_a_tag.innerHTML = userData.first_name + " " + userData.last_name;
            
            new_li_a_tag2.href = "SignOut";
            new_li_a_tag2.innerHTML = "<i class='fa-solid fa-right-from-bracket'></i>";

            new_li_tag1.appendChild(new_li_a_tag);
            quick_link_ul.appendChild(new_li_tag1);
            
            new_li_tag2.appendChild(new_li_a_tag2);
            quick_link_ul.appendChild(new_li_tag2);
        } else {

        }

        const banerProduct = json.product;
        
        document.getElementById("baner-product-title").innerHTML = banerProduct.title;
         document.getElementById("baner-a").href = "shop-single.html?id=" + banerProduct.id;
        document.getElementById("baner-product-img").src = "product-images/" + banerProduct.id + "/image2.png";

        const productList = json.products;

        //similer product

        let productHtml = document.getElementById("featurd-product");
        document.getElementById("featurd-product-main").innerHTML = "";

        //similer product List
        productList.forEach(item => {
//                console.log(item.title);

            let productCloneHtml = productHtml.cloneNode(true);

            productCloneHtml.querySelector("#featurd-product-a1").href = "shop-single.html?id=" + item.id;
            productCloneHtml.querySelector("#featurd-product-image").src = "product-images/" + item.id + "/image1.png";
            productCloneHtml.querySelector("#featurd-product-a2").href = "shop-single.html?id=" + item.id;
            productCloneHtml.querySelector("#featurd-product-title").innerHTML = item.title;
            productCloneHtml.querySelector("#featurd-product-size").innerHTML = item.size.name;
            productCloneHtml.querySelector("#featurd-product-price").innerHTML = "Rs. " + new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);

            productCloneHtml.querySelector("#featurd-product-color").style.backgroundColor = item.color.name;

            //add to cart similer product
            productCloneHtml.querySelector("#featurd-product-add-to-cart").addEventListener("click",
                    (e) => {
                addToCart(item.id, 1);
                e.preventDefault();
            }
            );


            document.getElementById("featurd-product-main").appendChild(productCloneHtml);
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
        //not sign in
        console.log("Not Sign in");
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


