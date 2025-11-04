async function loadShopData() {
    const response = await fetch("ShopLoadData");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const main_CategoryList = json.main_CategoryList;
        loadOption("maincategory", json.main_CategoryList, "name");
//        loadSelect("maincategory", main_CategoryList, "name");
        loadOption("category", json.sub_CategorysList, "name");
        loadOption("size", json.sizeList, "name");
        loadOption("color", json.colorList, "name");
        loadOption("brand", json.brandList, "name");
        
        updateProductView(json);
    } else {
        Swal.fire({
            title: 'Error!',
            text: 'Please try again.',
            icon: 'error',
            confirmButtonText: 'Cool'
        });
    }

}

function loadSelect(selectTagId, list, property) {
    const SelectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item[property];
        SelectTag.appendChild(optionTag);
    });
}

function loadOption(prfix, dataList, property) {
    let options = document.getElementById(prfix + "-list");
    let li = document.getElementById(prfix + "-li");

    options.innerHTML = "";  // Clear current options

    dataList.forEach(data => {
        // Clone the template li
        let li_clone = li.cloneNode(true);

        if (prfix == "color") {
            li_clone.querySelector("#" + prfix + "-a span:nth-child(2)").style.backgroundColor = data[property];
//            li_clone.querySelector("#" + prfix + "-a span:nth-child(2)").innerHTML = data[property];
        } else {
            // Set the text for each option
            li_clone.querySelector("#" + prfix + "-a span:nth-child(2)").innerHTML = data[property];
        }
        // Append the cloned li to the options list
        options.appendChild(li_clone);
    });

    // Add click event listeners to each cloned li for tick functionality
    document.querySelectorAll("#" + prfix + "-list li").forEach(function (item) {
        item.addEventListener("click", function (event) {
            event.preventDefault();  // Prevent default anchor click behavior

            // Remove 'selected' class from all list items
            document.querySelectorAll("#" + prfix + "-list li").forEach(function (item) {
                item.classList.remove("selected");
            });

            // Add 'selected' class to clicked list item
            this.classList.add("selected");

            // Manage the tick icon display
            updateTickIcon(this, prfix);
        });
    });
}

// Helper function to manage tick icon display
function updateTickIcon(selectedItem, prfix) {
    // Remove tick for all list items
    document.querySelectorAll("#" + prfix + "-list li").forEach(function (item) {
        let tick = item.querySelector(".tick");
        if (tick) {
            tick.innerHTML = "";  // Clear any existing tick
        }
    });

    // Add tick to selected list item
    let selectedTick = selectedItem.querySelector(".tick");
    if (selectedTick) {
        selectedTick.innerHTML = '<i class="fas fa-check"></i>';  // Font Awesome check icon
    }
}

async function searchProducts(firstResult) {
    

    let main_category_name = document.getElementById("maincategory-list").querySelector(".selected a span:nth-child(2)")?.innerHTML;
    console.log(main_category_name);

    let sub_category_name = document.getElementById("category-list").querySelector(".selected a span:nth-child(2)")?.innerHTML;
    console.log(sub_category_name);

    let brand_name = document.getElementById("brand-list").querySelector(".selected a span:nth-child(2)")?.innerHTML;
    console.log(brand_name);
    
    
    let price_range_start = $('#slider-range').slider('values', 0);
    let price_range_end = $('#slider-range').slider('values', 1);
    console.log(price_range_start);
    console.log(price_range_end);
    
    let size_name = document.getElementById("size-list").querySelector(".selected a span:nth-child(2)")?.innerHTML;
    console.log(size_name);
    
    let color_name = document.getElementById("color-list").querySelector(".selected a span:nth-child(2)")?.style.backgroundColor;
    console.log(color_name);
    
   let sort_text = document.getElementById("p-sort").value;
    console.log(sort_text);

    const data = {
        firstResult: firstResult,
        main_category_name: main_category_name,
        sub_category_name: sub_category_name,
        brand_name: brand_name,
        size_name: size_name,
        price_range_start: price_range_start,
        price_range_end: price_range_end,
        color_name:color_name,
        sort_text: sort_text,
    };
    
    const response = await fetch("SearchProdcts",
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
            updateProductView(json);
//            currentPage = 0;
//            if (json.allProductCount==0) {
//                Swal.fire({
//            title: 'Ok!',
//            text: 'No Product Available!',
//            icon: 'success',
//            confirmButtonText: 'Cool'
//        }); 
//            }
//            Swal.fire({
//            title: 'Ok!',
//            text: json.message,
//            icon: 'success',
//            confirmButtonText: 'Cool'
//        });
        } else {
          Swal.fire({
            title: 'Error!',
            text: json.message,
            icon: 'error',
            confirmButtonText: 'Cool'
        });
        }
    } else {
        Swal.fire({
            title: 'Error!',
            text: 'Please try again.',
            icon: 'error',
            confirmButtonText: 'Cool'
        });
    }
}

var currentPage = 0;
var single_product = document.getElementById("single-product");
//pagination button
var pagination_button = document.getElementById("pagination-button");
function updateProductView(json) {
    //start load product
    let product_container = document.getElementById("product-container");
    product_container.innerHTML = "";
    
    json.productList.forEach(product => {
        let single_product_clone = single_product.cloneNode(true);
//          console.log(product);
        //update details
        single_product_clone.querySelector("#single-product-a-1").href = "shop-single.html?id=" + product.id;
        single_product_clone.querySelector("#single-product-img-1").src = "product-images/" + product.id + "/image1.png";
        single_product_clone.querySelector("#single-product-a-2").href = "shop-single.html?id=" + product.id;
        single_product_clone.querySelector("#single-product-title").innerHTML = product.title;
        single_product_clone.querySelector("#single-product-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price);;
        //update details
        product_container.appendChild(single_product_clone);
    });
    //end:load product
    
    //start pagination
    let pagination_container = document.getElementById("pagination-container");
    pagination_container.innerHTML = "";

    let product_count = json.allProductCount;
    const product_per_page = 6;
    let pages = Math.ceil(product_count / product_per_page);
    
    //Add privios button
    if (currentPage != 0) {
        let pagination_button_clone_prv = pagination_button.cloneNode(true);
        pagination_button_clone_prv.innerHTML = "&lt;";

        pagination_button_clone_prv.addEventListener("click", e => {
            currentPage--;
            searchProducts(currentPage * 6);
        });
        pagination_container.appendChild(pagination_button_clone_prv);
    }
    
    //add page buttons
    for (let i = 0; i < pages; i++) {
        let pagination_button_clone = pagination_button.cloneNode(true);
        pagination_button_clone.innerHTML = i + 1;

        pagination_button_clone.addEventListener("click", e => {
//            alert("ok");
            currentPage = i;
            searchProducts(i * 6);

        });

        if (i == currentPage) {
            pagination_button_clone.className = "active";
        } else {
            pagination_button_clone.className = "";
        }
        pagination_container.appendChild(pagination_button_clone);
    }
    
    //Add Next button
    if (currentPage != (pages - 1)) {
        let pagination_button_clone_next = pagination_button.cloneNode(true);
        pagination_button_clone_next.innerHTML = "&gt;";

        pagination_button_clone_next.addEventListener("click", e => {
            currentPage++;
            searchProducts(currentPage * 6);
        });
        pagination_container.appendChild(pagination_button_clone_next);
    }
}





