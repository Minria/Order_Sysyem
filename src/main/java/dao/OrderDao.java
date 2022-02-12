package dao;

import order_system_util.DBUtil;
import entity.Dish;
import entity.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class OrderDao {

    public static void add(Order order){
        addOrderUser(order);
        addOrderDish(order);
    }

    private static void addOrderUser(Order order){
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement =null;
        String sql = "insert into order_user values (null,?,now(),0)";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setInt(1,order.getUserId());
            int ret = statement.executeUpdate();
            if(ret!=0){
                System.out.println("新增成功");
                return;
            }
            System.out.println("新增失败");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }
    private static void addOrderDish(Order order){
        Connection connection = DBUtil.getConnection();
        String sql = "insert into order_dish values(?, ?)";
        PreparedStatement statement = null;
        try {
            assert connection != null;
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);
            List<Dish> dishes = order.getDishes();
            for (Dish dish : dishes)  {
                statement.setInt(1, order.getOrderId());
                statement.setInt(2, dish.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            deleteOrderUser(order.getOrderId());
        } finally {
            DBUtil.close(connection, statement, null);
        }
    }

    private static void deleteOrderUser(int orderId) {
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        String sql = "delete from order_user where orderId = ?";
        try {
            assert connection != null;
            statement=connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            int ret = statement.executeUpdate();
            if(ret!=0){
                System.out.println("删除成功");
                return;
            }
            System.out.println("删除失败");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }
    public static List<Order> selectAll(){
        List<Order> orders=new ArrayList<>();
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "select * from order_user";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            resultSet=statement.executeQuery();
            while(resultSet.next()){
                Order order=new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return orders;
    }
    public static List<Order> selectByUserId(int userId){
        List<Order> orders =new ArrayList<>();
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "select * from order_user where userId = ?";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setInt(1,userId);
            resultSet=statement.executeQuery();
            while(resultSet.next()){
                Order order=new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return orders;
    }

    public static Order selectById(int orderId)  {

        Order order = buildOrder(orderId);
        List<Integer> dishIds = selectDishIds(orderId);
        getDishDetail(order, dishIds);
        return order;
    }


    private static Order buildOrder(int orderId) {
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_user where orderId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    private static List<Integer> selectDishIds(int orderId) {
        List<Integer> dishIds = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_dish where orderId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dishIds.add(resultSet.getInt("dishId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return dishIds;
    }

    private static void getDishDetail(Order order, List<Integer> dishIds)  {
        List<Dish> dishes = new ArrayList<>();
        for (Integer dishId : dishIds) {
            Dish dish = DishDao.selectById(dishId);
            dishes.add(dish);
        }
        order.setDishes(dishes);
    }

    public static void changeState(int orderId, int isDone)  {
        Connection connection = DBUtil.getConnection();
        String sql = "update order_user set isDone = ? where orderId = ?";
        PreparedStatement statement = null;
        try {
            assert connection != null;
            statement = connection.prepareStatement(sql);
            statement.setInt(1, isDone);
            statement.setInt(2, orderId);
            int ret = statement.executeUpdate();
            if (ret != 1) {
                System.out.println("修改订单状态失败");
                return;
            }
            System.out.println("修改订单状态成功");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("修改订单状态失败");
        } finally {
            DBUtil.close(connection, statement, null);
        }
    }
}
