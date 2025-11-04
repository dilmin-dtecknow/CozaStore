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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProdcts", urlPatterns = {"/SearchProdcts"})
public class SearchProdcts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //get request json
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //get all Product
        Criteria criteria1 = session.createCriteria(Product.class);
        Criteria criteria3 = session.createCriteria(Category.class);
        if (requestJsonObject.has("main_category_name")) {
            String main_category_name = requestJsonObject.get("main_category_name").getAsString();
//            System.out.println(main_category_name);

            //Get category List from Db
            Criteria criteria2 = session.createCriteria(Main_Category.class);
            criteria2.add(Restrictions.eq("name", main_category_name));
            Main_Category main_Category = (Main_Category) criteria2.uniqueResult();

            //Filter Model by Category List from Db
            criteria3.add(Restrictions.eq("main_category", main_Category));
            List<Category> categoryList = criteria3.list();

            if (!categoryList.isEmpty()) {
                criteria1.add(Restrictions.in("category", categoryList));
            } else {
                responseJsonObject.addProperty("message", "No Product Longer");
            }
        }

        if (requestJsonObject.has("sub_category_name")) {
            String sub_category_name = requestJsonObject.get("sub_category_name").getAsString();
//            System.out.println(sub_category_name);

            Criteria criteria4 = session.createCriteria(Sub_Category.class);
            criteria4.add(Restrictions.eq("name", sub_category_name));
            Sub_Category sub_Category = (Sub_Category) criteria4.uniqueResult();

//            responseJsonObject.add("sub_Category", gson.toJsonTree(sub_Category));
            //Filter Model by Category List from Db
//            Criteria criteria5 = session.createCriteria(Category.class);
            criteria3.add(Restrictions.eq("sub_category", sub_Category));
            List<Category> categoryList = criteria3.list();

//            responseJsonObject.add("categoryList", gson.toJsonTree(categoryList));
            if (!categoryList.isEmpty()) {
                criteria1.add(Restrictions.in("category", categoryList));
            } else {
                responseJsonObject.addProperty("message", "No Product Longer");
            }
        }

        //brand
        if (requestJsonObject.has("brand_name")) {
            String brand_name = requestJsonObject.get("brand_name").getAsString();
//            System.out.println(brand_name);

            Criteria criteria5 = session.createCriteria(Brand.class);
            criteria5.add(Restrictions.eq("name", brand_name));
            Brand brand = (Brand) criteria5.uniqueResult();

            //product filter by brand
            criteria1.add(Restrictions.eq("brand", brand));
        }

        //size
        if (requestJsonObject.has("size_name")) {
            String size_name = requestJsonObject.get("size_name").getAsString();
//            System.out.println(size_name);

            Criteria criteria6 = session.createCriteria(Size.class);
            criteria6.add(Restrictions.eq("name", size_name));
            Size size = (Size) criteria6.uniqueResult();

            //product filter by size
            criteria1.add(Restrictions.eq("size", size));
        }

        //color
        if (requestJsonObject.has("color_name")) {
            String color_name = requestJsonObject.get("color_name").getAsString();
//            System.out.println(color_name);

            Criteria criteria7 = session.createCriteria(Color.class);
            criteria7.add(Restrictions.eq("name", color_name));
            Color color = (Color) criteria7.uniqueResult();

            //product filter by color
            criteria1.add(Restrictions.eq("color", color));
        }

        //serach by price
        double price_range_start = requestJsonObject.get("price_range_start").getAsDouble();
        double price_range_end = requestJsonObject.get("price_range_end").getAsDouble();

        criteria1.add(Restrictions.ge("price", price_range_start));
        criteria1.add(Restrictions.le("price", price_range_end));

        String sort_text = requestJsonObject.get("sort_text").getAsString();
//        System.out.println(sort_text);

        if (sort_text.equals("Name, A to Z")) {
            criteria1.addOrder(Order.asc("title"));
        } else if (sort_text.equals("Name, Z to A")) {
            criteria1.addOrder(Order.desc("title"));

        } else if (sort_text.equals("Product, New to Old")) {
            criteria1.addOrder(Order.desc("id"));
        } else if (sort_text.equals("Product, Old to New")) {
            criteria1.addOrder(Order.asc("id"));
        } else if (sort_text.equals("Price, low to high")) {
            criteria1.addOrder(Order.asc("price"));
        } else if (sort_text.equals("Price, high to low")) {
            criteria1.addOrder(Order.desc("price"));
        } else if (sort_text.equals("Brand, A to Z")) {
            criteria1.createAlias("brand", "b");
            criteria1.addOrder(Order.asc("b.name"));
        } else if (sort_text.equals("Brand, Z to A")) {
            // Sorting by brand name in descending order
            criteria1.createAlias("brand", "b");
            criteria1.addOrder(Order.desc("b.name"));
        }

        responseJsonObject.addProperty("allProductCount", criteria1.list().size());

        //set product range
        int firstResult = requestJsonObject.get("firstResult").getAsInt();
        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(6);

        //get product list
        List<Product> productList = criteria1.list();

        for (Product product : productList) {
            product.setUser(null);
        }

        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));
        System.out.println(gson.toJson(responseJsonObject));
    }

}
