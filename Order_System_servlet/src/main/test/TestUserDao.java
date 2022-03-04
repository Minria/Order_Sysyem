import dao.UserDao;
import entity.User;

public class TestUserDao {

    public static void main3(String[] args) {
        UserDao.delete(2);
    }
    public static void main(String[] args) {
        User user=UserDao.selectById(2);
        System.out.println(user);
    }


    public static void main1(String[] args) {
//        User user=new User();

//        user.setName("wangfuming");
//        user.setPassword("123");
//        user.setIdAdmin(1);

//        user.setName("wfm");
//        user.setPassword("qwe123456");
//        user.setIdAdmin(0);
//
//        System.out.println(user);
//        UserDao.add(user);
    }
}
