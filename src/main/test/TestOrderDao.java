import dao.OrderDao;
import entity.Dish;
import entity.Order;
import order_system_util.OrderSystemException;

import java.util.ArrayList;
import java.util.List;

public class TestOrderDao {
    public static void main2(String[] args) throws OrderSystemException {
        OrderDao.deleteOrderUser(6);
    }
    public static void main(String[] args) throws OrderSystemException {
        Order order=new Order();
        order.setUserId(1);
        Dish dish1=new Dish();
        dish1.setDishId(11);
        dish1.setName("扬州炒饭");
        dish1.setPrice(1100);
        Dish dish2=new Dish();
        dish2.setDishId(12);
        dish2.setName("蛋炒饭");
        dish2.setPrice(1200);
        List<Dish> dishes=new ArrayList<>();
        dishes.add(dish1);
        dishes.add(dish2);
        order.setDishes(dishes);
        OrderDao.add(order);
    }
}
