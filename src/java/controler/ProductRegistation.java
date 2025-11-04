package controler;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Brand;
import entity.Category;
import entity.Color;
import entity.Main_Category;
import entity.Product;
import entity.Product_Status;
import entity.Size;
import entity.Sub_Category;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "ProductRegistation", urlPatterns = {"/ProductRegistation"})
public class ProductRegistation extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        String mainCategoryId = request.getParameter("mainCategoryId");
        String productSubCategoryId = request.getParameter("productSubCategoryId");
        String title = request.getParameter("productName");
        String description = request.getParameter("productDescription");
        String sizeId = request.getParameter("sizeId");
        String colorId = request.getParameter("colorId");
        String brandId = request.getParameter("brandId");
        String price = request.getParameter("productPrice");
        String quantity = request.getParameter("quantity");

        Part image1 = request.getPart("image1");
        Part image2 = request.getPart("image2");
        Part image3 = request.getPart("image3");

        if (!Validations.isInteger(mainCategoryId)) {
            response_DTO.setContent("Invalide MainCategory");
        } else if (!Validations.isInteger(productSubCategoryId)) {
            response_DTO.setContent("Invalide Subcategory");
        } else if (!Validations.isInteger(sizeId)) {
            response_DTO.setContent("Invalide Size");
        } else if (!Validations.isInteger(colorId)) {
            response_DTO.setContent("Invalide Colour");
        } else if (!Validations.isInteger(brandId)) {
            response_DTO.setContent("Invalide Brand");
        } else if (Integer.parseInt(mainCategoryId) == 0) {
            response_DTO.setContent("Please Select Main Category(Gender)");
        } else if (Integer.parseInt(productSubCategoryId) == 0) {
            response_DTO.setContent("Please Select Sub Category(Product)");
        } else if (title.isEmpty()) {
            response_DTO.setContent("Please fill Product Tile");
        } else if (description.isEmpty()) {
            response_DTO.setContent("Please fill Product Description");
        } else if (price.isEmpty()) {
            response_DTO.setContent("Please fill Product Price");
        } else if (!Validations.isDouble(price)) {
            response_DTO.setContent("Ivalide Price");
        } else if (Double.parseDouble(price) <= 0) {
            response_DTO.setContent("Price must be greater than 0");
        } else if (Integer.parseInt(brandId) == 0) {
            response_DTO.setContent("Please Select a Brand");
        } else if (Integer.parseInt(sizeId) == 0) {
            response_DTO.setContent("Please Select a Size");
        } else if (Integer.parseInt(colorId) == 0) {
            response_DTO.setContent("Please Select Color");
        } else if (quantity.isEmpty()) {
            response_DTO.setContent("Please fill Product Quantity");
        } else if (!Validations.isInteger(quantity)) {
            response_DTO.setContent("Ivalide Quantity");
        } else if (Double.parseDouble(quantity) <= 0) {
            response_DTO.setContent("quantity must be greater than 0");
        } else if (image1.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload Image1");
        } else if (image2.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload Image2");
        } else if (image3.getSubmittedFileName() == null) {
            response_DTO.setContent("Please upload Image3");
        } else {

            Main_Category main_Category = (Main_Category) session.get(Main_Category.class, Integer.parseInt(mainCategoryId));
            if (mainCategoryId == null) {
                response_DTO.setContent("Please Select valide Main Category(Gender)");
            } else {

                Sub_Category sub_Category = (Sub_Category) session.get(Sub_Category.class, Integer.parseInt(productSubCategoryId));
                if (sub_Category == null) {
                    response_DTO.setContent("Please Select valide Sub Category(Product)");
                } else {

                    // Check if the combination of mainCategoryId and productSubCategoryId exists in the Category table
                    Criteria criteria = session.createCriteria(Category.class);
                    criteria.add(Restrictions.eq("main_category", main_Category));
                    criteria.add(Restrictions.eq("sub_category", sub_Category));

                    List<Category> categoryList = criteria.list();

                    if (categoryList.isEmpty()) {
                        response_DTO.setContent("Invalid Main Category and Sub Category combination");
                    } else {
                        // combination success
                        Brand brand = (Brand) session.get(Brand.class, Integer.parseInt(brandId));

                        if (brand == null) {
                            response_DTO.setContent("Please Select valide Brand");
                        } else {

                            Size size = (Size) session.get(Size.class, Integer.parseInt(sizeId));

                            if (size == null) {
                                response_DTO.setContent("Please Select valide Size");
                            } else {

                                Color color = (Color) session.get(Color.class, Integer.parseInt(colorId));

                                if (color == null) {
                                    response_DTO.setContent("Please Select valide Color");
                                } else {
                                    Category categoryId = (Category) criteria.uniqueResult();
                                    Product_Status product_Status = (Product_Status) session.get(Product_Status.class, 1);

                                    Product product = new Product();
                                    product.setBrand(brand);
                                    product.setCategory(categoryId);
                                    product.setColor(color);
                                    product.setDate_time(new Date());
                                    product.setDescription(description);
                                    product.setPrice(Double.parseDouble(price));
                                    product.setProduct_status(product_Status);
                                    product.setQty(Integer.parseInt(quantity));
                                    product.setSize(size);
                                    product.setTitle(title);

                                    User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
                                    Criteria criteria1 = session.createCriteria(User.class);
                                    criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                                    User user = (User) criteria1.uniqueResult();
                                    product.setUser(user);

                                    //insert
                                    int pid = (int) session.save(product);
                                    session.beginTransaction().commit();
                                    
                                    //3 img saving
                                    String applicationPath = request.getServletContext().getRealPath("");
                                    String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
                                       System.out.println(newApplicationPath);
                                    File folder = new File(newApplicationPath + "//product-images//" + pid);
//                                    if (!folder.exists()) {
//                                        folder.mkdir();
//                                    }
                                    folder.mkdir();

                                    File file1 = new File(folder, "image1.png");
                                    InputStream inputStream1 = image1.getInputStream();
                                    Files.copy(
                                            inputStream1,
                                            file1.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING
                                    );

                                    File file2 = new File(folder, "image2.png");
                                    InputStream inputStream2 = image2.getInputStream();
                                    Files.copy(
                                            inputStream2,
                                            file2.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING
                                    );

                                    File file3 = new File(folder, "image3.png");
                                    InputStream inputStream3 = image3.getInputStream();
                                    Files.copy(
                                            inputStream3,
                                            file3.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING
                                    );

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("New Product Added");
                                }
                            }
                        }
                    }

                }
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
        session.close();
    }

}
