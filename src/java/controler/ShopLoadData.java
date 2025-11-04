package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Category;
import entity.Color;
import entity.Main_Category;
import entity.Product;
import entity.Size;
import entity.Sub_Category;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@WebServlet(name = "ShopLoadData", urlPatterns = {"/ShopLoadData"})
public class ShopLoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        //get main category from db
        Criteria criteria1 = session.createCriteria(Main_Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Main_Category> main_CategoryList = criteria1.list();
        jsonObject.add("main_CategoryList", gson.toJsonTree(main_CategoryList));

        //get sub category from db
        Criteria criteria2 = session.createCriteria(Sub_Category.class);
        criteria2.addOrder(Order.asc("name"));
        List<Sub_Category> sub_CategorysList = criteria2.list();
        jsonObject.add("sub_CategorysList", gson.toJsonTree(sub_CategorysList));

        //get Category from db
        Criteria criteria3 = session.createCriteria(Category.class);
        criteria3.addOrder(Order.asc("id"));
        List<Category> categoryList = criteria3.list();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));

        //get color from db
        Criteria criteria4 = session.createCriteria(Color.class);
        criteria4.addOrder(Order.asc("name"));
        List<Color> colorList = criteria4.list();
        jsonObject.add("colorList", gson.toJsonTree(colorList));

        //get size from db
        Criteria criteria5 = session.createCriteria(Size.class);
        criteria5.addOrder(Order.asc("name"));
        List<Size> sizeList = criteria5.list();
        jsonObject.add("sizeList", gson.toJsonTree(sizeList));

        //get brand from db 
        Criteria criteria6 = session.createCriteria(Brand.class);
        criteria6.addOrder(Order.asc("name"));
        List<Brand> brandList = criteria6.list();
        jsonObject.add("brandList", gson.toJsonTree(brandList));
        
        //get product from db
        Criteria criteria7 = session.createCriteria(Product.class);
        // latest product
        criteria7.addOrder(Order.desc("id"));
        //all product count
        jsonObject.addProperty("allProductCount", criteria7.list().size());
        
        //set product range
        criteria7.setFirstResult(0);
        criteria7.setMaxResults(6);
        
        List<Product> productlist = criteria7.list();
        
        for (Product product : productlist) {
            product.setUser(null);
        }

        jsonObject.add("productList", gson.toJsonTree(productlist));
        jsonObject.addProperty("success", true);
        //main code
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
    }

}
