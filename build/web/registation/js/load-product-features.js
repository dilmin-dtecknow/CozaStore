var categoryList;

async function loadFeaturs() {
    const response = await fetch("LoadProductFeatures");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const brandList = json.brandList;
        const main_CategoryList = json.main_CategoryList;
        const sub_CategoryList = json.sub_CategoryList;
        categoryList = json.categoryList; // Store categoryList for later use
        const colorList = json.colorList;
        const sizeList = json.sizeList;

        loadSelect("productCategory", main_CategoryList, "name"); // Load main categories
        loadSelect("brandSelect", brandList, "name");
        loadSelect("sizeSelect", sizeList, "name");
        loadSelect("colorSelect", colorList, "name");

        // Add event listener for main category selection change
        document.getElementById("productCategory").addEventListener("change", updateModels);

    } else {
        Swal.fire({
            title: 'Sorry',
            text: 'Something went wrong. Please try again later!',
            icon: 'error',
            confirmButtonText: 'OK'
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

function clearSelect(selectTagId) {
    const SelectTag = document.getElementById(selectTagId);
    SelectTag.innerHTML = ''; // Clear all options
}

function updateModels() {
    let subCategorySelect = document.getElementById("productSubCategory");

    // Clear previous options
    clearSelect("productSubCategory");

    let selectedMainCategoryId = document.getElementById("productCategory").value;

    categoryList.forEach(category => {
        if (category.main_category.id == selectedMainCategoryId) {
            let optionTag = document.createElement("option");
            optionTag.value = category.sub_category.id;
            optionTag.innerHTML = category.sub_category.name;
            subCategorySelect.appendChild(optionTag);
        }
    });
}

async function registerProduct() {
    const mainCategory = document.getElementById("productCategory");
    const productSubCategory = document.getElementById("productSubCategory");
    const productName = document.getElementById("productName");
    const productDescription = document.getElementById("productDescription");
    const productPrice = document.getElementById("productPrice");
    const brandSelect = document.getElementById("brandSelect");
    const sizeSelect = document.getElementById("sizeSelect");
    const colorSelect = document.getElementById("colorSelect");
    const quantity = document.getElementById("quantity");
    const image1Tag = document.getElementById("image1");
    const image2Tag = document.getElementById("image2");
    const image3Tag = document.getElementById("image3");

    const data = new FormData();
    data.append("mainCategoryId", mainCategory.value);
    data.append("productSubCategoryId", productSubCategory.value);
    data.append("productName", productName.value);
    data.append("productDescription", productDescription.value);
    data.append("productPrice", productPrice.value);
    data.append("brandId", brandSelect.value);
    data.append("sizeId", sizeSelect.value);
    data.append("colorId", colorSelect.value);
    data.append("quantity", quantity.value);

    data.append("image1", image1Tag.files[0]);
    data.append("image2", image2Tag.files[0]);
    data.append("image3", image3Tag.files[0]);

    const response = await fetch("ProductRegistation",
            {
                method: "POST",
                body: data
            }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {

            Swal.fire({
                title: 'Success',
                text: json.content,
                icon: 'success',
                confirmButtonText: 'OK'
            });
            window.location.reload();
        } else {
            Swal.fire({
                title: 'Error',
                text: json.content,
                icon: 'error',
                confirmButtonText: 'OK'
            });
        }
    } else {
        Swal.fire({
            title: 'Sorry',
            text: 'Something went wrong. Please try again later!',
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}