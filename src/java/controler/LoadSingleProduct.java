package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Brand;
import entity.Category;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO response_DTO = new Response_DTO();

        String productId = request.getParameter("id");

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            if (Validations.isInteger(productId)) {
                Product product = (Product) session.get(Product.class, Integer.parseInt(productId));
                product.getUser().setPassword(null);
                product.getUser().setVerification(null);
                product.getUser().setEmail(null);

                //category id = product.category id
                // Get the category id from the product
                Category category = product.getCategory(); // Assuming there is a relationship like this
                Integer categoryId = null;
                if (category != null) {
                    categoryId = category.getId(); // Get the category id
                }

                Criteria criteria2 = session.createCriteria(Product.class);
                criteria2.add(Restrictions.eq("category.id", categoryId));
                criteria2.add(Restrictions.ne("id", product.getId())); 
                List<Product> productList = criteria2.list();
                
                Criteria criteria3 = session.createCriteria(Brand.class);
                
                List<Brand> brandList = criteria3.list();
                
                Criteria criteria4 = session.createCriteria(Product.class);
                criteria4.add(Restrictions.eq("brand", product.getBrand()));
                criteria4.add(Restrictions.ne("id", product.getId())); 
                List<Product> productByBrandList = criteria4.list();

                
                
                // Prepare response JSON object
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("categoryId", categoryId); // Add the category id to the response
                jsonObject.add("product", gson.toJsonTree(product)); // Add product details to the response

                jsonObject.add("productList", gson.toJsonTree(productList));
                jsonObject.add("productByBrandList", gson.toJsonTree(productByBrandList));
               

                // Set the response type and write the JSON object
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(jsonObject));
                System.out.println(gson.toJson(jsonObject));
            }else{
                response_DTO.setContent("Product Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
