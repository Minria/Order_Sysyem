版本迭代

V3：更改技术栈为Spring+Mybatis

V2：修复了因为主键绑定而无法删除菜品的问题

V1： 上线

支持用户的注册、登录、点餐

支持商家添加菜品、删除菜品、处理订单

# 1. 项目简介

## 1.1 项目背景

该项目就是为了顾客方便通过手机来进行在线点餐。

## 1.2 模块

1、对用户的管理

2、对菜品的管理

3、对订单的管理

## 1.3 技术栈

SpringBoot+SpringMVC+Mybatis

# 2. 需求分析

## 2.1 用户管理

注册、登录、注销

需要区分顾客与商家，因为他们的操作不相同

## 2.2 菜品管理

能够对菜品进行增删查改

对于顾客：只能查看

对于商家：可以进行增删查改

## 2.3 订单管理

对订单进行增删查改

对于顾客：新增订单以及查看自己过去订单

对于商家：查看所有订单，修改订单状态

# 3. 数据库设计

## 3.1 对于用户需要有

id，用户名，密码，还要有一个区分是商家还是顾客

```sql
create table user (
    userId int primary key auto_increment,
    name varchar(50) unique,
    password varchar(50),
    isAdmin int  -- 是否是管理员, 0 表示不是管理员, 1 表示是管理员
);
```

## 3.2 对于菜品需要有

id，名称，价格

```sql
create table dishes (
    dishId int primary key auto_increment,
    name varchar(50),
    price int  -- 以分为单位, 使用 int 表示. 尽量不要用 float double, 会存在误差
    -- 后面优化增加了菜品状态
);
```

需要注意的价格为多少分，不可用float以及double，由于IEEE754格式，导致浮点相加容易出现错误

## 3.3 对于订单管理

我们需要有两张表，一张关联用户订单，一张关联订单和菜品

对于用户订单表

订单id，用户id，下单时间，是否完成

```sql
create table order_user (
    orderId int primary key auto_increment,
    userId int,    -- 这个用户 id 需要和 user 表中的 userId 具有关联关系
    time datetime, -- 下单时间
    isDone int,    -- 1 表示订单完结, 0 表示订单未完结.
    foreign key(userId) references user(userId)
);
```

对于订单菜品表

订单id，菜品id

```sql
create table order_dish (
    orderId int,   -- orderId 也和 order_user 表的 orderId 字段有外键关系
    dishId int,    -- dishId 也和 dishes 表中的 dishId 存在外键关系
    foreign key(orderId) references order_user(orderId),
    foreign key(dishId) references dishes(dishId)
);
```

# 4. 数据库操作

## 4.1 对用户操作

```java
@Mapper
public interface UserMapper {
    int add(User user);
    int delete(int id);
    User selectById(int id);
    User selectByName(String name);
}
```

## 4.2 对菜品的操作

```java
@Mapper
public interface DishMapper {
    int add(Dish dish);
    int update(int id,int state);
    List<Dish> selectAll();
    Dish selectById(int id);
    Dish selectByName(String name);
    List<Dish> findDish(int orderId);
}
```

## 4.3 对订单的操作

```java
@Mapper
public interface OrderMapper {
    int addOrderUser(Order order);
    int addOrderDish(Order order);
    int deleteOrderUser(int orderId);
    List<Order> selectAll();
    List<Order> selectByUserId(int id);
    int changeState(int orderId, int isDone);
    Order buildOrder(int orderId);
}
```

1、新增订单

在前文中说明，一个订单是由两个表来完成的。分别是`order_user`和`order_dish`

所以新增一个订单就需要往两个数据库中填表

需要注意的，先添加`order_user`,这时得到自增id`orderId`然后用与添加到`order_dish`中

也需要注意的是，如果第二表添加失败，抛出异常，就需要删除`order_user`表中刚才添加的（调用`deleteOrderUser`）

2、查看订单详情

第一步构建订单，使用`buildOrder`函数得到一个订单，这时我们的点单的基本信息已经完成，就只有里面的菜品没有添加，在调用`findDish`函数得到一个线性表，将其插入订单中即可完成一个订单

# 5. 前后端API的约定

前后端采用JSON来构造相应格式

前端发给后端一个请求字符串，通过谷歌gson转化为一个请求类

后端构造一个响应类，通过谷歌gson转化为一个响应字符串

## 5.1 用户操作

**用户注册**

```sql
//请求
POST/register
{
	name:xxx,
	password:xxx
}
//响应
HTTP/1.1 200 OK
{
	ok:1,
	reason:xxx
}
//后端接口
public Object register(@RequestBody User user)
```

构造注册用户名以及密码，通过字符串发送后端

后端处理：由于用户名的唯一性，所以需要与已有用户名进行对比。

同时需要对账号密码进行判空处理

如果已经存在则注册成功，反之，则注册失败。

**用户登录**

```
POST/login
{
	name:xxx,
	password:xxx
}
HTTP/1.1 200 OK
{
	ok:1,//1表示成功
	reason:xxx,//失败的时候返回原因
	name:xxx,
	isAdmin:0 //0表示普通用户，1表示管理员
}
public Object login(@RequestBody User user)
```

构造登录用户名以及密码

先通过用户名查找用户是否存在，不存在则登录失败

用户存在则需要对比密码是否相同，不相同则登录失败

**检查登录状态**

```
GET/login
{
}
{
	ok:1,
	reason:xxx,
	name:xxx,
	isAdmin:0
}
public Object isLogin(HttpServlet req)
```

首先判断是否存在session，如果不存在，说明没有登录，返回登录页面

**用户注销**

```
GET/logout
{
}
{
	ok:1,
	reason:xxx
}
```

检查session是否存在，如果不存在就说明没有登录，如果存在，那就删除

## 5.2 对菜品的管理

 **新增菜品**

管理员权限

```
POST/dish
{
	name:xxx,
	price:xxx,
}
{
	ok:1,
	reason:xxx
}
```

名称以及价格构造菜品类然后加入数据库

需要注意的是，在一家店中通常一个菜名对应一种菜

如果新增的菜和已有菜名重复，那就添加失败

如果新增菜品和以前删除的菜名和价格相同，那就更改原先的菜品状态

**删除菜品**

管理员权限

```
dish/delete?dishId=xxx
{
}
{
	ok:1,
	reason:xxx
}
```

**查看所有菜品**

```
GET/dish/getDish
{
}
{
	{
		dishId:xxx,
		name:xxx,
		price:xxx
	},{
		dishId:xxx,
		name:xxx,
		price:xxx	
	}
}
```

## 5.3 订单管理

**新增订单**

```
POST/order
{
	[1,2,3,4]
}
{
	ok:1,
	reason:xxx
}
```

**查看订单**

根据身份来判断操作

管理员和用户都可以进行查看订单，管理员可以查看所有订单，而用户只能查看自己的订单

由于使用统一函数接口，所以权限只拦截到登录状态

在内部判断管理员的操作和普通用户的操作不同

```
GET/order
{
}
{
	{
		orderId:xxx,
		userId:xxx,
		time:xxx,
		isDone:xxx,
	}
}
```

**查看指定订单详情**

```
GET/order?orderId=xxx
{
}
{
	orderId:xxx,
	userId:xxx,
	time:xxx,
	isDone:xxx,
	dishes:xxx
}
```

**修改订单状态**

管理员操作

```	
GET/order/changeState?orderId=xxx&isDone=1
{
}
{
	ok:1,
	reason:xxx
}
```

## 5.4 权限配置

```java
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin-dish.html")
                .addPathPatterns("/admin-order.html")
                .addPathPatterns("/dish/add")
                .addPathPatterns("/dish/delete")
                .addPathPatterns("/order/changeState");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/order/add")
           		.addPathPatterns("/order/findOrders");
    }
}
```



# 6. BUG-FREE

## 6.1 添加菜品

在进行添加order_user时，在添加成功后，需要得到自动生成的`orderId`所以需要如下实现

```java
//该版本为V1版本代码---使用JDBC编程
PreparedStatement statement =null;
ResultSet resultSet = null;
String sql = "xxxxxx";
try {
    assert connection != null;
    statement= connection.prepareStatement(sql);
    //下面是修改后
    //statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    statement.setInt(1,order.getUserId());
    int ret = statement.executeUpdate();
    if(ret!=0){
        resultSet=statement.getGeneratedKeys();
        if(resultSet.next()){
            order.setOrderId(resultSet.getInt(1));
        }
        System.out.println("插入订单第一步成功");
    }else{
        System.out.println("新增失败");
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    DBUtil.close(connection,statement,resultSet);
}
```

执行后报错

```java
java.sql.SQLException: Generated keys not requested.
```

错误原因：

<font color="red">**获取预编译块的时候未获取带有主键返回策略的预编译块**</font>

解决方式：

获取`preparedStatement`的时候多一个参数`Statement.RETURN_GENERATED_KEYS`就可以了

````java
statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
````

## 6.2 在订单完成后无法删除或者更新菜品

在已有订单选中菜品时，我们执行删除操作，**虽然在页面时提示成功**，但是在控制台查看提示

抛出异常：**绑定外键后执行删除或者更新操作不可进行**

PS:	改完代码后查看前端时发现，发现这个项目在设计时就已经确定绑定主键后不可删除。我在后续实现的时候没有触发前端页面这个选项。

解决方法如下：

1、那我们可以取消外键，成功使用`delete`方式将菜品删除，但是我们在查看订单时也会这个菜品的记录会消失不见。得不偿失，不建议采用

2、我们就需要给菜品设定一个状态，表示现在存在，还是现在不存在。暂时没有说明缺点。

```sql
create table dishes (
    dishId int primary key auto_increment,
    name varchar(50),
    price int  -- 以分为单位, 使用 int 表示. 尽量不要用 float double, 会存在误差
    state int  -- 0表示存在，1表示下架删除。
);
```

这样就可以区分存在，以及下架。

当我们查看所有菜品时就选择状态为0的

当我们删除菜品的时候我们将状态置为1

当我们查询过去的订单时，这和状态没有关系，仍然可以查看被我们“删除”的的菜品

## 6.3 后端不能识别前端发送的字符串

使用JSON字符串进行前后端交互是本项目使用的方式

在使用Servlet实现项目的时候

```
{"name":"xxx","password":"xxxx"}
```

```java
public void func(HttpServletRequest req,HttpServletResponse resp){
    //设置请求的格式utf-8
    //设置返回的格式为application/json
    //得到参数
    String name=req.getParementer("name");
    String password=req.getParementer("password");
   
}
```

但是在Spring中

```java
//接受一个json字符串并将其转换为一个对象
public Object func(@RequestBody User user);
```

但是不能接受的信息

通过POSTMAN进行模拟测试post的body中添加字符串

`{"name":"xxx","password":"xxxx"}`可以得到正常的响应

通过fiddle进行抓包后发现，vue对我们的json字符串发送的格式为`x-www-form-urlencoded`

通过查询发现，vue对字符串的处理格式默认为上述

修改格式为`application/json`后后端可以正常接受参数

# 7. 代码优化

## 7.1 对繁琐代码进行优化

在我们执行删除操作时

```java
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session= req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="没有登录";
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                response.ok=0;
                response.reason="没有登录";
                System.out.println("没有登录");
            }else{
                if(user.getIsAdmin()==0){
                    response.ok=0;
                    response.reason="不是管理员";
                    System.out.println("不是管理员");
                }else{
                    String dishStrId=req.getParameter("dishId");
                    System.out.println("字符串格式->"+dishStrId);
                    if(dishStrId==null){
                        response.ok=0;
                        response.reason="参数不正确";
                    }else{
                        int id=Integer.parseInt(dishStrId);
                        System.out.println("数字格式->"+dishStrId);
                        DishDao.delete(id);
                        response.ok=1;
                        response.reason="";
                    }
                }
            }
        }
        String jsonString = gson.toJson(response);
        resp.getWriter().write(jsonString);
    }
```

每一个情况我们都需要来写出response.ok=？和response.reason=？我们可以自定义一个异常，然后在catch中捕捉到异常后进行统一处理。

```java
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        try{
            HttpSession session= req.getSession(false);
            if(session==null){
                throw new OrderSystemException("没有登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            if(user.getIsAdmin()==0){
                throw new OrderSystemException("您不是管理员");
            }
            String dishStrId=req.getParameter("dishId");
            System.out.println("字符串格式->"+dishStrId);
            if(dishStrId==null){
                throw new OrderSystemException("参数不正确");
            }
            int id=Integer.parseInt(dishStrId);
            System.out.println("数字格式->"+dishStrId);
            DishDao.delete(id);
            response.ok=1;
            response.reason="";
        }catch (Exception e){
            System.out.println("DishServlet_doDelete:");
            System.out.println(e.getMessage());
            e.printStackTrace();
            response.ok=0;
            response.reason=e.getMessage();
        } finally {
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
```

如上改进后虽然代码没有减少太多

但是如果在dao层也使用异常抛出，那么我们在管理员页面进行删除的时候就可以提示删除失败

```
删除菜品失败! Cannot delete or update a parent row: a foreign key constraint fails (`order_system`.`order_dish`, CONSTRAINT `order_dish_ibfk_2` FOREIGN KEY (`dishId`) REFERENCES `dishes` (`dishId`))
```

而不是删除成功，但实际上并没有删除成功。

-----------------------------------------------------------------------------------------------------------------------------------------------------------

PS：在使用SpringBoot重写项目的时候，可以使用统一异常处理，和统一返回。但是我们可以看见，很多函数的返回的结构并不相同，这里就不在使用统一异常处理和统一返回。
